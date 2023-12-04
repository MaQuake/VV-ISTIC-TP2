package fr.istic.vv;

import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Should provide the path to the source code");
            System.exit(1);
        }

        Path sourcePath = Paths.get(args[0]);
        if (!sourcePath.toFile().exists() || !sourcePath.toFile().isDirectory() || !sourcePath.toFile().canRead()) {
            System.err.println("Provide a path to an existing readable directory");
            System.exit(2);
        }

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
    }
}