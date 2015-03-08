package net.wazim.endmund.domain;

public class EdmundSolution {

    private final GuardianClueAndSolution guardianClueAndSolution;
    private final String edmundSolution;

    public EdmundSolution(GuardianClueAndSolution guardianClueAndSolution, String edmundSolution) {
        this.guardianClueAndSolution = guardianClueAndSolution;
        this.edmundSolution = edmundSolution;
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
}
