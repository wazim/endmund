package net.wazim.endmund.persistence;

import net.wazim.endmund.domain.EdmundSolution;
import net.wazim.endmund.domain.GuardianClueAndSolution;
import net.wazim.endmund.utils.NextIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@SuppressWarnings("unused")
public class CloudSqlCrosswordRepository implements CrosswordRepository {

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    private Logger logger = LoggerFactory.getLogger(CloudSqlCrosswordRepository.class);
    private final JdbcTemplate jdbcTemplate;
    private final NextIdGenerator nextIdGenerator;
    private List<GuardianClueAndSolution> allCluesAndSolutions = new ArrayList<>();
    private List<EdmundSolution> edmundsSolutions = new ArrayList<>();

    public CloudSqlCrosswordRepository(JdbcTemplate jdbcTemplate, NextIdGenerator nextIdGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.nextIdGenerator = nextIdGenerator;
    }

    @Override
    public int getIdForCrossword() {
        List<Integer> id = jdbcTemplate.query("SELECT id FROM crossword WHERE ref = 0", (resultSet, i) -> resultSet.getInt("id"));
        return id.get(0);
    }

    @Override
    public void setNextIdForCrossword(int nextId) {
        jdbcTemplate.update("UPDATE crossword SET id = " + nextId + " WHERE ref = 0");
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
        return jdbcTemplate.query("SELECT * FROM solutions", (resultSet, i) -> {
            GuardianClueAndSolution guardianClueAndSolution = new GuardianClueAndSolution(
                    resultSet.getString("clue"),
                    resultSet.getString("solution"),
                    resultSet.getInt("solution_length")
            );
            return new EdmundSolution(
                    guardianClueAndSolution,
                    resultSet.getString("edmund_solution"),
                    resultSet.getInt("id"),
                    resultSet.getBoolean("hinted")
            );
        });
    }

    @Override
    public void addEndmundSolution(GuardianClueAndSolution cluesAndSolution, String solution, boolean hinted) {
        int numberOfSolutionsBeforeInsert = numberOfSolutionsInDatabase();

        EdmundSolution edmundSolution = new EdmundSolution(cluesAndSolution, solution, nextIdGenerator.getNextId(), hinted);

        jdbcTemplate.update(format(
                "INSERT INTO solutions " +
                        "(id, clue, solution_length, solution, edmund_solution, hinted)" +
                        " VALUES " +
                        "(%d, '%s', %d, '%s', '%s', '%s')",
                edmundSolution.getId(),
                edmundSolution.getGuardianClueAndSolution().getClue(),
                edmundSolution.getGuardianClueAndSolution().getSolutionLength(),
                edmundSolution.getGuardianClueAndSolution().getClueSolution(),
                edmundSolution.getEdmundSolution(),
                hinted
        ));

        waitUntilInsertConfirmed(numberOfSolutionsBeforeInsert);

        logger.info(format("Edmund solved the clue -- %s -- as %s and the correct answer was %s",
                cluesAndSolution.getClue(),
                solution,
                cluesAndSolution.getClueSolution()));

        simpMessageSendingOperations.convertAndSend("/topic/solutions", edmundSolution);
    }

    private void waitUntilInsertConfirmed(int numberOfSolutionsBeforeInsert) {
        int numberOfSolutionsAfterInsert = numberOfSolutionsInDatabase();

        while (numberOfSolutionsAfterInsert != (numberOfSolutionsBeforeInsert + 1)) {
            logger.info("Endmund hasn't inserted into the database yet... One second...");
            numberOfSolutionsAfterInsert = numberOfSolutionsInDatabase();
            try {
                Thread.sleep(700);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private int numberOfSolutionsInDatabase() {
        return jdbcTemplate.query("SELECT COUNT (*) FROM solutions", (resultSet, i) -> resultSet.getInt("count")).get(0);
    }

}
