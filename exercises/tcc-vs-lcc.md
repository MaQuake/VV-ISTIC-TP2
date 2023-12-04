# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

A refresher on TCC and LCC is available in the [course notes](https://oscarlvp.github.io/vandv-classes/#cohesion-graph).

## Answer

TCC and LCC will produce the same value when all method 
in a class access to a common attribute, or if they do not access
to any attributes.


```java
public class ExampleClass {

    private String string1;
    private String string2;

    public void method1() {
        this.string1 = "";
        this.string2 = "";
    }

    public void method2() {
        this.string1 = "method2";
        this.string2 = "method2";
    }

}
```

LCC could not be lower than TCC because it's more common to
share attributes inside methods. Moreover, LCC represent connected
directly and indirectly nodes when TCC is only directly connected nodes.

So LCC is TCC + indirectly connected nodes.
