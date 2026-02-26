package com.autoflex.resource;

import com.autoflex.dto.PageResponseDTO;
import com.autoflex.dto.RawMaterialCreateDTO;
import com.autoflex.dto.RawMaterialResponseDTO;
import com.autoflex.service.RawMaterialService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/api/raw-materials")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RawMaterialResource {

    private final RawMaterialService rawMaterialService;

    public RawMaterialResource(RawMaterialService rawMaterialService) {
        this.rawMaterialService = rawMaterialService;
    }

    @GET
    public PageResponseDTO<RawMaterialResponseDTO> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return rawMaterialService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    public RawMaterialResponseDTO findById(@PathParam("id") Long id) {
        return rawMaterialService.findById(id);
    }

    @POST
    public Response create(@Valid RawMaterialCreateDTO dto) {
        RawMaterialResponseDTO created = rawMaterialService.create(dto);
        return Response.created(URI.create("/api/raw-materials/" + created.id())).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public RawMaterialResponseDTO update(@PathParam("id") Long id, @Valid RawMaterialCreateDTO dto) {
        return rawMaterialService.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        rawMaterialService.delete(id);
        return Response.noContent().build();
    }
}