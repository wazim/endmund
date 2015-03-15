package net.wazim.endmund.utils;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class NextIdGenerator {

    private JdbcTemplate jdbcTemplate;

    public NextIdGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getNextId() {
        int lastId = getLastId();

        return lastId + 1;
    }

    private int getLastId() {
        List<Integer> id = jdbcTemplate.query("SELECT id FROM solutions ORDER BY id DESC LIMIT 1", (resultSet, i) -> {
            return resultSet.getInt("id");
        });

        if (id.size() > 0) {
            return id.get(0);
        }

        return -1;
    }

}
