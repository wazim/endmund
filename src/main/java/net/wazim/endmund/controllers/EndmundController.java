package net.wazim.endmund.controllers;

import net.wazim.endmund.client.GuardianCrosswordClient;
import net.wazim.endmund.domain.EdmundSolution;
import net.wazim.endmund.persistence.CrosswordRepository;
import net.wazim.endmund.utils.HintToggler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@SuppressWarnings("unused")
@RestController
@RequestMapping(value = "endmund")
public class EndmundController {

    private Logger logger = LoggerFactory.getLogger(EndmundController.class);

    @Autowired
    private GuardianCrosswordClient guardianCrosswordClient;
    @Autowired
    private CrosswordRepository crosswordRepository;
    @Autowired
    private HintToggler hintToggle;

    @RequestMapping(value = "clues", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<EdmundSolution> allCluesAndSolutions() {
        return crosswordRepository.getAllEdmundSolutions();
    }

    @RequestMapping(value = "toggle", method = POST)
    public void toggleHints() {
        hintToggle.toggleHints();
        logger.info("Hint Toggle is set to : " + hintToggle.isToggleOn());
    }

}
