LDAPBeans is a tool that allow you to access easily to LDAP objects throw Java interfaces. The idea is that you simply have to write an interface describing datas of LDAP objets without having to write implementation of your beans. LDAPBeans is in charge of providing access to your LDAP directory.

# How does it work #
Please see [Introduction](Introduction.md) in the wiki

# Features #
  * Primitive type mapping (and their wrapper)
  * Mapping to other LdapBean object
  * Complex mapping using regular expressions
  * LDAP directory access cache
  * Utility features describe in the [LdapbeansUtils](LdapbeansUtils.md) page in the wiki
    * Scanner (find classes in classpath, ie. looking for classes from their package)
    * Zip file helper (provide File interface for zip files)
    * i18n
    * Cache
    * Pool

# How to use it #
## For maven users ##
Add following lines to your pom.xml:
```
<project>
  ...
  <dependencies>
    <dependency>
      <groupId>com.googlecode.ldapbeans</groupId>
      <artifactId>ldapbeans</artifactId>
      <version>0.1.2</version>
    </dependency>
    ...
  </dependencies>
  ...
  <repositories>
    <repository>
      <id>ldapbeans</id>
      <url>http://ldapbeans.googlecode.com/svn/maven2</url>
    </repository>
    ...
  </repositories>
  ...
</project>
```

## For other's ##
[Download the lastest ldapbean jar](http://code.google.com/p/ldapbeans/downloads/detail?name=ldapbeans-0.1.2.jar) and add it to your classpath


&lt;wiki:gadget url="http://www.ohloh.net/p/488647/widgets/project\_cocomo.xml" height="240" border="0"/&gt;&lt;wiki:gadget url="http://www.ohloh.net/p/488647/widgets/project\_factoids.xml" border="0"/&gt;