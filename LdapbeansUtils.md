# Introduction #

LDAPBeans have an utility package that  offer some interesting features and can be reused in other projects.

All these features presented here are is the package `ldapbeans.util`

# Cache #
It offers an interfaces and some differents implementations for cache mechanism.
## Simple cache ##
It is a simple implementation of the Cache interface. This is the simpliest implementation of a cache as wrapper over an HashMap.
## LRU cache ##
With this implementation, it is now possible to have a cache with a maximum size. If the maximum size is reach and if you want to add another entry, the LRU (Last Recently Used) entry will be removed
## TTL cache ##
This cache implementation automatically remove the cache entries if they are too old. No entries will be retained over a certain time.
## Commitable cache ##
This cache offer `commit` and `rollback` features for a cache. Any modification of the cache can be rolled back since the last commit.

# i18n #
This package offer some interesting internationalization features and a logger interface that support simple internationalized message.

Example :

**sample.properties**
```
hello.world=Hello $0
```
**sample\_fr.properties**
```
hello.world=Salut $0
```
**Sample.java**
```
import ldapbeans.util.i18n.MessageManager;

public class Sample {
    public static void main(String[] args) {
        // Display the message ("Hello foo") in your language
        System.out.println(MessageManager.getInstance("sample").getMessage("hello.world", "foo"));
    }
```
# Pool #
It is just another pool mechanism implementation.

# Scanner #
This package allows you to find some classes present in the classpath. You can apply filters to narrow the search.

Example :
```
import ldapbeans.util.scanner.PackageHelper;
import ldapbeans.util.scanner.ClassFilter;

public class Sample {
    public static void main(String[] args) {
        Collection<Class<?>> classes = null;

        // Find all classes in the package "some.package" but not in its sub-package.
	classes = PackageHelper.getInstance().getClasses("some.package", false, null);

        // Find all classes in the package "some.package" and in its sub-package.
	classes = PackageHelper.getInstance().getClasses("some.package", true, null);

        // Create a filter that accepts only classes which name starts with "foo"
        ClassFilter classFilter = new ClassFilter() {
            public boolean accept(Class<?> p_Class) {
                return p_Class.getName().startsWith("foo");
            }
        };

        // Find classes accepted by the filter in the package "some.package" but not in its sub-package.
	classes = PackageHelper.getInstance().getClasses("some.package", false, classFilter);

        // Find classes accepted by the filter in the package "some.package" and in its sub-package.
	classes = PackageHelper.getInstance().getClasses("some.package", true, classFilter);
    }
```

# Zip file helper #
This package offer an implementation of `java.io.File` over `java.util.zip.ZipFile`. This allow to access ZipFile as standard File. Is is now possible to navigate into a ZipFile using `File` interface