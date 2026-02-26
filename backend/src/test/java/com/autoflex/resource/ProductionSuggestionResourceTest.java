package com.autoflex.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@DisplayName("ProductionSuggestion Resource — Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductionSuggestionResourceTest {

        /**
         * Popula o banco com produtos, insumos e estoque para testar o cálculo de sugestão.
         */
        @Test
        @Order(0)
        @DisplayName("SETUP — seed data for production suggestion tests")
        void seedTestData() {
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "Suggestion Steel",
                                                    "stockQuantity": 100.0000
                                                }
                                                """)
                                .when()
                                .post("/api/raw-materials")
                                .then()
                                .statusCode(201);

                Integer rmId = given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "Suggestion Copper",
                                                    "stockQuantity": 50.0000
                                                }
                                                """)
                                .when()
                                .post("/api/raw-materials")
                                .then()
                                .statusCode(201)
                                .extract().path("id");

                Integer productId = given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "Suggestion Motor",
                                                    "value": 2500.00
                                                }
                                                """)
                                .when()
                                .post("/api/products")
                                .then()
                                .statusCode(201)
                                .extract().path("id");

                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "rawMaterialId": %d,
                                                    "quantity": 10.0000
                                                }
                                                """.formatted(rmId))
                                .when()
                                .post("/api/products/" + productId + "/raw-materials")
                                .then()
                                .statusCode(201);

                Assertions.assertNotNull(productId);
                Assertions.assertNotNull(rmId);
        }

        /**
         * Verifica se o endpoint de sugestão retorna dados processados com sucesso.
         */
        @Test
        @Order(1)
        @DisplayName("GET /api/production/suggestion — should return suggestion with items")
        void shouldReturnSuggestion() {
                given()
                                .when()
                                .get("/api/production/suggestion")
                                .then()
                                .statusCode(200)
                                .body("items", notNullValue())
                                .body("totalValue", notNullValue());
        }

        /**
         * Valida se a estrutura do JSON de resposta contém todos os campos necessários para o frontend.
         */
        @Test
        @Order(2)
        @DisplayName("GET /api/production/suggestion — response should have correct structure")
        void shouldHaveCorrectStructure() {
                given()
                                .when()
                                .get("/api/production/suggestion")
                                .then()
                                .statusCode(200)
                                .body("items", everyItem(
                                                allOf(
                                                                hasKey("productId"),
                                                                hasKey("productName"),
                                                                hasKey("productValue"),
                                                                hasKey("quantityToProduce"),
                                                                hasKey("subtotal"))))
                                .body("totalValue", greaterThanOrEqualTo(0f));
        }
}
