package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.List;
import java.util.Optional;


// This class visits a compilation unit and
// prints all public enum, classes or interfaces along with their public methods
public class PublicElementsPrinter extends VoidVisitorWithDefaults<Void> {

    private String currentPackageName;
    private String currentClassName;
    @Override
    public void visit(CompilationUnit unit, Void arg) {
        PackageDeclaration packageDeclaration = unit.getPackageDeclaration().orElse(null);
        currentPackageName = packageDeclaration != null ? packageDeclaration.getNameAsString() : "";
        for(TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
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
        if(!declaration.isPublic()) return;
        //System.out.println("  " + declaration.getDeclarationAsString(true, true));
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
