package net.wazim.endmund.persistence;

import net.wazim.endmund.domain.EdmundSolution;
import net.wazim.endmund.domain.GuardianClueAndSolution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.support.GenericMessage;

import java.util.ArrayList;
import java.util.List;

public class InMemoryCrosswordRepository implements CrosswordRepository {

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    private int crosswordId = 26505;
    private List<GuardianClueAndSolution> allCluesAndSolutions = new ArrayList<>();
    private List<EdmundSolution> edmundsSolutions = new ArrayList<>();

    @Override
    public int getIdForCrossword() {
        return crosswordId;
    }

    @Override
    public void setNextIdForCrossword(int nextId) {
        crosswordId = nextId;
    }

    @Override
    public void saveClueAndSolution(GuardianClueAndSolution guardianClueAndSolution) {
        allCluesAndSolutions.add(guardianClueAndSolution);
    }

    @Override
    public List<GuardianClueAndSolution> getAllGuardianCluesAndSolutions() {
        return allCluesAndSolutions;
    }

    @Override
    public List<EdmundSolution> getAllEdmundSolutions() {
        return edmundsSolutions;
    }

    @Override
    public void addEndmundSolution(GuardianClueAndSolution cluesAndSolution, String solution) {
        System.out.println(String.format("Edmund solved the clue -- %s -- as %s and the correct answer was %s",
                cluesAndSolution.getClue(),
                solution,
                cluesAndSolution.getClueSolution()));
        EdmundSolution edmundSolution = new EdmundSolution(cluesAndSolution, solution);
        simpMessageSendingOperations.convertAndSend("/topic/solutions", edmundSolution);
        edmundsSolutions.add(edmundSolution);
    }

}
