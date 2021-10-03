package org.elefteria.elefteriasn;

import org.elefteria.elefteriasn.controller.UserController;
import org.elefteria.elefteriasn.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ElefteriaSnApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private UserController userController;

	@Autowired
	private TestRestTemplate  restTemplate;

	private String jwtToken = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJvYmFtYW1vbmtleSIsImF1dGhvcml0aWVzIjpbeyJhdXRob3JpdHkiOiJST0xFX1VTRVIifV0sImlhdCI6MTYzMjkxNTY0NywiZXhwIjoxNjMzMzA1NjAwfQ.sbOmdNy655rSUqJB2efZ0w3Qct3luyqWBEFxzaKvmDD8LFrBgf6g4HuxByVY2GD_";

	@Test
	void contextLoads() {
		assertThat(userController).isNotNull();
	}

	@Test
	public void getUserShouldReturnUser() throws Exception {
        HttpHeaders headers = getHeaders();
        headers.set("Authorization", jwtToken);
        HttpEntity<String> jwtEntity = new HttpEntity<>(headers);

		UserDto userDto = this.restTemplate.exchange("http://localhost:" + port + "/api/users/obamamonkey", HttpMethod.GET, jwtEntity, UserDto.class).getBody();

		System.out.println(userDto);

		assertThat(userDto.getUsername()).isEqualTo("obamamonkey");
		assertThat(userDto.getLastName()).isEqualTo("Obama");
		assertThat(userDto.getFirstName()).isEqualTo("Monkey");

//		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/api/users/obamamonkey", UserDto.class))
//                .isNotNull();
	}

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

}
