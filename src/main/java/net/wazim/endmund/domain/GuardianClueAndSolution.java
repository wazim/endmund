package net.wazim.endmund.domain;

public class GuardianClueAndSolution {

    private final String clue;
    private final String clueSolution;
    private final int solutionLength;

    public GuardianClueAndSolution(String clue, String clueSolution, int solutionLength) {
        this.clue = clue;
        this.clueSolution = clueSolution;
        this.solutionLength = solutionLength;
    }

    public String getClue() {
        return clue;
    }

    public String getClueSolution() {
        return clueSolution;
    }

    public int getSolutionLength() {
        return solutionLength;
    }

    @Override
    public String toString() {
        return clue +  " = " + clueSolution + "("+solutionLength+")";
    }
}
