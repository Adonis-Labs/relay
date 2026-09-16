package com.example.relay.catalog.transactions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.example.relay.TestcontainersConfiguration;
import com.example.relay.catalog.transactions.SelfInvocationDemoService.TransactionSnapshot;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves that self-invocation (this.innerMethod()) bypasses the Spring proxy:
 * despite REQUIRES_NEW, innerMethod() never actually gets its own transaction.
 */
@SpringBootTest
@Import({ TestcontainersConfiguration.class, SelfInvocationDemoService.class })
class SelfInvocationPitfallDemoTest {

	@Autowired
	SelfInvocationDemoService demoService;

	@Test
	void selfInvocationIgnoresRequiresNewAndReusesOuterTransaction() {
		TransactionSnapshot snapshot = demoService.outerMethod();

		assertThat(snapshot.innerTxName()).isEqualTo(snapshot.outerTxName());
	}
}
