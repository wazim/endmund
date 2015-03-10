package net.wazim.endmund.persistence;

import net.wazim.endmund.domain.EdmundSolution;
import net.wazim.endmund.domain.GuardianClueAndSolution;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.support.GenericMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static net.wazim.endmund.utils.NextIdGenerator.getNextId;

@SuppressWarnings("unused")
public class CloudSqlCrosswordRepository implements CrosswordRepository {

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    private List<GuardianClueAndSolution> allCluesAndSolutions = new ArrayList<>();
    private List<EdmundSolution> edmundsSolutions = new ArrayList<>();
    private JdbcTemplate jdbcTemplate;

    public CloudSqlCrosswordRepository() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName("babar.elephantsql.com");
        dataSource.setDatabaseName(System.getProperty("database"));
        dataSource.setUser(System.getProperty("database"));
        dataSource.setPassword(System.getProperty("database.password"));
        jdbcTemplate = new JdbcTemplate(dataSource);
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
                    resultSet.getInt("id")
            );
        });
    }

    @Override
    public void addEndmundSolution(GuardianClueAndSolution cluesAndSolution, String solution) {
        System.out.println(String.format("Edmund solved the clue -- %s -- as %s and the correct answer was %s",
                cluesAndSolution.getClue(),
                solution,
                cluesAndSolution.getClueSolution()));
        EdmundSolution edmundSolution = new EdmundSolution(cluesAndSolution, solution, getNextId());
        simpMessageSendingOperations.convertAndSend("/topic/solutions", edmundSolution);
        jdbcTemplate.update(String.format("INSERT INTO solutions " +
                        "(id, clue, solution_length, solution, edmund_solution)" +
                        " VALUES " +
                        "(%d, '%s', %d, '%s', '%s')"
                , edmundSolution.getId(),
                edmundSolution.getGuardianClueAndSolution().getClue(),
                edmundSolution.getGuardianClueAndSolution().getSolutionLength(),
                edmundSolution.getGuardianClueAndSolution().getClueSolution(),
                edmundSolution.getEdmundSolution()));
    }

}
