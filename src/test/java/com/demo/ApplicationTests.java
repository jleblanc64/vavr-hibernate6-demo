package com.demo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.Set;

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
        var req = new HttpEntity<>("{\"name\":\"a\",\"number\":3,\"i\":4}");
        var resp = cli.postForObject(url, req, String.class);
        var respJ = new JSONObject(resp);
        var id = respJ.get("id");

        // GET by ID
        resp = cli.getForObject(url + "/" + id, String.class);
        respJ = new JSONObject(resp);
        assertEquals("a", respJ.get("name"));
        assertEquals(3, respJ.get("number"));
        assertEquals(3, respJ.get("numberOpt"));
        assertEquals(4, respJ.get("i"));

        var orders = respJ.getJSONArray("orders");
        assertEquals(0, orders.length());

        // LIST
        resp = cli.getForObject(url, String.class);
        var respJa = new JSONArray(resp);
        assertEquals(1, respJa.length());

        var resp0 = respJa.getJSONObject(0);
        assertEquals("a", resp0.get("name"));

        // DELETE
        cli.delete(url + "/" + id, req, String.class);

        // LIST
        resp = cli.getForObject(url, String.class);
        respJa = new JSONArray(resp);
        assertEquals(0, respJa.length());

        // GET by ID should respond 404 NOT FOUND
        int httpCode = cli.getForEntity(url + "/" + id, String.class).getStatusCodeValue();
        assertThat(httpCode).isEqualTo(404);

        // empty name
        req = new HttpEntity<>("{}");

        resp = cli.postForObject(url, req, String.class);
        respJ = new JSONObject(resp);
        assertEquals("default", respJ.get("name"));

        // orders
        req = new HttpEntity<>("{\"name\":\"a\",\"orders\":[{\"description\":\"d\"},{\"description\":\"d2\"}]}");
        resp = cli.postForObject(url, req, String.class);

        respJ = new JSONObject(resp);
        assertEquals(-10, respJ.getInt("number"));
        assertTrue(respJ.isNull("numberOpt"));
        assertTrue(respJ.isNull("i"));

        orders = respJ.getJSONArray("orders");
        var descriptions = new HashSet<>();
        for (var i = 0; i < orders.length(); i++)
            descriptions.add(orders.getJSONObject(i).get("description"));

        assertEquals(Set.of("d", "d2"), descriptions);
    }
}
