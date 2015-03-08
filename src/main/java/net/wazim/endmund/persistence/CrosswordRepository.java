package net.wazim.endmund.persistence;

import net.wazim.endmund.domain.EdmundSolution;
import net.wazim.endmund.domain.GuardianClueAndSolution;

import java.util.List;

public interface CrosswordRepository {

    int getIdForCrossword();

    void setNextIdForCrossword(int nextId);

    void saveClueAndSolution(GuardianClueAndSolution guardianClueAndSolution);

    List<GuardianClueAndSolution> getAllGuardianCluesAndSolutions();

    List<EdmundSolution> getAllEdmundSolutions();

    void addEndmundSolution(GuardianClueAndSolution cluesAndSolution, String solution);
}
