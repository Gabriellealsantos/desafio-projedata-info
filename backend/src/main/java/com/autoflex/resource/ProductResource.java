package com.autoflex.resource;

import com.autoflex.dto.PageResponseDTO;
import com.autoflex.dto.ProductCreateDTO;
import com.autoflex.dto.ProductResponseDTO;
import com.autoflex.service.ProductService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @GET
    public PageResponseDTO<ProductResponseDTO> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return productService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    public ProductResponseDTO findById(@PathParam("id") Long id) {
        return productService.findById(id);
    }

    @POST
    public Response create(@Valid ProductCreateDTO dto) {
        ProductResponseDTO created = productService.create(dto);
        return Response.created(URI.create("/api/products/" + created.id())).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public ProductResponseDTO update(@PathParam("id") Long id, @Valid ProductCreateDTO dto) {
        return productService.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        productService.delete(id);
        return Response.noContent().build();
    }
}