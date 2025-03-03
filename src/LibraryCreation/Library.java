package LibraryCreation;

import java.io.*;
import java.util.List;

public class Library implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private String fileLocation;
    private String headerFile;
    private String sourceFile;
    private String keywordsTXT;
    private Example[] examples;
    private final List<String> acceptedVariables;
    private boolean location = false;

    public Library(String name, String headerFile, String sourceFile, String keywordsTXT, Example[] examples, List<String> acceptedVariables, String fileLocation) {
        this.name = name;
        this.fileLocation = fileLocation;
        this.headerFile = headerFile;
        this.sourceFile = sourceFile;
        this.keywordsTXT = keywordsTXT;
        this.examples = examples;
        this.acceptedVariables = acceptedVariables;
        location = true;
    }

    public Library(String name, String headerFile, String sourceFile, String keywordsTXT, Example[] examples, List<String> acceptedVariables) {
        this.name = name;
        this.headerFile = headerFile;
        this.sourceFile = sourceFile;
        this.keywordsTXT = keywordsTXT;
        this.examples = examples;
        this.acceptedVariables = acceptedVariables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
        location = true;
    }

    public String getHeaderFile() {
        return headerFile;
    }

    public void setHeaderFile(String headerFile) {
        this.headerFile = headerFile;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getKeywordsTXT() {
        return keywordsTXT;
    }

    public void setKeywordsTXT(String keywordsTXT) {
        this.keywordsTXT = keywordsTXT;
    }

    public Example[] getExamples() {
        return examples;
    }

    public void setExamples(Example[] examples) {
        this.examples = examples;
    }

    public void addExample(Example example) {
        if (this.examples == null) {
            this.examples = new Example[]{ example };
        } else {
            Example[] newExamples = new Example[this.examples.length + 1];
            System.arraycopy(this.examples, 0, newExamples, 0, this.examples.length);
            newExamples[this.examples.length] = example;
            this.examples = newExamples;
        }
    }

    public List<String> getAcceptedVariables() {
        return acceptedVariables;
    }

    public String serializeLibrary(String filename) throws IOException {
        File dir = new File("SavedLibraries");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, filename);
        try (FileOutputStream fileOut = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(this);
        }
        return file.getAbsolutePath();
    }

    public static Library deserializeLibrary(String fullFilePath) throws IOException, ClassNotFoundException {
        File file = new File(fullFilePath);
        try (FileInputStream fileIn = new FileInputStream(file);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (Library) in.readObject();
        }
    }

    public void buildLibrary() {
        if (!location) {
            new LibraryMaker(headerFile, sourceFile, keywordsTXT, examples, name).makeLibrary();
        } else {
            new LibraryMaker(headerFile, sourceFile, keywordsTXT, examples, name, fileLocation).makeLibrary();
        }
    }

    public void buildLibrary(Library library) {
        LibraryMaker maker = new LibraryMaker(library.getHeaderFile(), library.getSourceFile(), library.getKeywordsTXT(), library.getExamples(), library.getName());
        maker.makeLibrary();
        try {
            for (Example example : library.getExamples()) {
                maker.addExample(example);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void buildLibrary(Library library, String fileLocation) {
        LibraryMaker maker = new LibraryMaker(library.getHeaderFile(), library.getSourceFile(), library.getKeywordsTXT(), library.getExamples(), library.getName(), fileLocation);
        maker.makeLibrary();
        try {
            for (Example example : library.getExamples()) {
                maker.addExample(example);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
