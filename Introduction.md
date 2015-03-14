# Introduction #

LDAPBeans is an easy way to read and write object to a LDAP directory.

# Code sample #

**Person.java**
```
import ldapbeans.annotation.LdapAttribute;
import ldapbeans.annotation.ObjectClass;

@ObjectClass({ "person" })
public interface Person extends LdapBean {

    @LdapAttribute("cn")
    String getCommonName();

    @LdapAttribute("cn")
    void setCommonName(String p_CommonName);

    @LdapAttribute("sn")
    String getSurname();

    @LdapAttribute("sn")
    void setSurname(String p_Surname);

    @LdapAttribute("description")
    Person getParent();
}
```

**Sample.java**
```
public class Sample {
    public static void main(String[] args) {
    }

    public static void createPerson() {
        LdapBeanManager manager = LdapBeanManager.getInstance("ldap://localhost:389", "ou=system");
        Person person = null;
        person = s_Manager.create(Person.class, "cn=foo,ou=system");
        person.setCommonName("foo");
        person.setSurname("surname");
        person.store();
    }

    public static void modifyPerson() {
LdapBeanManager manager = LdapBeanManager.getInstance("ldap://localhost:389", "ou=system");
        Person person = null;
        person = s_Manager.findByDn(Person.class, "cn=Kim Wilde,ou=system");
        person.setSurname("foo");
        person.store();
    }


    public static void modifyParent() {
LdapBeanManager manager = LdapBeanManager.getInstance("ldap://localhost:389", "ou=system");
        Person person = null;
        Person parent = null;
        person = s_Manager.findByDn(Person.class, "cn=Kim Wilde,ou=system");

        parent = person.getParent();
        parent.setSurname("foo");
        parent.store();
    }
}
```
You can find more complex samples on [Features](Features.md) page.