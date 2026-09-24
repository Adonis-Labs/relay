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
import com.example.relay.catalog.internal.domain.Brand;
import com.example.relay.catalog.internal.domain.Category;
import com.example.relay.catalog.internal.domain.Product;
import com.example.relay.catalog.internal.domain.Sku;
import com.example.relay.catalog.internal.domain.SkuStatus;
import com.example.relay.catalog.internal.repository.ProductRepository;

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

	@Autowired
	ProductRepository productRepository;

	@Test
	@Transactional
	void batchSizeTurnsPerProductSelectsIntoBatchedInClauseSelects() {
		// category/brand are NOT NULL @ManyToOne columns on Product; reusing the same
		// row for every product keeps this test's focus on sku batching (see below).
		Category category = new Category();
		category.setName("N+1 Demo Category");
		entityManager.persist(category);

		Brand brand = new Brand();
		brand.setName("N+1 Demo Brand");
		entityManager.persist(brand);

		for (int p = 0; p < PRODUCT_COUNT; p++) {
			Product product = new Product();
			product.setName("N+1 Demo Product " + p);
			product.setCategory(category);
			product.setBrand(brand);
			for (int s = 0; s < SKUS_PER_PRODUCT; s++) {
				Sku sku = new Sku();
				sku.setPrice(BigDecimal.TEN);
				sku.setCurrency("USD");
				sku.setStatus(SkuStatus.ACTIVE);
				sku.setSkuCode("DEMO-SKU-" + p + "-" + s);
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

		// +2: category/brand default to EAGER with no join-fetch, so each is resolved with
		// its own SELECT the first time it's encountered - but only once, since every
		// product shares the same category/brand row and Hibernate finds it already in the
		// persistence context (by id) for the remaining products.
		int expectedBatches = (int) Math.ceil((double) products.size() / BATCH_SIZE);
		assertThat(stats.getPrepareStatementCount()).isEqualTo(1 + 2 + expectedBatches);
	}

	/**
	 * Products getAll (NX-92) used to eager-load Product.category/Product.brand with a
	 * separate SELECT per product per association. findAllProjectedBy() instead projects
	 * only the columns ProductResponse needs - including category/brand publicId via a
	 * join - so Category/Brand are never loaded as entities and there's nothing left to
	 * N+1 on, regardless of how many products exist.
	 */
	@Test
	@Transactional
	void projectionQueryStaysAtOneStatementRegardlessOfProductCount() {
		Category category = new Category();
		category.setName("N+1 Demo Category");
		entityManager.persist(category);

		Brand brand = new Brand();
		brand.setName("N+1 Demo Brand");
		entityManager.persist(brand);

		for (int p = 0; p < PRODUCT_COUNT; p++) {
			Product product = new Product();
			product.setName("N+1 Demo Projected Product " + p);
			product.setCategory(category);
			product.setBrand(brand);
			entityManager.persist(product);
		}
		entityManager.flush();
		entityManager.clear();

		Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
		stats.clear();

		var products = productRepository.findAllProjectedBy();

		assertThat(products).hasSize(PRODUCT_COUNT);
		assertThat(stats.getPrepareStatementCount()).isEqualTo(1);
	}
}