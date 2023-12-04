# Code of your exercise

## Pour la partie PublicElementsPrinter
```java
@Override
public void visit(FieldDeclaration declaration, Void arg) {
        if (declaration.isPrivate() && !hasGetter(declaration)) {
            String fieldName = declaration.getVariables().get(0).getNameAsString();
            System.out.println("Private Field Without Getter:");
            System.out.println("  Field Name: " + fieldName);
            System.out.println("  Declaring Class: " + currentClassName);
            System.out.println("  Package: " + currentPackageName);}
}


    
    
```

```java
private boolean hasGetter(FieldDeclaration fieldDeclaration) {
        String fieldName = fieldDeclaration.getVariables().get(0).getNameAsString();
        String getterMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        Optional<ClassOrInterfaceDeclaration> classDeclarationOpt = fieldDeclaration.findAncestor(ClassOrInterfaceDeclaration.class);
        if (classDeclarationOpt.isPresent()) {
            ClassOrInterfaceDeclaration classDeclaration = classDeclarationOpt.get();
            List<BodyDeclaration<?>> classMembers = classDeclaration.getMembers();

            for (BodyDeclaration<?> member : classMembers) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration) member;
                    if (methodDeclaration.isPublic() &&
                            methodDeclaration.isMethodDeclaration() &&
                            methodDeclaration.getNameAsString().equals(getterMethodName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
```

## Partie Main


```java
        // Redirection de la sortie vers un fichier
        PrintStream originalOut = System.out;
        PrintStream fileOut = new PrintStream(new File("output.txt"));
        System.setOut(fileOut);

        try {
            SourceRoot root = new SourceRoot(sourcePath);
            PublicElementsPrinter printer = new PublicElementsPrinter();
            root.parse("", (localPath, absolutePath, result) -> {
                result.ifSuccessful(unit -> unit.accept(printer, null));
                return SourceRoot.Callback.Result.DONT_SAVE;
            });
        } finally {
            // Rétablir la sortie standard
            System.setOut(originalOut);
            fileOut.close();
        }
```