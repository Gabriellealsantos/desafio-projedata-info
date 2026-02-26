package com.autoflex.resource;

import com.autoflex.dto.ProductRawMaterialCreateDTO;
import com.autoflex.dto.ProductRawMaterialResponseDTO;
import com.autoflex.service.ProductRawMaterialService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/products/{productId}/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductRawMaterialResource {

    private final ProductRawMaterialService productRawMaterialService;

    public ProductRawMaterialResource(ProductRawMaterialService productRawMaterialService) {
        this.productRawMaterialService = productRawMaterialService;
    }

    @GET
    public List<ProductRawMaterialResponseDTO> findByProductId(@PathParam("productId") Long productId) {
        return productRawMaterialService.findByProductId(productId);
    }

    @POST
    public Response addRawMaterial(
            @PathParam("productId") Long productId,
            @Valid ProductRawMaterialCreateDTO dto) {

        ProductRawMaterialResponseDTO created = productRawMaterialService.addRawMaterialToProduct(productId, dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @POST
    @Path("/batch")
    public Response addMultipleRawMaterials(
            @PathParam("productId") Long productId,
            @Valid List<ProductRawMaterialCreateDTO> dtos) {

        List<ProductRawMaterialResponseDTO> created = productRawMaterialService.addMultipleRawMaterials(productId, dtos);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{rawMaterialId}")
    public ProductRawMaterialResponseDTO updateQuantity(
            @PathParam("productId") Long productId,
            @PathParam("rawMaterialId") Long rawMaterialId,
            @Valid ProductRawMaterialCreateDTO dto) {

        return productRawMaterialService.updateQuantity(productId, rawMaterialId, dto);
    }

    @DELETE
    @Path("/{rawMaterialId}")
    public Response removeRawMaterial(
            @PathParam("productId") Long productId,
            @PathParam("rawMaterialId") Long rawMaterialId) {

        productRawMaterialService.removeRawMaterialFromProduct(productId, rawMaterialId);
        return Response.noContent().build();
    }
}