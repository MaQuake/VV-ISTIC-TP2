package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.SwitchExpr;
import com.github.javaparser.ast.nodeTypes.SwitchNode;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.lang.invoke.SwitchPoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


// This class visits a compilation unit and
// prints all public enum, classes or interfaces along with their public methods
public class PublicElementsPrinter extends VoidVisitorWithDefaults<Void> {

    private Map<String, Integer> ccValuesMap = new HashMap<>();

    private String currentPackageName;
    private String currentClassName;
    @Override
    public void visit(CompilationUnit unit, Void arg) {
        PackageDeclaration packageDeclaration = unit.getPackageDeclaration().orElse(null);
        currentPackageName = packageDeclaration != null ? packageDeclaration.getNameAsString() : "";
        for(TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
        generateHistogram();
    }

    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        currentClassName = declaration.getNameAsString();

        if(!declaration.isPublic()) return;
        System.out.println(declaration.getFullyQualifiedName().orElse("[Anonymous]"));
        for(MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }
        // Printing nested types in the top level
        for(BodyDeclaration<?> member : declaration.getMembers()) {
            member.accept(this, arg);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(EnumDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(FieldDeclaration declaration, Void arg) {
        if (declaration.isPrivate() && !hasGetter(declaration)) {
            String fieldName = declaration.getVariables().get(0).getNameAsString();
            System.out.println("Private Field Without Getter:");
            System.out.println("  Field Name: " + fieldName);
            System.out.println("  Declaring Class: " + currentClassName);
            System.out.println("  Package: " + currentPackageName);
        }
    }
    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        //if (!declaration.isPublic()) return;

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

}
