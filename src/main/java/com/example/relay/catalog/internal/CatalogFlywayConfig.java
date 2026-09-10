package com.example.relay.catalog.internal;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.boot.jpa.autoconfigure.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogFlywayConfig {

	@Bean
	public Flyway catalogFlyway(DataSource dataSource) {
		Flyway flyway = Flyway.configure()
				.dataSource(dataSource)
				.locations("classpath:db/migration/catalog")
				.schemas("catalog")
				.baselineOnMigrate(true)
				.baselineVersion("2026.09.10.11.51.09")
				.load();
		flyway.migrate();
		return flyway;
	}

	// Forces EntityManagerFactory to wait for catalogFlyway, since spring.flyway.enabled=false skips Boot's automatic ordering
	@Configuration
	static class CatalogFlywayJpaDependencyConfiguration extends EntityManagerFactoryDependsOnPostProcessor {

		CatalogFlywayJpaDependencyConfiguration() {
			super("catalogFlyway");
		}
	}

}
