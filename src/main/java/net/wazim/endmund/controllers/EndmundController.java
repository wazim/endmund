package net.wazim.endmund.controllers;

import net.wazim.endmund.client.GuardianCrosswordClient;
import net.wazim.endmund.domain.EdmundSolution;
import net.wazim.endmund.domain.GuardianClueAndSolution;
import net.wazim.endmund.persistence.CrosswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@SuppressWarnings("unused")
@RestController
@RequestMapping(value = "endmund")
public class EndmundController {

    @Autowired
    private GuardianCrosswordClient guardianCrosswordClient;
    @Autowired
    private CrosswordRepository crosswordRepository;

    @RequestMapping(value = "clues", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<EdmundSolution> allCluesAndSolutions() {
        return crosswordRepository.getAllEdmundSolutions();
    }

}
