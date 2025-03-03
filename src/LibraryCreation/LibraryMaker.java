package LibraryCreation;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class LibraryMaker {
    private final String name;
    private final File headerFile;
    private final File sourceFile;
    private final File keywordsTXT;
    private List<String> fileLines;
    private final List<String> constructorParams = new ArrayList<>();
    private final Vector<Variable> VariableVector = new Vector<>();
    private final List<String> methodPrototypes = new ArrayList<>();
    private final File folder;
    private String headerContent;
    private String sourceContent;
    private String keywordsContent;
    private Example[] examples;
    private boolean needsAccepting = false;

    public LibraryMaker(File file, String fileLocation, String name) {
        this.name = name;
        this.folder = new File(fileLocation, name);
        this.folder.mkdirs();
        File srcFolder = new File(this.folder, "src");
        srcFolder.mkdirs();
        keywordsTXT = new File(this.folder, "keywords.txt");
        headerFile = new File(srcFolder, name + ".h");
        sourceFile = new File(srcFolder, name + ".cpp");
        try {
            headerFile.createNewFile();
            sourceFile.createNewFile();
            keywordsTXT.createNewFile();
            fileLines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LibraryMaker(File file, String name) {
        this.name = name;
        this.folder = new File(name);
        this.folder.mkdirs();
        File srcFolder = new File(this.folder, "src");
        srcFolder.mkdirs();
        keywordsTXT = new File(this.folder, "keywords.txt");
        headerFile = new File(srcFolder, name + ".h");
        sourceFile = new File(srcFolder, name + ".cpp");
        try {
            headerFile.createNewFile();
            sourceFile.createNewFile();
            keywordsTXT.createNewFile();
            fileLines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean NeedsAccepting() {
        return needsAccepting;
    }

    public void setNeedsAccepting(boolean needsAccepting) {
        this.needsAccepting = needsAccepting;
    }

    public LibraryMaker(String headerContent, String sourceContent, String keywordsContent, Example[] examples, String name, String fileLocation) {
        this.headerContent = headerContent;
        this.sourceContent = sourceContent;
        this.keywordsContent = keywordsContent;
        if (examples != null) {
            this.examples = examples;
        } else {
            this.examples = new Example[0];
        }
        this.name = name;
        this.folder = new File(fileLocation, this.name);
        folder.mkdirs();
        File srcFolder = new File(folder, "src");
        srcFolder.mkdirs();
        this.headerFile = new File(srcFolder, this.name + ".h");
        this.sourceFile = new File(srcFolder, this.name + ".cpp");
        this.keywordsTXT = new File(folder, "keywords.txt");
    }

    public LibraryMaker(String headerContent, String sourceContent, String keywordsContent, Example[] examples, String name) {
        this.headerContent = headerContent;
        this.sourceContent = sourceContent;
        this.keywordsContent = keywordsContent;
        if (examples != null) {
            this.examples = examples;
        } else {
            this.examples = new Example[0];
        }
        this.name = name;
        this.folder = new File(this.name);
        folder.mkdirs();
        File srcFolder = new File(folder, "src");
        srcFolder.mkdirs();
        this.headerFile = new File(srcFolder, this.name + ".h");
        this.sourceFile = new File(srcFolder, this.name + ".cpp");
        this.keywordsTXT = new File(folder, "keywords.txt");
    }

    public void makeLibrary() {
        try {
            if (fileLines != null) {
                writeHeader();
                writeSource();
                writeKeywords();
            } else {
                try (BufferedWriter hw = new BufferedWriter(new FileWriter(headerFile))) {
                    hw.write(headerContent);
                }
                try (BufferedWriter sw = new BufferedWriter(new FileWriter(sourceFile))) {
                    sw.write(sourceContent);
                }
                try (BufferedWriter kw = new BufferedWriter(new FileWriter(keywordsTXT))) {
                    kw.write(keywordsContent);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Vector<Variable> getVariableVector() {
        return VariableVector;
    }

    private void writeHeader() throws IOException {
        List<Variable> acceptedVariable = new ArrayList<>();
        try (BufferedWriter headerWriter = new BufferedWriter(new FileWriter(headerFile))) {
            headerWriter.write("#ifndef " + name.toUpperCase() + "_h\n");
            headerWriter.write("#define " + name.toUpperCase() + "_h\n\n");
            headerWriter.write("#include \"Arduino.h\"\n\n");
            headerWriter.write("class " + name + " {\n");
            headerWriter.write("\tpublic:\n");
            for (String line : fileLines) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                if ((line.startsWith("int ") || line.startsWith("string ") ||
                        line.startsWith("double ") || line.startsWith("char ") ||
                        line.startsWith("bool ") || line.startsWith("byte ") ||
                        line.startsWith("int[") || line.startsWith("string[") ||
                        line.startsWith("double[") || line.startsWith("char[") ||
                        line.startsWith("bool[") || line.startsWith("byte["))
                        && !line.contains("{")) {
                    Variable varObj = getVariable(line);
                    VariableVector.add(varObj);
                    while (!varObj.isDecisionMade()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(e);
                        }
                    }
                    if (varObj.isAccepted()) {
                        acceptedVariable.add(varObj);
                        constructorParams.add(varObj.getDeclaration());
                    }
                } else if (line.contains("(") && line.contains(")") && line.contains("{") && !line.contains("}")) {
                    String prototype = line.substring(0, line.indexOf("{")).trim() + ";";
                    methodPrototypes.add(prototype);
                }
            }
            StringBuilder constructorSignature = new StringBuilder();
            constructorSignature.append(name).append("(");
            for (int i = 0; i < acceptedVariable.size(); i++) {
                constructorSignature.append(acceptedVariable.get(i).getDeclaration());
                if (i < acceptedVariable.size() - 1) {
                    constructorSignature.append(", ");
                }
            }
            constructorSignature.append(");");
            headerWriter.write("\t\t" + constructorSignature + "\n");
            for (String prototype : methodPrototypes) {
                headerWriter.write("\t\t" + prototype + "\n");
            }
            headerWriter.write("\n\tprivate:\n");
            for (Variable var : acceptedVariable) {
                headerWriter.write("\t\t" + var.getMemberDeclaration() + ";\n");
            }
            headerWriter.write("};\n\n");
            headerWriter.write("#endif\n");
        }
    }

    private Variable getVariable(String line) {
        int firstSpace = line.indexOf(" ");
        String type = line.substring(0, firstSpace);
        String remainder = line.substring(firstSpace).trim();
        int endIndex = remainder.length();
        int spaceIndex = remainder.indexOf(" ");
        int semicolonIndex = remainder.indexOf(";");
        int equalsIndex = remainder.indexOf("=");
        if (spaceIndex != -1) {
            endIndex = spaceIndex;
        }
        if (semicolonIndex != -1 && semicolonIndex < endIndex) {
            endIndex = semicolonIndex;
        }
        if (equalsIndex != -1 && equalsIndex < endIndex) {
            endIndex = equalsIndex;
        }
        String varName = remainder.substring(0, endIndex).trim();
        needsAccepting = true;
        return new Variable(type, varName);
    }

    private void writeSource() throws IOException {
        boolean functionBlock = false;
        try (BufferedWriter sourceWriter = new BufferedWriter(new FileWriter(sourceFile))) {
            sourceWriter.write("#include \"Arduino.h\"\n");
            sourceWriter.write("#include \"" + name.toUpperCase() + ".h\"\n\n");
            sourceWriter.write(name + "::" + name + "(");
            for (int i = 0; i < constructorParams.size(); i++) {
                sourceWriter.write(constructorParams.get(i));
                if (i < constructorParams.size() - 1) {
                    sourceWriter.write(", ");
                }
            }
            sourceWriter.write(") { \n");
            for (String param : constructorParams) {
                String[] items = param.split(" ");
                sourceWriter.write("\t_" + items[1] + " = " + items[1] + ";\n");
            }
            sourceWriter.write("}\n");
            sourceWriter.newLine();
            for (String line : fileLines) {
                if (line.equals("}")) {
                    sourceWriter.write("}\n");
                    sourceWriter.newLine();
                    functionBlock = false;
                }
                if (functionBlock) {
                    sourceWriter.write("\t" + line + "\n");
                }
                line = line.trim();
                if (line.isEmpty())
                    continue;
                if ((line.startsWith("int ") || line.startsWith("string ") ||
                        line.startsWith("double ") || line.startsWith("char ") ||
                        line.startsWith("bool ") || line.startsWith("byte ") || line.startsWith("void "))
                        && (line.contains("{") && !functionBlock)) {
                    functionBlock = true;
                    String[] functionLineArray = line.split(" ");
                    sourceWriter.write(functionLineArray[0] + "::" + functionLineArray[1]);
                    int i = 1;
                    while(true) {
                        i++;
                        if (functionLineArray.length >= i+1) {
                            sourceWriter.write(" " + functionLineArray[i]);
                        } else {
                            break;
                        }
                    }
                    sourceWriter.newLine();
                }
            }
        }
    }

    public void writeKeywords() throws IOException {
        try (BufferedWriter keywords = new BufferedWriter(new FileWriter(keywordsTXT))) {
            keywords.write(name);
            keywords.write("\tKEYWORD1\n");
            for (String prototype : methodPrototypes) {
                int parenIndex = prototype.indexOf('(');
                if (parenIndex != -1) {
                    String methodName = prototype.substring(0, parenIndex).trim();
                    String[] parts = methodName.split(" ");
                    String cleanName = parts[parts.length - 1];
                    keywords.write(cleanName);
                    keywords.write("\tKEYWORD2\n");
                }
            }
        }
    }

    public void addExample(File example, String exampleName) throws IOException {
        File examplesFolder = new File(folder, "examples");
        if (!examplesFolder.exists()) {
            examplesFolder.mkdirs();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(example));
             BufferedWriter writer = new BufferedWriter(new FileWriter(new File(examplesFolder, exampleName)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public void addExample(Example example) throws IOException {
        File examplesFolder = new File(folder, "examples");
        if (!examplesFolder.exists()) {
            examplesFolder.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(examplesFolder, example.getName())))) {
            writer.write(example.getFileInformation());
        }
    }

    public Library saveLibrary() {
        try {
            String headerContent = new String(Files.readAllBytes(headerFile.toPath()));
            String sourceContent = new String(Files.readAllBytes(sourceFile.toPath()));
            String keywordsContent = new String(Files.readAllBytes(keywordsTXT.toPath()));
            File examplesFolder = new File(folder, "examples");
            Example[] examplesArray = new Example[0];
            if (examplesFolder.exists() && examplesFolder.isDirectory()) {
                File[] exampleFiles = examplesFolder.listFiles();
                if (exampleFiles != null && exampleFiles.length > 0) {
                    examplesArray = new Example[exampleFiles.length];
                    for (int i = 0; i < exampleFiles.length; i++) {
                        String fileContent = new String(Files.readAllBytes(exampleFiles[i].toPath()));
                        examplesArray[i] = new Example(exampleFiles[i].getName(), fileContent);
                    }
                }
            }
            List<String> acceptedVars = new ArrayList<>();
            for (Variable var : VariableVector) {
                if (var.isAccepted()) {
                    acceptedVars.add(var.getDeclaration());
                }
            }
            return new Library(name, headerContent, sourceContent, keywordsContent, examplesArray, acceptedVars);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Library saveLibraryFromStrings() {
        List<String> acceptedVars = new ArrayList<>();
        for (Variable var : VariableVector) {
            if (var.isAccepted()) {
                acceptedVars.add(var.getDeclaration());
            }
        }
        return new Library(name, headerContent, sourceContent, keywordsContent, examples, acceptedVars);
    }
}
