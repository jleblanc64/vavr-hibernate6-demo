package com.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

    @LocalServerPort
    int port;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0").withDatabaseName("database").withPassword("test").withPassword("test");

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.username", mysql::getUsername);
    }

    @Autowired
    TestRestTemplate cli;

    @Test
    public void test() throws Exception {
        var url = "http://localhost:" + port + "/customers";

        // POST customer
        var hdrs = new HttpHeaders();
        hdrs.setContentType(MediaType.APPLICATION_JSON);
        var req = new HttpEntity<>("{\"name\":\"a\",\"number\":3,\"i\":4}", hdrs);
        var resp = cli.postForObject(url, req, String.class);

        // extract ID from created customer
        var om = new ObjectMapper();
        var root = om.readTree(resp);
        long id = root.path("id").longValue();

        // GET by ID
        resp = cli.getForObject(url + "/" + id, String.class);
        var respJ = new JSONObject(resp);
        assertEquals("a", respJ.get("name"));
        assertEquals(3, respJ.get("number"));
        assertEquals(4, respJ.get("i"));

        // LIST
        resp = cli.getForObject(url, String.class);
        root = om.readTree(resp);
        assertThat(root.size()).isEqualTo(1);

        var name = root.path(0).path("name").textValue();
        assertThat(name).isEqualTo("a");

        // DELETE
        cli.delete(url + "/" + id, req, String.class);

        // LIST
        resp = cli.getForObject(url, String.class);
        root = om.readTree(resp);
        assertThat(root.size()).isEqualTo(0);

        // GET by ID should respond 404 NOT FOUND
        int httpCode = cli.getForEntity(url + "/" + id, String.class).getStatusCodeValue();
        assertThat(httpCode).isEqualTo(404);

        // empty name
        req = new HttpEntity<>("{}", hdrs);

        resp = cli.postForObject(url, req, String.class);
        root = om.readTree(resp);
        name = root.path("name").textValue();
        assertThat(name).isEqualTo("default");

        // orders
        req = new HttpEntity<>("{\"name\":\"a\",\"orders\":[{\"description\":\"d\"},{\"description\":\"d2\"}]}", hdrs);
        resp = cli.postForObject(url, req, String.class);

        respJ = new JSONObject(resp);
        assertEquals(2, respJ.getJSONArray("orders").length());
        assertEquals(-10, respJ.getInt("number"));
        assertTrue(respJ.isNull("i"));
    }
}
