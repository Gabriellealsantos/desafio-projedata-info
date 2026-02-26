package com.autoflex.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@DisplayName("RawMaterial Resource — Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RawMaterialResourceTest {

        /**
         * Testa o fluxo completo de criação de matéria-prima via API REST.
         */
        @Test
        @Order(1)
        @DisplayName("POST /api/raw-materials — should create raw material")
        void shouldCreateRawMaterial() {
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "Integration Steel",
                                                    "stockQuantity": 500.0000
                                                }
                                                """)
                                .when()
                                .post("/api/raw-materials")
                                .then()
                                .statusCode(201)
                                .body("name", equalTo("Integration Steel"))
                                .body("stockQuantity", equalTo(500.0000f))
                                .body("id", notNullValue());
        }

        /**
         * Valida se a API rejeita nomes vazios (validação @NotBlank).
         */
        @Test
        @Order(2)
        @DisplayName("POST /api/raw-materials — should reject blank name")
        void shouldRejectBlankName() {
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "",
                                                    "stockQuantity": 100.0000
                                                }
                                                """)
                                .when()
                                .post("/api/raw-materials")
                                .then()
                                .statusCode(400);
        }

        /**
         * Garante que o sistema não permite entrada de estoque negativo (validação @PositiveOrZero).
         */
        @Test
        @Order(3)
        @DisplayName("POST /api/raw-materials — should reject negative stock")
        void shouldRejectNegativeStock() {
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "Bad Material",
                                                    "stockQuantity": -5.0000
                                                }
                                                """)
                                .when()
                                .post("/api/raw-materials")
                                .then()
                                .statusCode(400);
        }

        /**
         * Verifica se a regra de negócio de nomes únicos é aplicada corretamente na API.
         */
        @Test
        @Order(4)
        @DisplayName("POST /api/raw-materials — should reject duplicate name")
        void shouldRejectDuplicateName() {
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "Unique Steel",
                                                    "stockQuantity": 100.0000
                                                }
                                                """)
                                .when()
                                .post("/api/raw-materials")
                                .then()
                                .statusCode(201);

                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "Unique Steel",
                                                    "stockQuantity": 200.0000
                                                }
                                                """)
                                .when()
                                .post("/api/raw-materials")
                                .then()
                                .statusCode(400)
                                .body("message", containsString("already exists"));
        }

        /**
         * Testa se o endpoint de listagem respeita os parâmetros de paginação.
         */
        @Test
        @Order(5)
        @DisplayName("GET /api/raw-materials — should return paginated list")
        void shouldReturnPaginatedList() {
                given()
                                .queryParam("page", 0)
                                .queryParam("size", 10)
                                .when()
                                .get("/api/raw-materials")
                                .then()
                                .statusCode(200)
                                .body("items", not(empty()))
                                .body("page", equalTo(0))
                                .body("totalElements", greaterThanOrEqualTo(1));
        }

        /**
         * Valida se a API retorna o status 404 corretamente para recursos inexistentes.
         */
        @Test
        @Order(6)
        @DisplayName("GET /api/raw-materials/{id} — should return 404 for non-existent")
        void shouldReturn404ForNonExistent() {
                given()
                                .when()
                                .get("/api/raw-materials/99999")
                                .then()
                                .statusCode(404)
                                .body("message", containsString("not found"));
        }

        /**
         * Testa a atualização de dados de uma matéria-prima via PUT.
         */
        @Test
        @Order(7)
        @DisplayName("PUT /api/raw-materials/{id} — should update raw material")
        void shouldUpdateRawMaterial() {
                Integer id = given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "To Update Material",
                                                    "stockQuantity": 100.0000
                                                }
                                                """)
                                .when()
                                .post("/api/raw-materials")
                                .then()
                                .statusCode(201)
                                .extract().path("id");

                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "Updated Material",
                                                    "stockQuantity": 999.0000
                                                }
                                                """)
                                .when()
                                .put("/api/raw-materials/" + id)
                                .then()
                                .statusCode(200)
                                .body("name", equalTo("Updated Material"))
                                .body("stockQuantity", equalTo(999.0000f));
        }

        /**
         * Verifica o fluxo de exclusão lógica: o item deve se tornar inacessível após o DELETE.
         */
        @Test
        @Order(8)
        @DisplayName("DELETE /api/raw-materials/{id} — should soft delete raw material")
        void shouldSoftDeleteRawMaterial() {
                Integer id = given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "To Delete Material",
                                                    "stockQuantity": 100.0000
                                                }
                                                """)
                                .when()
                                .post("/api/raw-materials")
                                .then()
                                .statusCode(201)
                                .extract().path("id");

                given()
                                .when()
                                .delete("/api/raw-materials/" + id)
                                .then()
                                .statusCode(204);

                given()
                                .when()
                                .get("/api/raw-materials/" + id)
                                .then()
                                .statusCode(404);
        }
}
