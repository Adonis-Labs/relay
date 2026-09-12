package com.example.relay.catalog;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Module-scoped: talks only to Flyway + a raw DataSource, no Spring context.
 * Proves the catalog migrations apply cleanly to a brand-new database.
 */
@Testcontainers
class CatalogFlywayMigrationTest {

	@Container
	static final PostgreSQLContainer postgres = new
			PostgreSQLContainer("postgres:18-alpine");

	@Test
	void migratesCatalogSchemaFromScratch() {
		Flyway flyway = Flyway.configure()
				.dataSource(dataSource())
				.locations("classpath:db/migration/catalog")
				.schemas("catalog")
				.load();

		MigrateResult result = flyway.migrate();

		// TODO(human): assert this migration run actually proves the catalog schema
		//		// is healthy. `result` (a Flyway MigrateResult) tells you how many
		//		// migrations ran and which version it landed on. Decide: is that enough,
		//		// or should the test also open a JDBC connection and confirm specific
		//		// tables (e.g. catalog.product, catalog.sku) really exist and are queryable?
		Assertions.assertTrue(result.success);
		Assertions.assertTrue(() -> result.migrationsExecuted > 0);
	}

	private static DataSource dataSource() {
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setUrl(postgres.getJdbcUrl());
		dataSource.setUser(postgres.getUsername());
		dataSource.setPassword(postgres.getPassword());
		return dataSource;
	}

}
