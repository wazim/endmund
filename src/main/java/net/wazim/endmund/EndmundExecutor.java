package net.wazim.endmund;

import net.wazim.endmund.client.EndmundResponseHandler;
import net.wazim.endmund.client.GuardianCrosswordClient;
import net.wazim.endmund.domain.GuardianClueAndSolution;
import net.wazim.endmund.persistence.CrosswordRepository;
import net.wazim.endmund.utils.HintToggler;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@SuppressWarnings("unused")
@Component
public class EndmundExecutor {

    private Logger logger = LoggerFactory.getLogger(EndmundExecutor.class);
    public static final String BASE_EDMUND_URL = "http://localhost:9090";

    @Autowired
    private HintToggler hintToggler;
    @Autowired
    private GuardianCrosswordClient guardianCrosswordClient;
    @Autowired
    private CrosswordRepository crosswordRepository;
    private String edmundJarName = "project-Edmund.jar";
    private Process edmundRunningProcess;
    public static final String DOWNLOAD_LINK_FOR_LATEST_RELEASE = "https://s3-eu-west-1.amazonaws.com/project-edmund/project-Edmund.jar";

    @Scheduled(fixedDelayString = "${schedule.delay}", initialDelay = 0)
    public void runEndToEndTesting() {
        logger.info("Starting schedule...");
        try {
            deployAndStartEdmund();

            runCluesAgainstEdmund(guardianCrosswordClient.getCluesAndSolutions());

            killAndDeleteEdmund();
        } catch (Exception e) {
            logger.error("Failed to deploy and start Endmund: " + e);
        }
    }

    private void runCluesAgainstEdmund(List<GuardianClueAndSolution> cluesAndSolutions) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setErrorHandler(new EndmundResponseHandler());
        for (GuardianClueAndSolution cluesAndSolution : cluesAndSolutions) {
            try {
                ResponseEntity<String> solution = createRequest(restTemplate, cluesAndSolution);

                if (solution.getStatusCode().is2xxSuccessful()) {
                    String solutionElement = extractSolution(solution.getBody());
                    if (solutionElement.length() > 2) {
                        crosswordRepository.addEndmundSolution(cluesAndSolution, solutionElement, hintToggler.isToggleOn());
                    }
                }
            } catch (Exception ignored) {
            }
        }
        logger.info("Finished running clues!");
    }

    private ResponseEntity<String> createRequest(RestTemplate restTemplate, GuardianClueAndSolution cluesAndSolution) {
        if (hintToggler.isToggleOn()) {
            return restTemplate.getForEntity(BASE_EDMUND_URL + "/solve" +
                    "?clue=" + cluesAndSolution.getClue() +
                    "&hint=" + createHint(cluesAndSolution) +
                    "&length=" + cluesAndSolution.getSolutionLength(), String.class
            );
        } else {
            return restTemplate.getForEntity(BASE_EDMUND_URL + "/solve" +
                    "?clue=" + cluesAndSolution.getClue() +
                    "&hint=" +
                    "&length=" + cluesAndSolution.getSolutionLength(), String.class
            );
        }
    }

    public static String createHint(GuardianClueAndSolution cluesAndSolution) {
        StringBuilder hint = new StringBuilder();

        hint.append(cluesAndSolution.getClueSolution().substring(0, 1));
        int numberOfFollowingLetters = cluesAndSolution.getSolutionLength() - 1;
        for(int i=0; i<numberOfFollowingLetters; i++) {
            hint.append(".");
        }

        return hint.toString().toLowerCase();
    }

    private static String extractSolution(String body) {
        JSONArray solution = new JSONArray(body);
        return solution.get(0).toString().toUpperCase();
    }

    private void killAndDeleteEdmund() throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        logger.info("Killing Edmund");
        edmundRunningProcess.destroyForcibly();

        while (edmundRunningProcess.isAlive()) {
            Thread.sleep(500);
            logger.info("Waiting until Edmund is dead!");
        }

        runtime.exec("rm -rf " + edmundJarName);
        runtime.exec("rm -rf dictionary");
        runtime.exec("rm -rf thesaurus.txt");
        logger.info("Deleted Edmund JAR");
    }

    private void deployAndStartEdmund() throws InterruptedException, IOException {
        Runtime runtime = Runtime.getRuntime();
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setErrorHandler(new EndmundResponseHandler());


        String[] piecesOfUrl = DOWNLOAD_LINK_FOR_LATEST_RELEASE.split("/");
        edmundJarName = piecesOfUrl[piecesOfUrl.length - 1];

        logger.info("Downloading Edmund JAR");

        FileUtils.copyURLToFile(new URL(DOWNLOAD_LINK_FOR_LATEST_RELEASE), new File(edmundJarName));

        edmundRunningProcess = runtime.exec("java -jar " + edmundJarName);
        logger.info("Running Edmund JAR");
        try {
            int edmundResponse = 0;
            while (edmundResponse != 200) {
                Thread.sleep(5000);
                try {
                    ResponseEntity<String> responseFromEdmund = restTemplate.getForEntity(BASE_EDMUND_URL, String.class);
                    edmundResponse = responseFromEdmund.getStatusCode().value();
                    logger.info("Response from Edmund: " + edmundResponse);
                } catch (Exception e) {
                    logger.warn(String.format("Couldn't connect because %s. Retrying...", e.getMessage()));
                }
            }
        } catch (Exception ignored) {
            logger.error("Could not connect to Edmund");
        }
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(10000);
        factory.setConnectTimeout(10000);
        return factory;
    }

}
