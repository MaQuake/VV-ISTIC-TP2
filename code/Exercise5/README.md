# Code of your exercise

Put here all the code created for this exercise

Here is the code that I modified to calculate cyclomatic complexity and
generate histogram in output.txt.


```java
@Override
    public void visit(MethodDeclaration declaration, Void arg) {

        String packageName = currentPackageName;
        String declaringClassName = currentClassName;
        String methodName = declaration.getNameAsString();
        List<Parameter> parameters = declaration.getParameters();

        System.out.println("Package: " + packageName);
        System.out.println("Declaring Class: " + declaringClassName);
        System.out.println("Method Name: " + methodName);

        // Print parameter types
        if (!parameters.isEmpty()) {
            System.out.print("Parameters: ");
            for (Parameter parameter : parameters) {
                System.out.print(parameter.getType() + " " + parameter.getNameAsString() + ", ");
            }
            System.out.println();
        }

        int cc = calculateCyclomaticComplexity(declaration);
        System.out.println("Cyclomatic Complexity: " + cc);
        System.out.println();
    }


    /**
     * Calculate cyclomatic complexity base on McCabe method
     * @param method
     * @return
     */
    private int calculateCyclomaticComplexity(MethodDeclaration method) {
        // Count the number of predicate nodes (conditionals)
        int predicateNodes = method.getNodesByType(IfStmt.class).size() +
                method.getNodesByType(WhileStmt.class).size() +
                method.getNodesByType(ForStmt.class).size() +
                method.getNodesByType(ForEachStmt.class).size() +
                method.getNodesByType(SwitchEntry.class).size();

        ccValuesMap.put(method.getNameAsString(), predicateNodes);

        // Simplified calculation of Cyclomatic Complexity: v(G) = number of predicate nodes + 1
        return predicateNodes + 1;
    }

    /**
     * Generate histogram based on collected Cyclomatic Complexity values
     */
    private void generateHistogram() {
        System.out.println("Cyclomatic Complexity Histogram:");

        // Group CC values by count
        Map<Integer, Long> ccCounts = ccValuesMap.values().stream()
                .collect(Collectors.groupingBy(cc -> cc, Collectors.counting()));

        // Print histogram
        ccCounts.forEach((cc, count) -> {
            System.out.println("CC=" + cc + ": " + "*".repeat(count.intValue()));
        });
    }
```

In the file output.txt in this directory you can find the output of
my program for the Apache collection project.

Histogramms are in the file output.txt.