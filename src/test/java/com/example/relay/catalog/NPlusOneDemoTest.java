package com.example.relay.catalog;

import java.math.BigDecimal;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import com.example.relay.TestcontainersConfiguration;
import com.example.relay.catalog.internal.domain.Product;
import com.example.relay.catalog.internal.domain.Sku;
import com.example.relay.catalog.internal.domain.SkuStatus;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Measures the cost of N+1 at scale: one JPQL query loads all Products, then
 * touching each Product's lazy `skus` collection fires per-product SELECTs.
 * `Product.skus` carries @BatchSize(size = 2), so instead of one SELECT per
 * Product, Hibernate batches them into ceil(PRODUCT_COUNT / 2) SELECTs.
 * Watch the console (spring.jpa.show-sql=true in
 * src/test/resources/application.properties) to see it happen.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class NPlusOneDemoTest {

	private static final int PRODUCT_COUNT = 5;
	private static final int SKUS_PER_PRODUCT = 2;
	private static final int BATCH_SIZE = 2; // must match @BatchSize on Product.skus

	@Autowired
	EntityManager entityManager;

	@Autowired
	EntityManagerFactory entityManagerFactory;

	@Test
	@Transactional
	void batchSizeTurnsPerProductSelectsIntoBatchedInClauseSelects() {
		for (int p = 0; p < PRODUCT_COUNT; p++) {
			Product product = new Product();
			product.setName("N+1 Demo Product " + p);
			for (int s = 0; s < SKUS_PER_PRODUCT; s++) {
				Sku sku = new Sku();
				sku.setPrice(BigDecimal.TEN);
				sku.setCurrency("USD");
				sku.setStatus(SkuStatus.ACTIVE);
				product.addSku(sku);
			}
			entityManager.persist(product);
		}
		entityManager.flush();
		entityManager.clear(); // drop the persistence context so nothing is cached

		Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
		stats.clear();

		// One query loads every Product...
		var products = entityManager.createQuery("select p from Product p", Product.class)
				.getResultList();

		// ...then touching the lazy `skus` collection fires one batched SELECT per BATCH_SIZE products.
		for (Product product : products) {
			product.getSkus().size();
		}

		int expectedBatches = (int) Math.ceil((double) products.size() / BATCH_SIZE);
		assertThat(stats.getPrepareStatementCount()).isEqualTo(1 + expectedBatches);
	}
}