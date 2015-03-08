package net.wazim.endmund.client;

import net.wazim.endmund.domain.GuardianClueAndSolution;
import net.wazim.endmund.persistence.CrosswordRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GuardianCrosswordClient {

    private final String baseGuardianCrosswordUri;
    private final CrosswordRepository crosswordRepository;
    private final RestTemplate client;

    public GuardianCrosswordClient(final String baseGuardianCrosswordUri, CrosswordRepository crosswordRepository) {
        this.baseGuardianCrosswordUri = baseGuardianCrosswordUri;
        this.crosswordRepository = crosswordRepository;
        client = new RestTemplate();
        client.setErrorHandler(new EndmundResponseHandler());
    }

    public List<GuardianClueAndSolution> getCluesAndSolutions() {
        checkOrUpdateId();

        ResponseEntity<String> crypticCrossWordPage = client.getForEntity(baseGuardianCrosswordUri + crosswordRepository.getIdForCrossword(), String.class);

        List<GuardianClueAndSolution> allCluesAndSolutions = new ArrayList<>();
        List<GuardianClueAndSolution> allAcrossCluesAndSolutions = getAllCluesAndSolutions(crypticCrossWordPage.getBody(), "across");
        List<GuardianClueAndSolution> allDownCluesAndSolutions = getAllCluesAndSolutions(crypticCrossWordPage.getBody(), "down");
        crosswordRepository.setNextIdForCrossword(crosswordRepository.getIdForCrossword() + 1);

        allCluesAndSolutions.addAll(allAcrossCluesAndSolutions);
        allCluesAndSolutions.addAll(allDownCluesAndSolutions);
        return allCluesAndSolutions;

    }

    private void checkOrUpdateId() {
        int currentId = crosswordRepository.getIdForCrossword();
        int nextId = crosswordRepository.getIdForCrossword() + 1;
        if (!client.getForEntity(baseGuardianCrosswordUri + crosswordRepository.getIdForCrossword(), String.class).getStatusCode().is2xxSuccessful()) {
            System.out.println("Can't retrieve cryptic crossword for " + currentId);
            if (client.getForEntity(baseGuardianCrosswordUri + nextId, String.class).getStatusCode().is2xxSuccessful()) {
                System.out.println("Setting cryptic crossword id to " + nextId);
                crosswordRepository.setNextIdForCrossword(nextId);
            }
        } else {
            System.out.println(crosswordRepository.getIdForCrossword() + " is a retrievable crossword");
        }
    }

    private List<GuardianClueAndSolution> getAllCluesAndSolutions(String body, String direction) {
        List<GuardianClueAndSolution> guardianCluesAndSolutions = new ArrayList<>();
        Map<Integer, String> clueSolutions = new TreeMap<>();
        Map<Integer, String> clues = new TreeMap<>();

        getAllClues(body, clues, direction);
        getAllSolutions(body, clueSolutions, direction);

        for (Map.Entry<Integer, String> clue : clues.entrySet()) {
            try {
                if (!clues.get(clue.getKey()).substring(clues.get(clue.getKey()).indexOf("(")).contains(",")) {
                    Integer solutionLength = Integer.valueOf(clues.get(clue.getKey())
                            .substring(
                                    clues.get(clue.getKey()).indexOf("(") + 1,
                                    clues.get(clue.getKey()).indexOf(")")
                            ));

                    guardianCluesAndSolutions.add(
                            new GuardianClueAndSolution(
                                    clues.get(clue.getKey()).replace("(" + solutionLength + ")", "").trim(),
                                    clueSolutions.get(clue.getKey()).trim(),
                                    solutionLength
                            ));
                }
            } catch (Exception ignored) {
            }
        }
        return guardianCluesAndSolutions;
    }

    private void getAllClues(String body, Map<Integer, String> clues, String direction) {
        Document document = Jsoup.parse(body);
        Elements allClues = document.getElementsByClass("clues-col");
        Element theClues;
        if (direction.equals("across")) {
            theClues = allClues.get(0);
        } else {
            theClues = allClues.get(1);
        }
        Elements eachClue = theClues.select("li");
        for (Element aClue : eachClue) {
            if (!aClue.select("label").text().contains("(") || aClue.getElementsByClass("clue-number").text().contains(",")) {
                continue;
            }
            clues.put(
                    Integer.valueOf(aClue.getElementsByClass("clue-number").text()),
                    aClue.select("label").text().replace(aClue.getElementsByClass("clue-number").text(), "").trim()
            );
        }
    }

    private void getAllSolutions(String body, Map<Integer, String> clues, String direction) {
        int startOfSolutions = body.indexOf("solutions = {}") + 16;
        int endOfSolutions = body.indexOf("var has_numbers");
        String solutionsAsString = body.substring(startOfSolutions, endOfSolutions);
        String[] solutions = solutionsAsString.split(";");

        int clueNumber;
        for (String solution : solutions) {
            clueNumber = getClueNumber(solution, direction);
            putOrAppend(clues, clueNumber, getValue(solution));
        }
        clues.remove(0);
    }

    private void putOrAppend(Map<Integer, String> clues, int clueNumber, String clue) {
        if (clues.containsKey(clueNumber)) {
            String clueValue = clues.get(clueNumber);
            clues.replace(clueNumber, clueValue + clue);
        } else {
            clues.put(clueNumber, getValue(clue));
        }
    }

    private int getClueNumber(String solution, String direction) {
        try {
            int startOfClueNumber = solution.indexOf("solutions[") + 11;
            int endOfClueNumber = solution.indexOf("-" + direction);
            String clueNumber = solution.substring(startOfClueNumber, endOfClueNumber).trim();
            return Integer.valueOf(clueNumber);
        } catch (Exception ignored) {
        }
        return 0;
    }

    private String getValue(String solution) {
        int i = solution.indexOf("=") + 1;
        String actualSolution = solution.substring(i).replace("\"", "");
        return actualSolution.trim();
    }

}
