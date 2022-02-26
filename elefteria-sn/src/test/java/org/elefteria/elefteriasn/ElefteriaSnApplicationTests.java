package org.elefteria.elefteriasn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elefteria.elefteriasn.controller.UserController;
import org.elefteria.elefteriasn.dto.PostDto;
import org.elefteria.elefteriasn.dto.UserDto;
import org.elefteria.elefteriasn.security.jwt.UsernameAndPasswordRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)    // This annotation is needed to make non-static @BeforeAll methods
class ElefteriaSnApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private UserController userController;

	@Autowired
	private TestRestTemplate  restTemplate;

	private String jwtToken = "";

	@Test
	void contextLoads() {
		assertThat(userController).isNotNull();
	}

	@Test
	@BeforeAll
	void login() {
		try {
			UsernameAndPasswordRequest usernameAndPasswordRequest = new UsernameAndPasswordRequest("mary", "mary");

			String requestBody = new ObjectMapper().writeValueAsString(usernameAndPasswordRequest);

			HttpEntity<String> loginEntity = new HttpEntity<>(requestBody, getHeaders());

			HttpHeaders headers = restTemplate.exchange("http://localhost:" + port + "/api/login", HttpMethod.POST, loginEntity, Object.class).getHeaders();

			jwtToken = headers.get("Authorization").get(0);

			System.out.println(jwtToken);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getPostShouldReturnPost() {
		HttpHeaders headers = getHeaders();
		HttpEntity<String> jwtEntity = new HttpEntity<>(headers);

		PostDto postDto = this.restTemplate.exchange("http://localhost:" + port + "/api/posts/1", HttpMethod.GET, jwtEntity, PostDto.class).getBody();

		assertThat(postDto.getTitle()).isEqualTo("First post made from postman");
		assertThat(postDto.getText()).isEqualTo("Testing new way to storing images");
		assertThat(postDto.getUsername()).isEqualTo("obamamonkey");
		assertThat(postDto.getCreatedDate()).isEqualTo(LocalDateTime.ofInstant(Instant.ofEpochMilli(1630174624000L), TimeZone.getDefault().toZoneId()));
	}

	@Test
	public void getUserShouldReturnUser() throws Exception {
        HttpHeaders headers = getHeaders();
        HttpEntity<String> jwtEntity = new HttpEntity<>(headers);

		UserDto userDto = this.restTemplate.exchange("http://localhost:" + port + "/api/users/obamamonkey", HttpMethod.GET, jwtEntity, UserDto.class).getBody();

		System.out.println(userDto);

		assertThat(userDto.getUsername()).isEqualTo("obamamonkey");
		assertThat(userDto.getLastName()).isEqualTo("Obama");
		assertThat(userDto.getFirstName()).isEqualTo("Monkey");

	}

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Authorization", jwtToken);
        return headers;
    }

}
