package net.wazim.endmund.domain;

public class EdmundSolution {

    private final GuardianClueAndSolution guardianClueAndSolution;
    private final String edmundSolution;
    private final int id;

    public EdmundSolution(GuardianClueAndSolution guardianClueAndSolution, String edmundSolution, int id) {
        this.guardianClueAndSolution = guardianClueAndSolution;
        this.edmundSolution = edmundSolution;
        this.id = id;
    }

    public String getEdmundSolution() {
        return edmundSolution;
    }

    public GuardianClueAndSolution getGuardianClueAndSolution() {
        return guardianClueAndSolution;
    }

    public boolean isEdmundCorrect() {
        return guardianClueAndSolution.getClueSolution().toUpperCase().contains(edmundSolution.toUpperCase());
    }

    public int getId() {
        return id;
    }

}
