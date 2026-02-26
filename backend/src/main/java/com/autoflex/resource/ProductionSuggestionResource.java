package com.autoflex.resource;

import com.autoflex.dto.ProductionSuggestionResponseDTO;
import com.autoflex.service.ProductionSuggestionService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/production")
@Produces(MediaType.APPLICATION_JSON)
public class ProductionSuggestionResource {

    private final ProductionSuggestionService productionSuggestionService;

    public ProductionSuggestionResource(ProductionSuggestionService productionSuggestionService) {
        this.productionSuggestionService = productionSuggestionService;
    }

    @GET
    @Path("/suggestion")
    public ProductionSuggestionResponseDTO getSuggestion() { // Tipo de retorno corrigido
        return productionSuggestionService.calculateSuggestion();
    }
}