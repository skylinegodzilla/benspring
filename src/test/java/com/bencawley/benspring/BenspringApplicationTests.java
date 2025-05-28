package com.bencawley.benspring;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// todo undisable this when we start making tests needs to be disabled to to security or database or somthing
@Disabled("Temporarily disabling until DB config is fixed")
@SpringBootTest(
		classes = BenspringApplication.class,
		properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
)
class BenspringApplicationTests {

	@Test
	void contextLoads() {
		// Verifies the ApplicationContext starts without loading Spring Security
	}

}
