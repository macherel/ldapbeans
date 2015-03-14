# System properties #

| **Property** | **Required** | **Default value** | **Description** |
|:-------------|:-------------|:------------------|:----------------|
| ldapbeans.cache.impl | false | ldapbeans.util.cache.SimpleCache | Name of the class that will be used for the cache (this class have to implement ldapbeans.util.cache.Cache interface |
| ldapbeans.generated.class.path | false | `null` | Path where generated class will be stored. |
| ldapbeans.debug.line.number.enabled | false | `false` | Simply set this property if you want to add line number debugging inforamtion in generated class. It can help to understand what happens when you try to decompile generated classes and debug your program. |
| ~~ldapbeans.use.proxy.bean~~ | false | `false` | Simply set this property if you want to use Proxy instead of generating classes when ldapbeans create a new bean (Not recommended). |

Example : `java -cp ldapbeans.jar:. -Dldapbeans.cache.impl=ldapbeans.util.cache.SimpleCache -Dldapbeans.generated.class.path=/tmp -Dldapbeans.debug.line.number.enabled Main`