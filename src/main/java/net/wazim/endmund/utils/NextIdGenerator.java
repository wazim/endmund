package net.wazim.endmund.utils;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class NextIdGenerator {

    private JdbcTemplate jdbcTemplate;

    public NextIdGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getNextId() {
        List<Integer> id = jdbcTemplate.query("SELECT id FROM id_generator", (resultSet, i) -> {
            return resultSet.getInt("id");
        });

        if (id.size() > 0) {
            Integer idToReturn = id.get(0);
            jdbcTemplate.update("UPDATE id_generator SET id=" + idToReturn + 1);
            return idToReturn;
        }

        return 0;
    }

}
