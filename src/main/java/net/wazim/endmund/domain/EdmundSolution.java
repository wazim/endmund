package net.wazim.endmund.domain;

public class EdmundSolution {

    private final GuardianClueAndSolution guardianClueAndSolution;
    private final String edmundSolution;
    private final int id;
    private final boolean hinted;

    public EdmundSolution(GuardianClueAndSolution guardianClueAndSolution, String edmundSolution, int id, boolean hinted) {
        this.guardianClueAndSolution = guardianClueAndSolution;
        this.edmundSolution = edmundSolution;
        this.id = id;
        this.hinted = hinted;
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

    public boolean isHinted() {
        return hinted;
    }

}
