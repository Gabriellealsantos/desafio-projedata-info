package com.autoflex.resource;

import com.autoflex.dto.ProductRawMaterialCreateDTO;
import com.autoflex.dto.ProductRawMaterialResponseDTO;
import com.autoflex.service.ProductRawMaterialService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.List;

@Path("/api/products/{productId}/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductRawMaterialResource {

    private final ProductRawMaterialService productRawMaterialService;

    public ProductRawMaterialResource(ProductRawMaterialService productRawMaterialService) {
        this.productRawMaterialService = productRawMaterialService;
    }

    /**
     * Lista a composição (insumos) de um produto específico.
     */
    @GET
    @Operation(summary = "Listar composição", description = "Retorna os insumos vinculados ao produto")
    public List<ProductRawMaterialResponseDTO> findByProductId(@PathParam("productId") Long productId) {
        return productRawMaterialService.findByProductId(productId);
    }

    /**
     * Adiciona um insumo à composição do produto.
     */
    @POST
    @Operation(summary = "Vincular insumo", description = "Associa uma matéria-prima ao produto")
    public Response addRawMaterial(
            @PathParam("productId") Long productId,
            @Valid ProductRawMaterialCreateDTO dto) {

        ProductRawMaterialResponseDTO created = productRawMaterialService.addRawMaterialToProduct(productId, dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Adiciona múltiplos insumos ao produto de uma só vez.
     */
    @POST
    @Path("/batch")
    @Operation(summary = "Vincular em lote", description = "Associa vários insumos ao produto")
    public Response addMultipleRawMaterials(
            @PathParam("productId") Long productId,
            @Valid List<ProductRawMaterialCreateDTO> dtos) {

        List<ProductRawMaterialResponseDTO> created = productRawMaterialService.addMultipleRawMaterials(productId, dtos);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Altera a quantidade necessária de um insumo no produto.
     */
    @PUT
    @Path("/{rawMaterialId}")
    @Operation(summary = "Atualizar quantidade", description = "Modifica a quantidade de um insumo na composição")
    public ProductRawMaterialResponseDTO updateQuantity(
            @PathParam("productId") Long productId,
            @PathParam("rawMaterialId") Long rawMaterialId,
            @Valid ProductRawMaterialCreateDTO dto) {

        return productRawMaterialService.updateQuantity(productId, rawMaterialId, dto);
    }

    /**
     * Remove um insumo da composição do produto.
     */
    @DELETE
    @Path("/{rawMaterialId}")
    @Operation(summary = "Remover vínculo", description = "Desvincula um insumo do produto")
    public Response removeRawMaterial(
            @PathParam("productId") Long productId,
            @PathParam("rawMaterialId") Long rawMaterialId) {

        productRawMaterialService.removeRawMaterialFromProduct(productId, rawMaterialId);
        return Response.noContent().build();
    }
}