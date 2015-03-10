package net.wazim.endmund;

import net.wazim.endmund.client.GuardianCrosswordClient;
import net.wazim.endmund.controllers.EndmundController;
import net.wazim.endmund.persistence.CrosswordRepository;
import net.wazim.endmund.persistence.CloudSqlCrosswordRepository;
import net.wazim.endmund.utils.NextIdGenerator;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@SuppressWarnings("unused")
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
public class EndmundRunner {

    public static void main(String[] args) {
        SpringApplication.run(EndmundRunner.class, args);
    }

    @Bean
    public EndmundController endmundController() {
        return new EndmundController();
    }

    @Bean
    public GuardianCrosswordClient guardianCrosswordClient() {
        return new GuardianCrosswordClient("http://www.theguardian.com/crosswords/cryptic/", crosswordRepository());
    }

    @Bean
    public CrosswordRepository crosswordRepository() {
        return new CloudSqlCrosswordRepository(jdbcTemplate(), nextIdGenerator());
    }

    @Bean
    public EndmundExecutor endmundExecutor() {
        return new EndmundExecutor();
    }

    @Bean
    public NextIdGenerator nextIdGenerator() {
        return new NextIdGenerator(jdbcTemplate());
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName("babar.elephantsql.com");
        dataSource.setDatabaseName(System.getProperty("database"));
        dataSource.setUser(System.getProperty("database"));
        dataSource.setPassword(System.getProperty("database.password"));
        return new JdbcTemplate(dataSource);
    }

}