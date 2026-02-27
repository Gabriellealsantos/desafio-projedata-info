describe("Raw Materials CRUD", () => {
  const uniqueName = `Steel-E2E-${Date.now()}`;

  beforeEach(() => {
    cy.visit("/");
    cy.contains("Matérias-Primas").click();
  });

  it("should create a new raw material", () => {
    cy.contains("Novo Insumo").click();
    cy.get("#rmName").clear().type(uniqueName);
    cy.get("#rmStock").clear().type("500");
    cy.contains("Salvar").click();

    cy.contains("Insumo criado com sucesso!");
    cy.contains(uniqueName).should("be.visible");
  });

  it("should edit the raw material", () => {
    cy.contains(uniqueName)
      .parents("tr, [class*=rounded]")
      .first()
      .within(() => {
        cy.get("button").first().click();
      });

    cy.get("#rmStock").clear().type("999");
    cy.contains("Salvar").click();

    cy.contains("Insumo atualizado com sucesso!");
  });

  it("should delete the raw material", () => {
    cy.contains(uniqueName)
      .parents("tr, [class*=rounded]")
      .first()
      .within(() => {
        cy.get("button").last().click();
      });

    cy.on("window:confirm", () => true);
    cy.contains("Insumo removido com sucesso!");
  });
});
