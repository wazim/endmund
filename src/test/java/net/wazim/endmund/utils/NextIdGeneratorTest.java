package net.wazim.endmund.utils;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class NextIdGeneratorTest {

    private NextIdGenerator nextIdGenerator;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = mock(JdbcTemplate.class);
        nextIdGenerator = new NextIdGenerator(jdbcTemplate);
    }

    @Test
    public void canGetTheNextId() {
        when(jdbcTemplate.query(any(String.class), any(RowMapper.class))).thenReturn(asList(0));
        assertThat(nextIdGenerator.getNextId(), is(1));
        when(jdbcTemplate.query(any(String.class), any(RowMapper.class))).thenReturn(asList(1));
        assertThat(nextIdGenerator.getNextId(), is(2));

    }

}