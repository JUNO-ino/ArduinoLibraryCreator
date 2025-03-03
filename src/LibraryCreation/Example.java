package LibraryCreation;

import java.io.Serializable;

public class Example implements Serializable {
    private String name;
    private String fileInformation;

    public Example(String name, String fileInformation) {
        this.name = name;
        this.fileInformation = fileInformation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileInformation() {
        return fileInformation;
    }

    public void setFileInformation(String fileInformation) {
        this.fileInformation = fileInformation;
    }
}
