package com.autoflex.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@DisplayName("ProductRawMaterial Resource — Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductRawMaterialResourceTest {

        private static Integer productId;
        private static Integer rawMaterialId;

        /**
         * Prepara o cenário criando um produto e um insumo para os testes de vínculo subsequentes.
         */
        @Test
        @Order(0)
        @DisplayName("SETUP — seed product and raw material for association tests")
        void seedData() {
                productId = given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "PRM Test Product",
                                                    "value": 1000.00
                                                }
                                                """)
                                .when()
                                .post("/api/products")
                                .then()
                                .statusCode(201)
                                .extract().path("id");

                rawMaterialId = given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "name": "PRM Test Steel",
                                                    "stockQuantity": 500.0000
                                                }
                                                """)
                                .when()
                                .post("/api/raw-materials")
                                .then()
                                .statusCode(201)
                                .extract().path("id");

                Assertions.assertNotNull(productId);
                Assertions.assertNotNull(rawMaterialId);
        }

        /**
         * Valida o registro de uma matéria-prima na composição de um produto.
         */
        @Test
        @Order(1)
        @DisplayName("POST /api/products/{id}/raw-materials — should add raw material to product")
        void shouldAddRawMaterialToProduct() {
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "rawMaterialId": %d,
                                                    "quantity": 10.0000
                                                }
                                                """.formatted(rawMaterialId))
                                .when()
                                .post("/api/products/" + productId + "/raw-materials")
                                .then()
                                .statusCode(201)
                                .body("rawMaterialId", equalTo(rawMaterialId))
                                .body("rawMaterialName", equalTo("PRM Test Steel"))
                                .body("quantity", equalTo(10.0000f));
        }

        /**
         * Verifica se a API retorna corretamente todos os insumos vinculados a um produto.
         */
        @Test
        @Order(2)
        @DisplayName("GET /api/products/{id}/raw-materials — should list associations")
        void shouldListAssociations() {
                given()
                                .when()
                                .get("/api/products/" + productId + "/raw-materials")
                                .then()
                                .statusCode(200)
                                .body("$", not(empty()))
                                .body("[0].rawMaterialName", equalTo("PRM Test Steel"));
        }

        /**
         * Testa a alteração da quantidade de um insumo específico dentro de uma composição.
         */
        @Test
        @Order(3)
        @DisplayName("PUT /api/products/{id}/raw-materials/{rmId} — should update quantity")
        void shouldUpdateQuantity() {
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "rawMaterialId": %d,
                                                    "quantity": 25.0000
                                                }
                                                """.formatted(rawMaterialId))
                                .when()
                                .put("/api/products/" + productId + "/raw-materials/" + rawMaterialId)
                                .then()
                                .statusCode(200)
                                .body("quantity", equalTo(25.0000f));
        }

        /**
         * Valida a remoção (inativação) de um insumo da lista de composição do produto.
         */
        @Test
        @Order(4)
        @DisplayName("DELETE /api/products/{id}/raw-materials/{rmId} — should remove association")
        void shouldRemoveAssociation() {
                given()
                                .when()
                                .delete("/api/products/" + productId + "/raw-materials/" + rawMaterialId)
                                .then()
                                .statusCode(204);

                given()
                                .when()
                                .get("/api/products/" + productId + "/raw-materials")
                                .then()
                                .statusCode(200)
                                .body("$", empty());
        }

        /**
         * Garante que a API retorne erro ao tentar manipular insumos de um produto inexistente.
         */
        @Test
        @Order(5)
        @DisplayName("POST — should return 404 for non-existent product")
        void shouldReturn404ForNonExistentProduct() {
                given()
                                .contentType(ContentType.JSON)
                                .body("""
                                                {
                                                    "rawMaterialId": 1,
                                                    "quantity": 10.0000
                                                }
                                                """)
                                .when()
                                .post("/api/products/99999/raw-materials")
                                .then()
                                .statusCode(404);
        }
}
