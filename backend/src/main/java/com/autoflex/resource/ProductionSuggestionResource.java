package com.autoflex.resource;

import com.autoflex.dto.ProductionSuggestionResponseDTO;
import com.autoflex.service.ProductionSuggestionService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/api/production")
@Produces(MediaType.APPLICATION_JSON)
public class ProductionSuggestionResource {

    private final ProductionSuggestionService productionSuggestionService;

    public ProductionSuggestionResource(ProductionSuggestionService productionSuggestionService) {
        this.productionSuggestionService = productionSuggestionService;
    }

    /**
     * Calcula e sugere a produção ideal com base no estoque disponível.
     */
    @GET
    @Path("/suggestion")
    @Operation(summary = "Sugerir produção", description = "Calcula o que pode ser produzido priorizando valor")
    public ProductionSuggestionResponseDTO getSuggestion() {
        return productionSuggestionService.calculateSuggestion();
    }
}