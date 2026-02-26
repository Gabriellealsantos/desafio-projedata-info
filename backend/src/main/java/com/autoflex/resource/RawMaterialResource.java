package com.autoflex.resource;

import com.autoflex.dto.PageResponseDTO;
import com.autoflex.dto.RawMaterialCreateDTO;
import com.autoflex.dto.RawMaterialResponseDTO;
import com.autoflex.service.RawMaterialService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.net.URI;

@Path("/api/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RawMaterialResource {

    private final RawMaterialService rawMaterialService;

    public RawMaterialResource(RawMaterialService rawMaterialService) {
        this.rawMaterialService = rawMaterialService;
    }

    /**
     * Recupera uma lista paginada de todas as matérias-primas.
     */
    @GET
    @Operation(summary = "Listar matérias-primas", description = "Retorna uma lista paginada de insumos cadastrados")
    @APIResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    public PageResponseDTO<RawMaterialResponseDTO> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return rawMaterialService.findAll(page, size);
    }

    /**
     * Busca os detalhes de uma matéria-prima específica pelo ID.
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Obter matéria-prima", description = "Retorna os detalhes de um insumo específico")
    @APIResponse(responseCode = "200", description = "Insumo encontrado")
    @APIResponse(responseCode = "404", description = "Insumo não encontrado")
    public RawMaterialResponseDTO findById(@PathParam("id") Long id) {
        return rawMaterialService.findById(id);
    }

    /**
     * Cria um novo registro de matéria-prima.
     */
    @POST
    @Operation(summary = "Criar matéria-prima", description = "Cadastra um novo insumo no sistema")
    @APIResponse(responseCode = "201", description = "Insumo criado com sucesso")
    public Response create(@Valid RawMaterialCreateDTO dto) {
        RawMaterialResponseDTO created = rawMaterialService.create(dto);
        return Response.created(URI.create("/api/raw-materials/" + created.id())).entity(created).build();
    }

    /**
     * Atualiza as informações de uma matéria-prima existente.
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualizar matéria-prima", description = "Modifica os dados de um insumo existente")
    @APIResponse(responseCode = "200", description = "Insumo atualizado")
    public RawMaterialResponseDTO update(@PathParam("id") Long id, @Valid RawMaterialCreateDTO dto) {
        return rawMaterialService.update(id, dto);
    }

    /**
     * Remove (inativa) uma matéria-prima se não estiver vinculada a produtos.
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Remover matéria-prima", description = "Realiza a exclusão lógica do insumo")
    @APIResponse(responseCode = "204", description = "Insumo removido")
    @APIResponse(responseCode = "400", description = "Erro de negócio (ex: item em uso)")
    public Response delete(@PathParam("id") Long id) {
        rawMaterialService.delete(id);
        return Response.noContent().build();
    }
}