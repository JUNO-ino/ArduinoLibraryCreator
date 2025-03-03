package LibraryCreation;

public class Variable {
    private final String type;
    private final String name;
    private boolean accepted = false;
    private boolean decisionMade = false;

    public Variable(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getDeclaration() {
        return type + " " + name + " ";
    }

    public String getMemberDeclaration() {
        return type + " _" + name;
    }

    public synchronized void accept() {
        accepted = true;
        decisionMade = true;
        notifyAll();
    }

    public synchronized void deny() {
        accepted = false;
        decisionMade = true;
        notifyAll();
    }

    public synchronized boolean isAccepted() {
        return accepted;
    }

    public synchronized boolean isDecisionMade() {
        return decisionMade;
    }

    public synchronized void waitForDecision() {
        while (!decisionMade) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
