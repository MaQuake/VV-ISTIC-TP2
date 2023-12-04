# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset. Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.

## Answer

I choose this project [Apache Commons Collections](https://github.com/apache/commons-collections)

After installing pmd, I launch this command : 

```pmd.bat check -f text -R .\quickstart.xml -d .\commons-collections\ ```

### True positive

I found a bad pratice  in this file
```.\commons-collections\src\test\java\org\apache\commons\collections4\AbstractObjectTest.java:115:        EqualsNull:     Avoid using equals() to compare against null```

```java
    @Test
    public void testEqualsNull() {
        final Object obj = makeObject();
        assertFalse(obj.equals(null)); // make sure this doesn't throw NPE either
    }
```

We should not use obj.equals(null). We should do this :
```java
    @Test
    public void testEqualsNull() {
        final Object obj = makeObject();
        assertFalse(obj == null); // make sure this doesn't throw NPE either
    }
```


### False positive

We can find this False positive :

```.\commons-collections\src\main\java\org\apache\commons\collections4\ClosureUtils.java:61:ClassWithOnlyPrivateConstructorsShouldBeFinal:       This class has only private constructors and may be final```

It says that has only private constructor but that is ok because
the comment over specify : "Don't allow instances."

