package com.example.relay.catalog.transactions;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Demo bean for the self-invocation pitfall: outerMethod() calls innerMethod()
 * via `this.`, bypassing the Spring AOP proxy entirely.
 */
@Component
public class SelfInvocationDemoService {

	public record TransactionSnapshot(String outerTxName, String innerTxName) {}

	@Transactional
	public TransactionSnapshot outerMethod() {
		String outerTxName = TransactionSynchronizationManager.getCurrentTransactionName();
		String innerTxName = this.innerMethod(); // self-invocation — bypasses the proxy
		return new TransactionSnapshot(outerTxName, innerTxName);
	}

	// TODO(human): implement innerMethod().
	// It should be annotated @Transactional(propagation = Propagation.REQUIRES_NEW)
	// and return TransactionSynchronizationManager.getCurrentTransactionName(),
	// so the test can compare it against outerTxName.
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String innerMethod() {
        return TransactionSynchronizationManager.getCurrentTransactionName();
	}
}
