describe("Dashboard - Production Suggestion", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("should load the dashboard by default", () => {
    cy.contains("Sugestão de Produção Otimizada").should("be.visible");
    cy.contains("Receita Estimada").should("be.visible");
  });

  it("should display a total value", () => {
    cy.contains("R$").should("be.visible");
  });

  it("should display the suggestion table with product data", () => {
    // Wait for loading to complete
    cy.get(".animate-spin", { timeout: 5000 }).should("not.exist");

    // The table or cards should be visible
    cy.get("table, [class*=rounded]").should("exist");
  });

  it("should navigate between tabs", () => {
    cy.contains("Produtos").click();
    cy.contains("Catálogo de produtos").should("be.visible");

    cy.contains("Matérias-Primas").click();
    cy.contains("Gerencie o estoque").should("be.visible");

    cy.contains("Sugestão Produção").click();
    cy.contains("Sugestão de Produção Otimizada").should("be.visible");
  });
});
