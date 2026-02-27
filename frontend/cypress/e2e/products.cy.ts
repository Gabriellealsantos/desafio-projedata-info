describe("Products CRUD", () => {
  const uniqueName = `Widget-E2E-${Date.now()}`;

  beforeEach(() => {
    cy.visit("/");
    cy.contains("Produtos").click();
  });

  it("should create a new product", () => {
    cy.contains("Novo Produto").click();
    cy.get("#productName").clear().type(uniqueName);
    cy.get("#productValue").clear().type("1500.50");
    cy.contains("Salvar Produto").click();

    cy.contains("Produto criado!");
    cy.contains(uniqueName).should("be.visible");
  });

  it("should open composition modal and see empty state", () => {
    cy.contains(uniqueName)
      .parents("[class*=rounded]")
      .first()
      .within(() => {
        cy.contains("Ficha Técnica").click();
      });

    cy.contains("Nenhum insumo vinculado.").should("be.visible");
  });

  it("should edit the product", () => {
    cy.contains(uniqueName)
      .parents("[class*=rounded]")
      .first()
      .within(() => {
        cy.get('[title="Editar Produto"]').click();
      });

    cy.get("#productValue").clear().type("2000");
    cy.contains("Salvar Produto").click();

    cy.contains("Produto atualizado!");
  });

  it("should delete the product", () => {
    cy.contains(uniqueName)
      .parents("[class*=rounded]")
      .first()
      .within(() => {
        cy.get('[title="Deletar"]').click();
      });

    cy.on("window:confirm", () => true);
    cy.contains("Produto removido!");
  });
});
