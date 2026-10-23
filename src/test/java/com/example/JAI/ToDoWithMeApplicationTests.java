package com.example.JAI;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = com.JAI.ToDoWithMeApplication.class, properties = {
    "spring.datasource.url=jdbc:h2:mem:context;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.data.redis.host=localhost", "spring.data.redis.port=6379",
    "spring.jwt.secret=test-only-secret-with-at-least-thirty-two-bytes",
    "openai.api.key=test-only", "openai.model=test-only", "openai.api.url=http://localhost:1"
})
class ToDoWithMeApplicationTests {

	@Test
	void contextLoads() {
	}

}
