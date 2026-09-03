package com.example.relay;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

	@Test
	void verifiesModularStructure() {
		ApplicationModules.of(RelayApplication.class).verify();
	}

}