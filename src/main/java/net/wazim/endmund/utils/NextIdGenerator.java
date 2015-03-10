package net.wazim.endmund.utils;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class NextIdGenerator {

    private JdbcTemplate jdbcTemplate;

    public NextIdGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getNextId() {
        int lastId = getLastId().get(0);

        return lastId++;
    }

    private List<Integer> getLastId() {
        return jdbcTemplate.query("SELECT id FROM solutions ORDER BY id DESC LIMIT 1", (resultSet, i) -> resultSet.getInt("id"));
    }

}
