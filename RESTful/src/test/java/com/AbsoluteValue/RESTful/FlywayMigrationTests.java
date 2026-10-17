package com.AbsoluteValue.RESTful;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:flywaydb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true"
})
@ActiveProfiles("test")
class FlywayMigrationTests extends JwtIntegrationTestSupport {

    @Test
    void migrationsCreateSchemaOnEmptyDatabase() {
    }
}
