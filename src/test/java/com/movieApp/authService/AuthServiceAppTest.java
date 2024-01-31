package com.movieApp.authService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthServiceAppTest {

	@Test
	void contextLoads() {
	}

	@Test
	public void applicationContextTest() {
		AuthServiceApp.main(new String[] {});
	}

}
