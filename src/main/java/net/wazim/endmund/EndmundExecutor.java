package net.wazim.endmund;

import net.wazim.endmund.client.EndmundResponseHandler;
import net.wazim.endmund.client.GuardianCrosswordClient;
import net.wazim.endmund.domain.GuardianClueAndSolution;
import net.wazim.endmund.persistence.CrosswordRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
@Component
public class EndmundExecutor {

    public static final String EDMUND_JAR_NAME = "edmund-web-1.0-SNAPSHOT.jar";
    public static final String BASE_EDMUND_URL = "http://localhost:9090";
    @Autowired
    private GuardianCrosswordClient guardianCrosswordClient;
    @Autowired
    private CrosswordRepository crosswordRepository;

    @Scheduled(fixedDelay = 60000)
    public void runEndToEndTesting() {
        System.out.println("Starting schedule...");
        try {
//            deployAndStartEdmund();
            runCluesAgainstEdmund(guardianCrosswordClient.getCluesAndSolutions());

//            deleteEdmund();
        } catch (Exception e) {
            System.out.println("Failed to deploy and start Endmund: " + e);
        }
    }

    private void runCluesAgainstEdmund(List<GuardianClueAndSolution> cluesAndSolutions) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setErrorHandler(new EndmundResponseHandler());
        for (GuardianClueAndSolution cluesAndSolution : cluesAndSolutions) {
            try {
                ResponseEntity<String> solution = restTemplate.getForEntity(BASE_EDMUND_URL + "/solve" +
                        "?clue=" + cluesAndSolution.getClue() +
                        "&hint=" +
                        "&length=" + cluesAndSolution.getSolutionLength(), String.class);
                if (solution.getStatusCode().is2xxSuccessful()) {
                    String solutionElement = extractSolution(solution.getBody());
                    if (solutionElement.length() > 2) {
                        crosswordRepository.addEndmundSolution(cluesAndSolution, solutionElement);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        System.out.println("Finished running clues!");
    }

    private static String extractSolution(String body) {
        JSONArray solution = new JSONArray(body);
        return solution.get(0).toString().toUpperCase();
    }

    private void deleteEdmund() throws IOException {
        Runtime.getRuntime().exec("rm -rf " + EDMUND_JAR_NAME);
        System.out.println("Deleted Edmund JAR");
    }

    private void deployAndStartEdmund() throws InterruptedException, IOException {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Downloading Edmund JAR");
        Process exec = runtime.exec("wget https://github.com/wazim/edmund/releases/download/1.5.1/" + EDMUND_JAR_NAME);
        Thread.sleep(5000);
        runtime.exec("java -jar " + EDMUND_JAR_NAME);
        System.out.println("Running Edmund JAR");
        Thread.sleep(2000);
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new EndmundResponseHandler());

            int edmundResponse = 0;
            while (edmundResponse != 200) {
                Thread.sleep(5000);
                try {
                    ResponseEntity<String> responseFromEdmund = restTemplate.getForEntity(BASE_EDMUND_URL + "/health", String.class);
                    edmundResponse = responseFromEdmund.getStatusCode().value();
                    System.out.println(edmundResponse);
                } catch (Exception e) {
                    System.out.println(String.format("Couldn't connect because %s. Retrying...", e.getMessage()));
                }
            }
        } catch (Exception ignored) {
            System.out.println("Could not connect to Edmund");
        }
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(3500);
        factory.setConnectTimeout(3500);
        return factory;
    }

}
