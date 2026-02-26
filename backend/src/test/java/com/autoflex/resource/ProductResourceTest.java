package com.autoflex.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@DisplayName("Product Resource — Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductResourceTest {

        /**
         * Valida a criação bem-sucedida de um novo produto via POST.
         */
        @Test
        @Order(1)
        @DisplayName("POST /api/products — should create product")
        void shouldCreateProduct() {
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "Integration Motor",
                                                    "value": 1500.00
                                                }
                                                """)
                                .when()
                                .post("/api/products")
                                .then()
                                .statusCode(201)
                                .body("name", equalTo("Integration Motor"))
                                .body("value", equalTo(1500.00f))
                                .body("id", notNullValue());
        }

        /**
         * Verifica a validação de integridade para impedir nomes de produtos vazios.
         */
        @Test
        @Order(2)
        @DisplayName("POST /api/products — should reject blank name (validation)")
        void shouldRejectBlankName() {
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "",
                                                    "value": 100.00
                                                }
                                                """)
                                .when()
                                .post("/api/products")
                                .then()
                                .statusCode(400);
        }

        /**
         * Garante que o sistema rejeite valores de produto negativos.
         */
        @Test
        @Order(3)
        @DisplayName("POST /api/products — should reject negative value (validation)")
        void shouldRejectNegativeValue() {
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "Bad Product",
                                                    "value": -10.00
                                                }
                                                """)
                                .when()
                                .post("/api/products")
                                .then()
                                .statusCode(400);
        }

        /**
         * Valida a restrição de nomes únicos, garantindo que não haja duplicidade de produtos.
         */
        @Test
        @Order(4)
        @DisplayName("POST /api/products — should reject duplicate name")
        void shouldRejectDuplicateName() {
                // First create
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "Unique Motor",
                                                    "value": 500.00
                                                }
                                                """)
                                .when()
                                .post("/api/products")
                                .then()
                                .statusCode(201);

                // Duplicate
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "Unique Motor",
                                                    "value": 800.00
                                                }
                                                """)
                                .when()
                                .post("/api/products")
                                .then()
                                .statusCode(400)
                                .body("message", containsString("already exists"));
        }

        /**
         * Testa se o endpoint de listagem retorna os dados corretamente paginados.
         */
        @Test
        @Order(5)
        @DisplayName("GET /api/products — should return paginated list")
        void shouldReturnPaginatedList() {
                given()
                                .queryParam("page", 0)
                                .queryParam("size", 10)
                                .when()
                                .get("/api/products")
                                .then()
                                .statusCode(200)
                                .body("items", not(empty()))
                                .body("page", equalTo(0))
                                .body("size", equalTo(10))
                                .body("totalElements", greaterThanOrEqualTo(1));
        }

        /**
         * Verifica se a API retorna 404 corretamente ao tentar acessar um produto que não existe.
         */
        @Test
        @Order(6)
        @DisplayName("GET /api/products/{id} — should return 404 for non-existent")
        void shouldReturn404ForNonExistent() {
                given()
                                .when()
                                .get("/api/products/99999")
                                .then()
                                .statusCode(404)
                                .body("message", containsString("not found"));
        }

        /**
         * Valida a atualização dos campos de um produto existente via PUT.
         */
        @Test
        @Order(7)
        @DisplayName("PUT /api/products/{id} — should update product")
        void shouldUpdateProduct() {
                // Create first
                Integer id = given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "To Update",
                                                    "value": 100.00
                                                }
                                                """)
                                .when()
                                .post("/api/products")
                                .then()
                                .statusCode(201)
                                .extract().path("id");

                // Update
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "Updated Name",
                                                    "value": 999.00
                                                }
                                                """)
                                .when()
                                .put("/api/products/" + id)
                                .then()
                                .statusCode(200)
                                .body("name", equalTo("Updated Name"))
                                .body("value", equalTo(999.00f));
        }

        /**
         * Testa o fluxo de exclusão lógica (soft delete) do produto.
         */
        @Test
        @Order(8)
        @DisplayName("DELETE /api/products/{id} — should soft delete product")
        void shouldSoftDeleteProduct() {
                // Create first
                Integer id = given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "To Delete",
                                                    "value": 100.00
                                                }
                                                """)
                                .when()
                                .post("/api/products")
                                .then()
                                .statusCode(201)
                                .extract().path("id");

                // Delete
                given()
                                .when()
                                .delete("/api/products/" + id)
                                .then()
                                .statusCode(204);

                // Should not be found anymore (soft deleted)
                given()
                                .when()
                                .get("/api/products/" + id)
                                .then()
                                .statusCode(404);
        }
}
