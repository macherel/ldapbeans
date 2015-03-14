# Primitive types mapping #
You can map LDAP attribute to any primitive type (byte, short, int, long, float, double and char) and their respective wrapper (Byte, Short, Integer, Long, Float, Double and Character)

**Sample.java**
```
import ldapbeans.annotation.LdapAttribute;
import ldapbeans.annotation.ObjectClass;

@ObjectClass({ "myObjectclass" })
public interface Sample extends LdapBean {

    @LdapAttribute("byteAttribute")
    byte getByte();
    @LdapAttribute("byteAttribute")
    void setByte(byte p_Byte);

    @LdapAttribute("shortAttribute")
    short getShort();
    @LdapAttribute("shortAttribute")
    void setShort(short p_Short);

    @LdapAttribute("intAttribute")
    int getInt();
    @LdapAttribute("intAttribute")
    void setInt(int p_Int);

    @LdapAttribute("longAttribute")
    long getLong();
    @LdapAttribute("longAttribute")
    void setLong(long p_Long);

    @LdapAttribute("floatAttribute")
    float getFloat();
    @LdapAttribute("floatAttribute")
    void setFloat(float p_Float);

    @LdapAttribute("doubleAttribute")
    double getDouble();
    @LdapAttribute("doubleAttribute")
    void setDouble(double p_Double);

    @LdapAttribute("charAttribute")
    char getChar();
    @LdapAttribute("charAttribute")
    void setChar(char p_Char);

    @LdapAttribute("booleanAttribute")
    boolean getBoolean();
    @LdapAttribute("booleanAttribute")
    void setBoolean(boolean p_Boolean);
}
```

Boolean values are store as a string. By default, true is store as "true" and false is store as "false".
When an attribute is convert to boolean, "true" and "1" are convert to true and "false" and "0" are convert to false.
It is possible to customize these values by adding 'trueValue' and 'falseValue' attributes to LdapAttribute annotation :
```
@LdapAttribute(value="...", trueValue={ "a", "b", "c" }, falseValue={ "x", "y", "z" }
// In this example,
// true is convert to "a"  in a setter
// "a", "b" or "c" is convert to true in a getter 
// false is convert to "x"  in a setter
// "x", "y" or "z" is convert to true in a getter
```

# LdapBean mapping #
You can map LDAP attribute to other LdapBean object.
By default, the LDAP attribute have to correspond to the DN (distinguish name) of the other LdapBean.

**sample.ldif**
```
dn: name=foo,ou=system
objectClass: myObjectclass
objectClass: top
name: foo
otherBean: name=bar,ou=system

dn: name=bar,ou=system
objectClass: myObjectclass
objectClass: top
name: bar
```
**SampleBean.java**
```
import ldapbeans.annotation.LdapAttribute;
import ldapbeans.annotation.ObjectClass;

@ObjectClass({ "myObjectclass" })
public interface SampleBean extends LdapBean {

    @LdapAttribute("otherSampleBean")
    SampleBean getOtherSampleBean();

    @LdapAttribute("otherSampleBean")
    void setOtherSampleBean(SampleBean p_SampleBean);

    @LdapAttribute("name")
    String getName();
}
```
**Sample.java**
```
public class Sample {
    public static void main(String[] args) {
        LdapBeanManager manager = LdapBeanManager.getInstance("ldap://localhost:389", "ou=system");
        SampleBean foo = manager.findByDn(SampleBean.class, "name=foo,ou=system");

        // get the bean corresponding to the dn "name=bar,ou=system"
        SampleBean bar = foo.getOtherBean();
        // Return "bar"
        bar.getName();
        // Return null
        bar.getSampleBean();
        bar.setSampleBean(foo);
        // Return "foo"
        bar.getSampleBean().getName();
    }
```

**NB:** Only getter is managed for LdapBean. There is no way to use setter with LdapBean for the moment.

# Complex mapping #
## Using LDAP search ##
**sample.ldif**
```
dn: name=foo,ou=system
objectClass: myObjectclass
objectClass: top
name: foo
otherBean: bar

dn: name=bar,ou=system
objectClass: myObjectclass
objectClass: top
name: bar
```
**SampleBean.java**
```
import ldapbeans.annotation.LdapAttribute;
import ldapbeans.annotation.ObjectClass;

@ObjectClass({ "myObjectclass" })
public interface SampleBean extends LdapBean {

    @LdapAttribute(value="otherSampleBean", search="(name=$0)")
    SampleBean getOtherSampleBean();

    @LdapAttribute(value="otherSampleBean", method="getName")
    void setOtherSampleBean(SampleBean p_SampleBean);

    @LdapAttribute("name")
    String getName();
```
**Sample.java**
```
public class Sample {
    public static void main(String[] args) {
        LdapBeanManager manager = LdapBeanManager.getInstance("ldap://localhost:389", "ou=system");
        SampleBean bean = manager.findByDn(SampleBean.class, "name=foo,ou=system");

        // get the bean corresponding to result of the search (name=bar)"
        bean = bean.getOtherBean();
        // Return "bar"
        bean.getName();
        // Return null
        bar.getSampleBean();
        bar.setSampleBean(foo);
        // Return "foo"
        bar.getSampleBean().getName();
    }
```

## Using LDAP search with regular expression ##
**sample.ldif**
```
dn: name=foo,ou=system
objectClass: myObjectclass
objectClass: top
name: foo
otherBean: xxxx-bar-xxxx

dn: name=bar,ou=system
objectClass: myObjectclass
objectClass: top
name: bar
```
**SampleBean.java**
```
import ldapbeans.annotation.LdapAttribute;
import ldapbeans.annotation.ObjectClass;

@ObjectClass({ "myObjectclass" })
public interface SampleBean extends LdapBean {

    @LdapAttribute(value="otherSampleBean", search="(name=$0)", pattern = "^\\w+-(\\w*)-\\w+$")
    SampleBean getOtherSampleBean();

    @LdapAttribute(value="otherSampleBean", method="getName", pattern="$1-$0-$1")
    void setOtherSampleBean(SampleBean p_SampleBean, String p_String);

    @LdapAttribute("name")
    String getName();
```
**Sample.java**
```
public class Sample {
    public static void main(String[] args) {
        LdapBeanManager manager = LdapBeanManager.getInstance("ldap://localhost:389", "ou=system");
        SampleBean bean = manager.findByDn(SampleBean.class, "name=foo,ou=system");

        // get the bean corresponding to result of the search (name=bar)"
        bean = bean.getOtherBean();
        // Return "bar"
        bean.getName();
        // Return null
        bar.getSampleBean();
        bar.setSampleBean(foo, "xxxx");
        // Return "foo"
        bar.getSampleBean().getName();
    }
```