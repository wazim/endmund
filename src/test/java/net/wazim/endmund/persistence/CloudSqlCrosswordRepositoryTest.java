package net.wazim.endmund.persistence;

import net.wazim.endmund.domain.EdmundSolution;
import net.wazim.endmund.domain.GuardianClueAndSolution;
import net.wazim.endmund.utils.NextIdGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class CloudSqlCrosswordRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private NextIdGenerator nextIdGenerator;
    private CloudSqlCrosswordRepository cloudSqlCrosswordRepository;

    @Before
    public void setup() {
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        nextIdGenerator = Mockito.mock(NextIdGenerator.class);
        cloudSqlCrosswordRepository = new CloudSqlCrosswordRepository(jdbcTemplate, nextIdGenerator);
    }

    @Test
    public void canGetAllEdmundSolutions() {
        List<Object> edmundSolutions = asList(new EdmundSolution(new GuardianClueAndSolution("Hello", "Hi", 2), "Hi", 1));
        when(jdbcTemplate.query(any(String.class), any(RowMapper.class)))
                .thenReturn(edmundSolutions);
        assertThat(cloudSqlCrosswordRepository.getAllEdmundSolutions(), is(edmundSolutions));
    }

}