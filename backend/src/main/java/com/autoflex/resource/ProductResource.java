package com.autoflex.resource;

import com.autoflex.dto.PageResponseDTO;
import com.autoflex.dto.ProductCreateDTO;
import com.autoflex.dto.ProductResponseDTO;
import com.autoflex.service.ProductService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.net.URI;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Lista todos os produtos cadastrados com paginação.
     */
    @GET
    @Operation(summary = "Listar produtos", description = "Retorna uma lista paginada de produtos")
    public PageResponseDTO<ProductResponseDTO> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return productService.findAll(page, size);
    }

    /**
     * Detalha um produto específico.
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Obter produto", description = "Retorna os detalhes de um produto")
    public ProductResponseDTO findById(@PathParam("id") Long id) {
        return productService.findById(id);
    }

    /**
     * Cria um novo produto no catálogo.
     */
    @POST
    @Operation(summary = "Criar produto", description = "Cadastra um novo produto")
    @APIResponse(responseCode = "201", description = "Produto criado")
    public Response create(@Valid ProductCreateDTO dto) {
        ProductResponseDTO created = productService.create(dto);
        return Response.created(URI.create("/api/products/" + created.id())).entity(created).build();
    }

    /**
     * Atualiza os dados de um produto existente.
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualizar produto", description = "Atualiza informações do produto")
    public ProductResponseDTO update(@PathParam("id") Long id, @Valid ProductCreateDTO dto) {
        return productService.update(id, dto);
    }

    /**
     * Remove um produto e seus vínculos de composição.
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Remover produto", description = "Exclui logicamente o produto e seus vínculos")
    @APIResponse(responseCode = "204", description = "Produto removido")
    public Response delete(@PathParam("id") Long id) {
        productService.delete(id);
        return Response.noContent().build();
    }
}