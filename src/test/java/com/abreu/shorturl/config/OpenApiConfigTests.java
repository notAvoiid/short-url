package com.abreu.shorturl.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OpenApiConfigTests {

    @Autowired
    private OpenAPI openAPI;

    @LocalServerPort
    private int port;

    @Test
    void shouldLoadOpenAPIConfiguration() {
        assertNotNull(openAPI, "OpenAPI bean deve ser carregado");
    }

    @Test
    void shouldContainCorrectAPIMetadata() {
        Info info = openAPI.getInfo();

        assertEquals("Short URL API", info.getTitle());
        assertEquals("1.0", info.getVersion());
        assertEquals("API for creating and managing short URLs", info.getDescription());

        Contact contact = info.getContact();
        assertNotNull(contact, "Contact must be set");
        assertEquals("Igor Abreu", contact.getName());
        assertEquals("igorabreu.dev@gmail.com", contact.getEmail());
    }

    @Test
    void shouldMergeAnnotationAndBeanConfigurations() {
        Info info = openAPI.getInfo();

        assertEquals("Short URL API", info.getTitle());
        assertEquals("1.0", info.getVersion());

        assertNotNull(info.getContact());
        assertEquals("API for creating and managing short URLs", info.getDescription());
    }

    @Test
    void shouldDisplaySwaggerUiPage() {

        var content = given()
                .basePath("/swagger-ui/index.html")
                .port(port)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertTrue(content.contains("Swagger UI"));
    }
}