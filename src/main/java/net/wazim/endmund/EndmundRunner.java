package net.wazim.endmund;

import net.wazim.endmund.client.GuardianCrosswordClient;
import net.wazim.endmund.controllers.EndmundController;
import net.wazim.endmund.persistence.CloudSqlCrosswordRepository;
import net.wazim.endmund.persistence.CrosswordRepository;
import net.wazim.endmund.utils.HintToggler;
import net.wazim.endmund.utils.NextIdGenerator;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import static com.google.common.base.Verify.verify;

@SuppressWarnings("unused")
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
public class EndmundRunner {

    private boolean hintToggle = false;

    public static void main(String[] args) {
        SpringApplication.run(EndmundRunner.class, args);
    }

    public EndmundRunner() {
        verify(System.getProperty("database") != null);
        verify(System.getProperty("schedule.delay") != null);
        verify(System.getenv("DATABASE_PASSWORD") != null || System.getProperty("database.password") != null);
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

        if(System.getProperty("database.password") != null) {
            dataSource.setPassword(System.getProperty("database.password"));
        } else {
            dataSource.setPassword(System.getenv("DATABASE_PASSWORD"));
        }

        return new JdbcTemplate(dataSource);
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        if(System.getenv("PORT") != null) {
            factory.setPort(Integer.parseInt(System.getenv("PORT")));
        } else {
            factory.setPort(8080);
        }
        return factory;
    }

    @Bean
    public HintToggler hintToggler() {
        return new HintToggler();
    }

}