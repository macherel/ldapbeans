/*
 * This file is part of ldapbeans
 *
 * Released under LGPL
 *
 * ldapbeans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ldapbeans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ldapbeans.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2010 Bruno Macherel
 */
package ldapbeans.bean;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_5;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import ldapbeans.annotation.LdapAttribute;
import ldapbeans.util.LdapbeansConfiguration;
import ldapbeans.util.i18n.LdapbeansMessageManager;
import ldapbeans.util.i18n.Logger;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public final class LdapBeanClassManager {

    /** Instance of the logger for this class */
    private final static Logger LOG = Logger.getLogger();

    /** Singleton instance of this class */
    private final static LdapBeanClassManager INSTANCE;

    /** Message manager instance */
    private final static LdapbeansMessageManager MESSAGE = LdapbeansMessageManager
	    .getInstance();

    /** the configuration */
    private final static LdapbeansConfiguration CONFIG = LdapbeansConfiguration
	    .getInstance();

    static {
	INSTANCE = new LdapBeanClassManager();
    }

    /**
     * Return the singleton instance of this class
     * 
     * @return The singleton instance of this class
     */
    public static LdapBeanClassManager getInstance() {
	return INSTANCE;
    }

    /** Counter of generated classes */
    private int m_Count;

    private final Map<String, Class<?>> m_GeneratedClasses;

    private final LdapBeanClassLoader m_ClassLoader;

    /**
     * Default constructor. This class can not be instantiated.
     */
    private LdapBeanClassManager() {
	m_Count = 0;
	m_GeneratedClasses = new HashMap<String, Class<?>>();
	m_ClassLoader = new LdapBeanClassLoader();
    }

    /**
     * Return generated LdapBean class that implements interfaces passed in
     * parameter.
     * 
     * @param p_Interfaces
     *            Interfaces that the generated class have to implement
     * @return A generated class
     */
    public Class<?> getClass(Class<?>[] p_Interfaces) {

	Class<?> result;
	String key = getClassKey(p_Interfaces);

	synchronized (m_GeneratedClasses) {
	    result = m_GeneratedClasses.get(key);
	    if (result == null) {
		result = generateClass(p_Interfaces);
		m_GeneratedClasses.put(key, result);
	    }
	}
	return result;
    }

    /**
     * Generate new class that implements all interfaces
     * 
     * @param p_Interfaces
     *            The interfaces to implement
     * @return The new Class
     */
    private Class<?> generateClass(Class<?>[] p_Interfaces) {

	String tmp = "";
	int n = m_Count++;
	do {
	    tmp += (char) ('A' + n % 10);
	    n /= 10;
	} while (n != 0);

	String className = "LdapBeanGenerated" + tmp;

	String superClass = Object.class.getName().replace('.', '/');
	String[] interfaces = new String[p_Interfaces.length];
	for (int i = 0; i < p_Interfaces.length; i++) {
	    interfaces[i] = p_Interfaces[i].getName().replace('.', '/');
	}

	ClassWriter cw = new ClassWriter(COMPUTE_MAXS + COMPUTE_FRAMES);
	cw.visit(V1_5, ACC_PUBLIC, "ldapbeans/bean/" + className, null,
		superClass, interfaces);
	// Create fields
	generateField(cw);
	// create constructor
	generateConstructor(cw, className);
	// Create method
	Set<String> generatedMethod = new HashSet<String>();
	for (Class<?> clazz : p_Interfaces) {
	    for (Method method : clazz.getMethods()) {
		generateMethod(cw, className, method, generatedMethod);
	    }
	}
	cw.visitEnd();

	byte[] datas = cw.toByteArray();

	String generatedClassPath = CONFIG.getGeneratedClassPath();
	if (generatedClassPath != null) {
	    String path = generatedClassPath + "ldapbeans/bean";
	    String filename = className + ".class";
	    File file = new File(path, filename);
	    try {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(datas);
	    } catch (Exception e) {
		LOG.error(MESSAGE.getGeneratedClassWriteErrorMessage(className,
			file.getAbsolutePath()), e);
	    }
	}

	return m_ClassLoader.defineClass("ldapbeans.bean." + className, datas);
    }

    /**
     * Generate Fields of the generated class
     * 
     * @param p_ClassWriter
     *            The {@link ClassWriter} of the generated class
     */
    private void generateField(ClassWriter p_ClassWriter) {
	FieldVisitor fv;
	// generate "LdapObject m_LdapObject;"
	{
	    fv = p_ClassWriter.visitField(ACC_PRIVATE + ACC_FINAL,
		    "m_LdapObject", "Lldapbeans/bean/LdapObject;", null, null);
	    fv.visitEnd();
	}
	// generate "LdapObjectManager m_LdapObjectManager;"
	{
	    fv = p_ClassWriter.visitField(ACC_PRIVATE + ACC_FINAL,
		    "m_LdapObjectManager",
		    "Lldapbeans/bean/LdapObjectManager;", null, null);
	    fv.visitEnd();
	}
    }

    /**
     * Generate constructor of the generated class
     * 
     * @param p_ClassWriter
     *            The {@link ClassWriter} of the generated class
     * @param p_ClassName
     *            The name of the class
     */
    private void generateConstructor(ClassWriter p_ClassWriter,
	    String p_ClassName) {
	MethodVisitor mv;
	// Generate "<init>(LdapObject, LdapObjectManager)"
	mv = p_ClassWriter.visitMethod(ACC_PUBLIC, "<init>",
		"(Lldapbeans/bean/LdapObject;"
			+ "Lldapbeans/bean/LdapObjectManager;)V", null, null);
	mv.visitCode();
	mv.visitVarInsn(ALOAD, 0);
	mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
	mv.visitVarInsn(ALOAD, 0);
	mv.visitVarInsn(ALOAD, 1);
	mv.visitFieldInsn(PUTFIELD, "ldapbeans/bean/" + p_ClassName,
		"m_LdapObject", "Lldapbeans/bean/LdapObject;");
	mv.visitVarInsn(ALOAD, 0);
	mv.visitVarInsn(ALOAD, 2);
	mv.visitFieldInsn(PUTFIELD, "ldapbeans/bean/" + p_ClassName,
		"m_LdapObjectManager", "Lldapbeans/bean/LdapObjectManager;");
	mv.visitInsn(RETURN);
	mv.visitMaxs(0, 0);
	mv.visitEnd();
    }

    /**
     * Generate a method of the generated class
     * 
     * @param p_ClassWriter
     *            The {@link ClassWriter} of the generated class
     * @param p_ClassName
     *            The name of the class
     * @param p_Method
     *            the method to generate
     * @param p_GeneratedMethod
     *            Set of already generated methods
     */
    private void generateMethod(ClassWriter p_ClassWriter, String p_ClassName,
	    Method p_Method, Set<String> p_GeneratedMethod) {

	String methodDescriptor = Type.getMethodDescriptor(p_Method);
	String key = p_Method.getName() + methodDescriptor;
	if (!p_GeneratedMethod.contains(key)) {
	    try {
		if (p_Method.equals(LdapBean.class.getMethod("getDN"))) {
		    generateMethodGetDn(p_ClassWriter, p_ClassName, p_Method,
			    methodDescriptor);
		} else if (p_Method.equals(LdapBean.class.getMethod("store"))) {
		    generateMethodStore(p_ClassWriter, p_ClassName, p_Method,
			    methodDescriptor);
		} else if (p_Method.equals(LdapBean.class.getMethod("restore"))) {
		    generateMethodRestore(p_ClassWriter, p_ClassName, p_Method,
			    methodDescriptor);
		} else if (p_Method.equals(LdapBean.class.getMethod("move",
			String.class))) {
		    generateMethodMove(p_ClassWriter, p_ClassName, p_Method,
			    methodDescriptor);
		} else if (p_Method.equals(LdapBean.class.getMethod("remove"))) {
		    generateMethodRemove(p_ClassWriter, p_ClassName, p_Method,
			    methodDescriptor);
		} else {
		    generateMethod(p_ClassWriter, p_ClassName, p_Method,
			    methodDescriptor);
		}
		p_GeneratedMethod.add(key);
	    } catch (Exception e) {
		// Should not happen
		// Nothing to do, the method should not be generated
		LOG.warn(MESSAGE.getGeneratedClassErrorMessage(p_ClassName,
			p_Method), e);
	    }
	} else {
	    LOG.warn(MESSAGE.getGeneratedMethodExistsMessage(p_ClassName,
		    p_Method));
	}
    }

    /**
     * Generate a method of the generated class
     * 
     * @param p_ClassWriter
     *            The {@link ClassWriter} of the generated class
     * @param p_ClassName
     *            The name of the class
     * @param p_Method
     *            the method to generate
     * @param p_MethodDescriptor
     *            The method descriptor
     */
    private void generateMethodGetDn(ClassWriter p_ClassWriter,
	    String p_ClassName, Method p_Method, String p_MethodDescriptor) {
	MethodVisitor mv = p_ClassWriter.visitMethod(ACC_PUBLIC,
		p_Method.getName(), p_MethodDescriptor, null, null);
	mv.visitCode();
	mv.visitVarInsn(ALOAD, 0);
	mv.visitFieldInsn(GETFIELD, "ldapbeans/bean/" + p_ClassName,
		"m_LdapObject", "Lldapbeans/bean/LdapObject;");
	mv.visitMethodInsn(INVOKEVIRTUAL, "ldapbeans/bean/LdapObject", "getDn",
		"()Ljava/lang/String;");
	mv.visitInsn(ARETURN);
	mv.visitMaxs(0, 0);
	mv.visitEnd();
    }

    /**
     * Generate a method of the generated class
     * 
     * @param p_ClassWriter
     *            The {@link ClassWriter} of the generated class
     * @param p_ClassName
     *            The name of the class
     * @param p_Method
     *            the method to generate
     * @param p_MethodDescriptor
     *            The method descriptor
     */
    private void generateMethodStore(ClassWriter p_ClassWriter,
	    String p_ClassName, Method p_Method, String p_MethodDescriptor) {
	MethodVisitor mv = p_ClassWriter.visitMethod(ACC_PUBLIC, "store",
		"()V", null, new String[] { "javax/naming/NamingException" });
	mv.visitCode();
	mv.visitVarInsn(ALOAD, 0);
	mv.visitFieldInsn(GETFIELD, "ldapbeans/bean/" + p_ClassName,
		"m_LdapObjectManager", "Lldapbeans/bean/LdapObjectManager;");
	mv.visitVarInsn(ALOAD, 0);
	mv.visitFieldInsn(GETFIELD, "ldapbeans/bean/" + p_ClassName,
		"m_LdapObject", "Lldapbeans/bean/LdapObject;");
	mv.visitMethodInsn(INVOKEVIRTUAL, "ldapbeans/bean/LdapObjectManager",
		"storeLdapObject", "(Lldapbeans/bean/LdapObject;)V");
	mv.visitInsn(RETURN);
	mv.visitMaxs(0, 0);
	mv.visitEnd();
    }

    /**
     * Generate a method of the generated class
     * 
     * @param p_ClassWriter
     *            The {@link ClassWriter} of the generated class
     * @param p_ClassName
     *            The name of the class
     * @param p_Method
     *            the method to generate
     * @param p_MethodDescriptor
     *            The method descriptor
     */
    private void generateMethodRestore(ClassWriter p_ClassWriter,
	    String p_ClassName, Method p_Method, String p_MethodDescriptor) {
	MethodVisitor mv = p_ClassWriter.visitMethod(ACC_PUBLIC, "restore",
		"()V", null, new String[] { "javax/naming/NamingException" });
	mv.visitCode();
	mv.visitVarInsn(ALOAD, 0);
	mv.visitFieldInsn(GETFIELD, "ldapbeans/bean/" + p_ClassName,
		"m_LdapObjectManager", "Lldapbeans/bean/LdapObjectManager;");
	mv.visitVarInsn(ALOAD, 0);
	mv.visitFieldInsn(GETFIELD, "ldapbeans/bean/" + p_ClassName,
		"m_LdapObject", "Lldapbeans/bean/LdapObject;");
	mv.visitMethodInsn(INVOKEVIRTUAL, "ldapbeans/bean/LdapObjectManager",
		"restoreLdapObject", "(Lldapbeans/bean/LdapObject;)V");
	mv.visitInsn(RETURN);
	mv.visitMaxs(0, 0);
	mv.visitEnd();
    }

    /**
     * Generate a method of the generated class
     * 
     * @param p_ClassWriter
     *            The {@link ClassWriter} of the generated class
     * @param p_ClassName
     *            The name of the class
     * @param p_Method
     *            the method to generate
     * @param p_MethodDescriptor
     *            The method descriptor
     */
    private void generateMethodMove(ClassWriter p_ClassWriter,
	    String p_ClassName, Method p_Method, String p_MethodDescriptor) {
	MethodVisitor mv = p_ClassWriter.visitMethod(ACC_PUBLIC, "move",
		"(Ljava/lang/String;)V", null,
		new String[] { "javax/naming/NamingException" });
	mv.visitCode();
	// this.m_LdapObjectManager.moveLdapObject(this.m_LdapObject, p_Dn);
	mv.visitVarInsn(ALOAD, 0);
	mv.visitFieldInsn(GETFIELD, "ldapbeans/bean/" + p_ClassName,
		"m_LdapObjectManager", "Lldapbeans/bean/LdapObjectManager;");
	mv.visitVarInsn(ALOAD, 0);
	mv.visitFieldInsn(GETFIELD, "ldapbeans/bean/" + p_ClassName,
		"m_LdapObject", "Lldapbeans/bean/LdapObject;");
	mv.visitVarInsn(ALOAD, 1);
	mv.visitMethodInsn(INVOKEVIRTUAL, "ldapbeans/bean/LdapObjectManager",
		"moveLdapObject",
		"(Lldapbeans/bean/LdapObject;Ljava/lang/String;)V");
	mv.visitInsn(RETURN);
	mv.visitMaxs(0, 0);
	mv.visitEnd();
    }

    /**
     * Generate a method of the generated class
     * 
     * @param p_ClassWriter
     *            The {@link ClassWriter} of the generated class
     * @param p_ClassName
     *            The name of the class
     * @param p_Method
     *            the method to generate
     * @param p_MethodDescriptor
     *            The method descriptor
     */
    private void generateMethodRemove(ClassWriter p_ClassWriter,
	    String p_ClassName, Method p_Method, String p_MethodDescriptor) {
	MethodVisitor mv = p_ClassWriter.visitMethod(ACC_PUBLIC, "remove",
		"()V", null, new String[] { "javax/naming/NamingException" });
	mv.visitCode();
	mv.visitVarInsn(ALOAD, 0);
	mv.visitFieldInsn(GETFIELD, "ldapbeans/bean/" + p_ClassName,
		"m_LdapObjectManager", "Lldapbeans/bean/LdapObjectManager;");
	mv.visitVarInsn(ALOAD, 0);
	mv.visitFieldInsn(GETFIELD, "ldapbeans/bean/" + p_ClassName,
		"m_LdapObject", "Lldapbeans/bean/LdapObject;");
	mv.visitMethodInsn(INVOKEVIRTUAL, "ldapbeans/bean/LdapObjectManager",
		"removeLdapObject", "(Lldapbeans/bean/LdapObject;)V");
	mv.visitInsn(RETURN);
	mv.visitMaxs(0, 0);
	mv.visitEnd();
    }

    /**
     * Generate a method of the generated class
     * 
     * @param p_ClassWriter
     *            The {@link ClassWriter} of the generated class
     * @param p_ClassName
     *            The name of the class
     * @param p_Method
     *            the method to generate
     * @param p_MethodDescriptor
     *            The method descriptor
     */
    private void generateMethod(ClassWriter p_ClassWriter, String p_ClassName,
	    Method p_Method, String p_MethodDescriptor) {
	Class<?> returnType = p_Method.getReturnType();
	Class<?>[] parameterTypes = p_Method.getParameterTypes();
	if (void.class.equals(returnType) && (parameterTypes.length == 1)) {
	    // It must be a setter
	    generateMethodSetter(p_ClassWriter, p_ClassName, p_Method,
		    p_MethodDescriptor);
	} else if ((!void.class.equals(returnType))
		&& (parameterTypes.length == 0)) {
	    // It must be a getter
	    generateMethodGetter(p_ClassWriter, p_ClassName, p_Method,
		    p_MethodDescriptor);
	} else {
	    throw new UnsupportedOperationException();
	}
    }

    /**
     * Generate a method of the generated class
     * 
     * @param p_ClassWriter
     *            The {@link ClassWriter} of the generated class
     * @param p_ClassName
     *            The name of the class
     * @param p_Method
     *            the method to generate
     * @param p_MethodDescriptor
     *            The method descriptor
     */
    private void generateMethodSetter(ClassWriter p_ClassWriter,
	    String p_ClassName, Method p_Method, String p_MethodDescriptor) {
	Class<?>[] parameterTypes = p_Method.getParameterTypes();

	MethodVisitor mv = p_ClassWriter.visitMethod(ACC_PUBLIC,
		p_Method.getName(), p_MethodDescriptor, null, null);
	mv.visitCode();
	generateMethodSetterInitializeAttribute(mv, p_ClassName, p_Method);
	generateMethodSetterAssignValue(mv, p_Method, parameterTypes);
	// return;
	mv.visitInsn(RETURN);
	mv.visitMaxs(0, 0);
	mv.visitEnd();

    }

    /**
     * Generate a method of the generated class
     * 
     * @param p_MethodVisitor
     *            The {@link MethodVisitor} of the generated method
     * @param p_Class
     *            Name of the class to generate
     * @param p_Method
     *            the method to generate
     */
    private void generateMethodSetterInitializeAttribute(
	    MethodVisitor p_MethodVisitor, String p_Class, Method p_Method) {
	MethodVisitor mv = p_MethodVisitor;
	String attributeName = LdapBeanHelper.getInstance()
		.getLdapAttribute(p_Method).value();
	// Attributes attributes = m_LdapObject.getAttributes();
	mv.visitVarInsn(ALOAD, 0);
	mv.visitFieldInsn(GETFIELD, "ldapbeans/bean/" + p_Class,
		"m_LdapObject", "Lldapbeans/bean/LdapObject;");
	mv.visitMethodInsn(INVOKEVIRTUAL, "ldapbeans/bean/LdapObject",
		"getAttributes", "()Ljavax/naming/directory/Attributes;");
	mv.visitVarInsn(ASTORE, 2);
	// Attribute attribute = attributes.get("attributeName");
	mv.visitVarInsn(ALOAD, 2);
	mv.visitLdcInsn(attributeName);
	mv.visitMethodInsn(INVOKEINTERFACE,
		"javax/naming/directory/Attributes", "get",
		"(Ljava/lang/String;)Ljavax/naming/directory/Attribute;");
	mv.visitVarInsn(ASTORE, 3);
	// if (attribute == null) {
	mv.visitVarInsn(ALOAD, 3);
	Label l0 = new Label();
	mv.visitJumpInsn(IFNONNULL, l0);
	// attribute = new BasicAttribute("attributeName");
	mv.visitTypeInsn(NEW, "javax/naming/directory/BasicAttribute");
	mv.visitInsn(DUP);
	mv.visitLdcInsn(attributeName);
	mv.visitMethodInsn(INVOKESPECIAL,
		"javax/naming/directory/BasicAttribute", "<init>",
		"(Ljava/lang/String;)V");
	mv.visitVarInsn(ASTORE, 3);
	// attributes.put(attribute);
	mv.visitVarInsn(ALOAD, 2);
	mv.visitVarInsn(ALOAD, 3);
	mv.visitMethodInsn(INVOKEINTERFACE,
		"javax/naming/directory/Attributes", "put",
		"(Ljavax/naming/directory/Attribute;)"
			+ "Ljavax/naming/directory/Attribute;");
	mv.visitInsn(POP);
	if (!p_Method.getName().startsWith("add")) {
	    /*
	     * If method is not an adder (but a simple setter), attribute has to
	     * be cleared
	     */
	    // } else {
	    Label l1 = new Label();
	    mv.visitJumpInsn(GOTO, l1);
	    mv.visitLabel(l0);
	    // attribute.clear();
	    mv.visitVarInsn(ALOAD, 3);
	    mv.visitMethodInsn(INVOKEINTERFACE,
		    "javax/naming/directory/Attribute", "clear", "()V");
	    // }
	    mv.visitLabel(l1);
	} else {
	    // }
	    mv.visitLabel(l0);
	}
    }

    /**
     * Generate a portion of a method of the generated class
     * 
     * @param p_MethodVisitor
     *            The {@link MethodVisitor} of the generated method
     * @param p_Method
     *            the method to generate
     * @param p_ParameterTypes
     *            The type of parameters
     */
    private void generateMethodSetterAssignValue(MethodVisitor p_MethodVisitor,
	    Method p_Method, Class<?>[] p_ParameterTypes) {
	MethodVisitor mv = p_MethodVisitor;
	if (Collection.class.isAssignableFrom(p_ParameterTypes[0])) {
	    /*
	     * The parameter is a collection, each element of the collection
	     * will be added
	     */
	    // Iterator it = p_AttributeValue.iterator();
	    mv.visitVarInsn(ALOAD, 1);
	    mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Collection",
		    "iterator", "()Ljava/util/Iterator;");
	    mv.visitVarInsn(ASTORE, 5);
	    // while(it.hasNext()) {
	    Label l2 = new Label();
	    mv.visitLabel(l2);
	    mv.visitVarInsn(ALOAD, 5);
	    mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator",
		    "hasNext", "()Z");
	    Label l3 = new Label();
	    mv.visitJumpInsn(IFEQ, l3);

	    // String value = (String) it.next();
	    mv.visitVarInsn(ALOAD, 5);
	    mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next",
		    "()Ljava/lang/Object;");
	    mv.visitVarInsn(ASTORE, 4);
	    // attribute.add(value);
	    mv.visitVarInsn(ALOAD, 3);
	    mv.visitVarInsn(ALOAD, 4);
	    mv.visitMethodInsn(INVOKEINTERFACE,
		    "javax/naming/directory/Attribute", "add",
		    "(Ljava/lang/Object;)Z");
	    mv.visitInsn(POP);
	    // }
	    mv.visitJumpInsn(GOTO, l2);
	    mv.visitLabel(l3);
	} else {
	    generateMethodSetterAssignValueConvert(mv, p_Method,
		    p_ParameterTypes[0]);
	    // attribute.add(value);
	    mv.visitVarInsn(ALOAD, 3);
	    mv.visitVarInsn(ALOAD, 4);
	    mv.visitMethodInsn(INVOKEINTERFACE,
		    "javax/naming/directory/Attribute", "add",
		    "(Ljava/lang/Object;)Z");
	    mv.visitInsn(POP);
	}
    }

    /**
     * Generate a portion of a method of the generated class
     * 
     * @param p_MethodVisitor
     *            The {@link MethodVisitor} of the generated method
     * @param p_Method
     *            the method to generate
     * @param p_OriginalType
     *            The type before conversion
     */
    private void generateMethodSetterAssignValueConvert(
	    MethodVisitor p_MethodVisitor, Method p_Method,
	    Class<?> p_OriginalType) {
	MethodVisitor mv = p_MethodVisitor;
	/* the parameter is a simple type, it is simply added */
	if ((Boolean.class.equals(p_OriginalType) || boolean.class
		.equals(p_OriginalType))) {
	    // String value = p_AttributeValue?"true":"false";
	    generateMethodSetterAssignValueConvertBoolean(p_MethodVisitor,
		    p_Method, p_OriginalType);
	} else if ((Integer.class.equals(p_OriginalType) || int.class
		.equals(p_OriginalType))) {
	    // String value = String.valueOf((int)p_AttributeValue);
	    generateMethodSetterAssignValueConvertInteger(p_MethodVisitor,
		    p_Method, p_OriginalType);
	} else {
	    // String value = p_AttributeValue;
	    mv.visitVarInsn(ALOAD, 1);
	    mv.visitVarInsn(ASTORE, 4);
	}
    }

    /**
     * Generate a portion of a method of the generated class
     * 
     * @param p_MethodVisitor
     *            The {@link MethodVisitor} of the generated method
     * @param p_Method
     *            the method to generate
     * @param p_OriginalType
     *            The type before conversion
     */
    private void generateMethodSetterAssignValueConvertBoolean(
	    MethodVisitor p_MethodVisitor, Method p_Method,
	    Class<?> p_OriginalType) {
	MethodVisitor mv = p_MethodVisitor;
	LdapAttribute ldapAttribute = LdapBeanHelper.getInstance()
		.getLdapAttribute(p_Method);
	if (Boolean.class.equals(p_OriginalType)) {
	    mv.visitVarInsn(ALOAD, 1);
	    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean",
		    "booleanValue", "()Z");
	} else {
	    mv.visitVarInsn(ILOAD, 1);
	}
	Label l2 = new Label();
	mv.visitJumpInsn(IFEQ, l2);
	mv.visitLdcInsn(ldapAttribute.trueValue()[0]);
	Label l3 = new Label();
	mv.visitJumpInsn(GOTO, l3);
	mv.visitLabel(l2);
	mv.visitLdcInsn(ldapAttribute.falseValue()[0]);
	mv.visitLabel(l3);
	mv.visitVarInsn(ASTORE, 4);
    }

    /**
     * Generate a portion of a method of the generated class
     * 
     * @param p_MethodVisitor
     *            The {@link MethodVisitor} of the generated method
     * @param p_Method
     *            the method to generate
     * @param p_OriginalType
     *            The type before conversion
     */
    private void generateMethodSetterAssignValueConvertInteger(
	    MethodVisitor p_MethodVisitor, Method p_Method,
	    Class<?> p_OriginalType) {
	MethodVisitor mv = p_MethodVisitor;
	if (Integer.class.equals(p_OriginalType)) {
	    mv.visitVarInsn(ALOAD, 1);
	    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue",
		    "()I");
	} else {
	    mv.visitVarInsn(ILOAD, 1);
	}
	mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
		"(I)Ljava/lang/String;");
	mv.visitVarInsn(ASTORE, 4);
    }

    /**
     * Generate a method of the generated class
     * 
     * @param p_ClassWriter
     *            The {@link ClassWriter} of the generated class
     * @param p_ClassName
     *            The name of the class
     * @param p_Method
     *            the method to generate
     * @param p_MethodDescriptor
     *            The method descriptor
     */
    private void generateMethodGetter(ClassWriter p_ClassWriter,
	    String p_ClassName, Method p_Method, String p_MethodDescriptor) {
	Class<?> returnType = p_Method.getReturnType();
	MethodVisitor mv = p_ClassWriter.visitMethod(ACC_PUBLIC,
		p_Method.getName(), p_MethodDescriptor, null, null);
	mv.visitCode();
	generateMethodGetterLdapValue(mv, p_ClassName, p_Method, returnType);
	if ((Boolean.class.equals(returnType) || boolean.class
		.equals(returnType))) {
	    generateMethodGetterReturnBoolean(mv, p_Method, returnType);
	} else if ((Number.class.isAssignableFrom(returnType) || int.class
		.equals(returnType))) {
	    generateMethodGetterReturnNumber(mv, p_Method, returnType);
	} else {
	    // return result;
	    mv.visitVarInsn(ALOAD, 1);
	    Label l0 = new Label();
	    mv.visitJumpInsn(IFNULL, l0);
	    if (String.class.equals(returnType)) {
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object",
			"toString", "()Ljava/lang/String;");
		mv.visitInsn(ARETURN);
	    }
	    mv.visitLabel(l0);
	    mv.visitInsn(ACONST_NULL);
	    mv.visitInsn(ARETURN);
	}
	mv.visitMaxs(0, 0);
	mv.visitEnd();
    }

    /**
     * Generate a method of the generated class
     * 
     * @param p_MethodVisitor
     *            The {@link MethodVisitor} of the generated method
     * @param p_ClassName
     *            The name of the class
     * @param p_Method
     *            the method to generate
     * @param p_ReturnType
     *            The type of the result
     */
    private void generateMethodGetterLdapValue(MethodVisitor p_MethodVisitor,
	    String p_ClassName, Method p_Method, Class<?> p_ReturnType) {
	MethodVisitor mv = p_MethodVisitor;
	String attributeName = LdapBeanHelper.getInstance()
		.getLdapAttribute(p_Method).value();
	Label l0 = new Label();
	Label l1 = new Label();
	Label l2 = new Label();
	mv.visitTryCatchBlock(l0, l1, l2, "javax/naming/NamingException");
	// Object result = null;
	mv.visitInsn(ACONST_NULL);
	mv.visitVarInsn(ASTORE, 1);

	// Attributes attributes = m_LdapObject.getAttributes();
	mv.visitVarInsn(ALOAD, 0);
	mv.visitFieldInsn(GETFIELD, "ldapbeans/bean/" + p_ClassName,
		"m_LdapObject", "Lldapbeans/bean/LdapObject;");
	mv.visitMethodInsn(INVOKEVIRTUAL, "ldapbeans/bean/LdapObject",
		"getAttributes", "()Ljavax/naming/directory/Attributes;");
	mv.visitVarInsn(ASTORE, 2);
	// Attribute attribute = attributes.get("attributeName");
	mv.visitVarInsn(ALOAD, 2);
	mv.visitLdcInsn(attributeName);
	mv.visitMethodInsn(INVOKEINTERFACE,
		"javax/naming/directory/Attributes", "get",
		"(Ljava/lang/String;)Ljavax/naming/directory/Attribute;");
	mv.visitVarInsn(ASTORE, 3);
	// try {
	mv.visitLabel(l0);
	// if (attribute != null) {
	Label l3 = new Label();
	mv.visitVarInsn(ALOAD, 3);
	mv.visitJumpInsn(IFNULL, l3);
	generateMethodGetterAssignResult(mv, p_ReturnType);
	// }
	//
	mv.visitLabel(l1);
	mv.visitJumpInsn(GOTO, l3);
	mv.visitLabel(l2);
	mv.visitVarInsn(ASTORE, 4);
	// }
	mv.visitLabel(l3);
    }

    /**
     * Generate a portion of a method of the generated class
     * 
     * @param p_MethodVisitor
     *            The {@link MethodVisitor} of the generated method
     * @param p_Method
     *            the method to generate
     * @param p_ReturnType
     *            The type of the result
     */
    private void generateMethodGetterReturnBoolean(
	    MethodVisitor p_MethodVisitor, Method p_Method,
	    Class<?> p_ReturnType) {
	MethodVisitor mv = p_MethodVisitor;
	// return true or false instead of result
	LdapAttribute ldapAttribute = LdapBeanHelper.getInstance()
		.getLdapAttribute(p_Method);
	generateMethodGetterReturnBoolean(mv, p_ReturnType,
		ldapAttribute.trueValue(), true);
	generateMethodGetterReturnBoolean(mv, p_ReturnType,
		ldapAttribute.falseValue(), false);
	// Can't convert attribute to boolean
	// throw new IllegalArgumentException(object +
	// " cannot be converted into boolean");
	mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
	mv.visitInsn(DUP);
	mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
	mv.visitInsn(DUP);
	mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>",
		"()V");
	mv.visitVarInsn(ALOAD, 1);
	mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
		"(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
	mv.visitLdcInsn(" cannot be converted into boolean");
	mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
		"(Ljava/lang/String;)Ljava/lang/StringBuilder;");
	mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
		"toString", "()Ljava/lang/String;");
	mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException",
		"<init>", "(Ljava/lang/String;)V");
	mv.visitInsn(ATHROW);
    }

    /**
     * Generate a portion of a method of the generated class
     * 
     * @param p_MethodVisitor
     *            The {@link MethodVisitor} of the generated method
     * @param p_Method
     *            the method to generate
     * @param p_ReturnType
     *            The type of the result
     */
    private void generateMethodGetterReturnNumber(
	    MethodVisitor p_MethodVisitor, Method p_Method,
	    Class<?> p_ReturnType) {
	MethodVisitor mv = p_MethodVisitor;
	String returnType = p_ReturnType.getName().replace('.', '/');
	mv.visitVarInsn(ALOAD, 1);
	mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString",
		"()Ljava/lang/String;");
	if (int.class.equals(p_ReturnType)) {
	    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt",
		    "(Ljava/lang/String;)I");
	    mv.visitInsn(IRETURN);
	} else {
	    mv.visitMethodInsn(INVOKESTATIC, returnType, "valueOf",
		    "(Ljava/lang/String;)L" + returnType + ';');
	    mv.visitInsn(ARETURN);
	}
    }

    /**
     * Generate a portion of a method of the generated class
     * 
     * @param p_MethodVisitor
     *            The {@link MethodVisitor} of the generated method
     * @param p_ReturnType
     *            The type of the result
     * @param p_BValues
     *            List of possible String representation of the boolean value
     * @param p_BValue
     *            The boolean value to be returned
     */
    private void generateMethodGetterReturnBoolean(
	    MethodVisitor p_MethodVisitor, Class<?> p_ReturnType,
	    String[] p_BValues, boolean p_BValue) {
	MethodVisitor mv = p_MethodVisitor;
	{
	    // if(trueValue.equals(result))
	    Label l0 = new Label();
	    for (String bValue : p_BValues) {
		mv.visitLdcInsn(bValue);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object",
			"toString", "()Ljava/lang/String;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String",
			"equalsIgnoreCase", "(Ljava/lang/String;)Z");
		mv.visitJumpInsn(IFNE, l0);
	    }
	    Label l1 = new Label();
	    mv.visitJumpInsn(GOTO, l1);
	    // {
	    mv.visitLabel(l0);
	    if (boolean.class.equals(p_ReturnType)) {
		// return true|false;
		mv.visitInsn(p_BValue ? ICONST_1 : ICONST_0);
		mv.visitInsn(IRETURN);
	    } else {
		// return Boolean.TRUE|Boolean.FALSE;
		mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean",
			p_BValue ? "TRUE" : "FALSE", "Ljava/lang/Boolean;");
		mv.visitInsn(ARETURN);
	    }
	    // }
	    mv.visitLabel(l1);
	}
    }

    /**
     * Generate a portion of a method of the generated class
     * 
     * @param p_MethodVisitor
     *            The {@link MethodVisitor} of the generated method
     * @param p_ReturnType
     *            The type of the result
     */
    private void generateMethodGetterAssignResult(
	    MethodVisitor p_MethodVisitor, Class<?> p_ReturnType) {
	MethodVisitor mv = p_MethodVisitor;
	if (Collection.class.isAssignableFrom(p_ReturnType)) {
	    generateMethodGetterAssignResultCollection(p_MethodVisitor,
		    p_ReturnType);
	} else if (true == p_ReturnType.isArray()) {
	    generateMethodGetterAssignResultArray(p_MethodVisitor, p_ReturnType);
	} else {
	    // result = attribute.get();
	    mv.visitVarInsn(ALOAD, 3);
	    mv.visitMethodInsn(INVOKEINTERFACE,
		    "javax/naming/directory/Attribute", "get",
		    "()Ljava/lang/Object;");
	    mv.visitVarInsn(ASTORE, 1);
	}
    }

    /**
     * Generate a portion of a method of the generated class
     * 
     * @param p_MethodVisitor
     *            The {@link MethodVisitor} of the generated method
     * @param p_ReturnType
     *            The type of the result
     */
    private void generateMethodGetterAssignResultCollection(
	    MethodVisitor p_MethodVisitor, Class<?> p_ReturnType) {
	MethodVisitor mv = p_MethodVisitor;
	// NamingEnumeration<?> enumeration = attribute.getAll();
	mv.visitVarInsn(ALOAD, 3);
	mv.visitMethodInsn(INVOKEINTERFACE, "javax/naming/directory/Attribute",
		"getAll", "()Ljavax/naming/NamingEnumeration;");
	mv.visitVarInsn(ASTORE, 4);
	// result = new [Collection](attribute.size());
	generateMethodGetterInitializeResult(p_MethodVisitor, p_ReturnType);
	// while (enumeration.hasMoreElements()) {
	Label l4 = new Label();
	mv.visitLabel(l4);
	mv.visitVarInsn(ALOAD, 4);
	mv.visitMethodInsn(INVOKEINTERFACE, "javax/naming/NamingEnumeration",
		"hasMoreElements", "()Z");
	Label l5 = new Label();
	mv.visitJumpInsn(IFEQ, l5);
	// result.add(enumeration.nextElement());
	mv.visitVarInsn(ALOAD, 1);
	mv.visitVarInsn(ALOAD, 4);
	mv.visitMethodInsn(INVOKEINTERFACE, "javax/naming/NamingEnumeration",
		"nextElement", "()Ljava/lang/Object;");
	mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Collection", "add",
		"(Ljava/lang/Object;)Z");
	mv.visitInsn(POP);
	// }
	mv.visitJumpInsn(GOTO, l4);
	mv.visitLabel(l5);
	// enumeration.close();
	mv.visitVarInsn(ALOAD, 4);
	mv.visitMethodInsn(INVOKEINTERFACE, "javax/naming/NamingEnumeration",
		"close", "()V");
    }

    /**
     * Generate a portion of a method of the generated class
     * 
     * @param p_MethodVisitor
     *            The {@link MethodVisitor} of the generated method
     * @param p_ReturnType
     *            The type of the result
     */
    private void generateMethodGetterAssignResultArray(
	    MethodVisitor p_MethodVisitor, Class<?> p_ReturnType) {
	MethodVisitor mv = p_MethodVisitor;
	// NamingEnumeration<?> enumeration = attribute.getAll();
	mv.visitVarInsn(ALOAD, 3);
	mv.visitMethodInsn(INVOKEINTERFACE, "javax/naming/directory/Attribute",
		"getAll", "()Ljavax/naming/NamingEnumeration;");
	mv.visitVarInsn(ASTORE, 4);
	// result = new Object[attribute.size()];
	generateMethodGetterInitializeResult(p_MethodVisitor, p_ReturnType);
	// int i = 0;
	mv.visitInsn(ICONST_0);
	mv.visitVarInsn(ISTORE, 5);
	// while (enumeration.hasMoreElements()) {
	Label l4 = new Label();
	mv.visitLabel(l4);
	mv.visitVarInsn(ALOAD, 4);
	mv.visitMethodInsn(INVOKEINTERFACE, "javax/naming/NamingEnumeration",
		"hasMoreElements", "()Z");
	Label l5 = new Label();
	mv.visitJumpInsn(IFEQ, l5);
	// result[i++] = enumeration.nextElement();
	mv.visitVarInsn(ALOAD, 1);
	mv.visitVarInsn(ILOAD, 5);
	mv.visitIincInsn(5, 1);
	mv.visitVarInsn(ALOAD, 4);
	mv.visitMethodInsn(INVOKEINTERFACE, "javax/naming/NamingEnumeration",
		"nextElement", "()Ljava/lang/Object;");
	mv.visitInsn(AASTORE);
	// }
	mv.visitJumpInsn(GOTO, l4);
	mv.visitLabel(l5);
	// enumeration.close();
	mv.visitVarInsn(ALOAD, 4);
	mv.visitMethodInsn(INVOKEINTERFACE, "javax/naming/NamingEnumeration",
		"close", "()V");
    }

    /**
     * Generate a portion of a method of the generated class
     * 
     * @param p_MethodVisitor
     *            The {@link MethodVisitor} of the generated method
     * @param p_ReturnType
     *            The type of the result
     */
    private void generateMethodGetterInitializeResult(
	    MethodVisitor p_MethodVisitor, Class<?> p_ReturnType) {
	MethodVisitor mv = p_MethodVisitor;
	// result = new ArrayList<Object>(attribute.size());
	if (p_ReturnType.isArray()) {
	    mv.visitVarInsn(ALOAD, 3);
	    mv.visitMethodInsn(INVOKEINTERFACE,
		    "javax/naming/directory/Attribute", "size", "()I");
	    mv.visitTypeInsn(ANEWARRAY, p_ReturnType.getComponentType()
		    .getName().replace('.', '/'));
	} else if (!p_ReturnType.isInterface()) {
	    mv.visitTypeInsn(NEW, "java/util/ArrayList");
	    mv.visitInsn(DUP);
	    mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>",
		    "()V");
	} else {
	    String resultType;
	    if (List.class.isAssignableFrom(p_ReturnType)) {
		resultType = "java/util/ArrayList";
	    } else if (Queue.class.isAssignableFrom(p_ReturnType)) {
		resultType = "java/util/concurrent/ArrayBlockingQueue";
	    } else if (Set.class.isAssignableFrom(p_ReturnType)) {
		resultType = "java/util/HashSet";
	    } else {
		resultType = "java/util/ArrayList";
	    }
	    mv.visitTypeInsn(NEW, resultType);
	    mv.visitInsn(DUP);
	    mv.visitVarInsn(ALOAD, 3);
	    mv.visitMethodInsn(INVOKEINTERFACE,
		    "javax/naming/directory/Attribute", "size", "()I");
	    mv.visitMethodInsn(INVOKESPECIAL, resultType, "<init>", "(I)V");
	}
	mv.visitVarInsn(ASTORE, 1);
    }

    /**
     * Return a key for a generated class based on specific interfaces
     * 
     * @param p_Interfaces
     *            Interfaces
     * @return a key for a generated class based on specific interfaces
     */
    private String getClassKey(Class<?>[] p_Interfaces) {
	StringBuilder result = new StringBuilder();
	for (Class<?> clazz : p_Interfaces) {
	    result.append(clazz.getName()).append(' ');
	}
	return result.toString();
    }
}
