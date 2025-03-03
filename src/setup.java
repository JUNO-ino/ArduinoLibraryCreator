import LibraryCreation.Example;
import LibraryCreation.Library;
import LibraryCreation.LibraryMaker;
import LibraryCreation.Variable;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class setup {
    public static void main(String[] args) {
        File file = new File("File.ino");
        LibraryMaker libraryMaker = new LibraryMaker(file, "library");
        Scanner scanner = new Scanner(System.in);
        Thread makerThread = new Thread(new Runnable() {
            public void run() {
                libraryMaker.makeLibrary();
            }
        });
        makerThread.start();
        while (true) {
            if (setUpDecision(libraryMaker, scanner)){
                libraryMaker.setNeedsAccepting(false);
                break;
            }
            if(!libraryMaker.NeedsAccepting()){
                break;
            }
        }
        try {
            makerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        try {
//            libraryMaker.addExample(file, "example.ino");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        Library library = libraryMaker.saveLibrary();
//        library.addExample(new Example("example2.ino", "test"));
//        try {
//            String libraryFilePath = library.serializeLibrary("library01.txt");
//            library.buildLibrary(Library.deserializeLibrary(libraryFilePath));
//        } catch (IOException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            Library deserialize = Library.deserializeLibrary("C:\\Users\\schoo\\OneDrive\\Desktop\\ArduinoLibraryManager\\SavedLibraries\\library01.txt");
//            deserialize.buildLibrary();
//        } catch (IOException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
        scanner.close();
    }

    private static boolean setUpDecision(LibraryMaker libraryMaker, Scanner scanner) {
        boolean allDecided = true;
        for (Variable var : libraryMaker.getVariableVector()) {
            synchronized (var) {
                if (!var.isDecisionMade()) {
                    allDecided = false;
                    makeDecision(scanner, var);
                }
            }
        }
        if (allDecided && !libraryMaker.getVariableVector().isEmpty()) {
            return true;
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static void makeDecision(Scanner scanner, Variable var) {
        System.out.println(var.getDeclaration());
        System.out.println("Add Variable to constructor? (yes/no)");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("yes")) {
            var.accept();
            System.out.println("Added Variable To Constructor");
        } else {
            var.deny();
            System.out.println("Didn't Add Variable To Constructor");
        }
    }
}
