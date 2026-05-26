package com.techindna.eventsync;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.mockito.Mockito;
import javax.sql.DataSource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(EventSyncApplicationTests.TestDataSourceConfig.class)
class EventSyncApplicationTests {

    @Test
    void contextLoads() {
    }

    @TestConfiguration
    static class TestDataSourceConfig {
        @Bean
        DataSource dataSource() {
            return Mockito.mock(DataSource.class);
        }
    }

}
