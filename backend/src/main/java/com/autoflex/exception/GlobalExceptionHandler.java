package com.autoflex.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.jboss.resteasy.reactive.RestResponse;

public class GlobalExceptionHandler {

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleWebApplicationException(jakarta.ws.rs.WebApplicationException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), ex.getResponse().getStatus());
        return RestResponse.status(ex.getResponse().getStatus(), String.valueOf(error));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), Response.Status.NOT_FOUND.getStatusCode());
        return RestResponse.status(Response.Status.NOT_FOUND, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), Response.Status.BAD_REQUEST.getStatusCode());
        return RestResponse.status(Response.Status.BAD_REQUEST, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        ErrorResponse error = new ErrorResponse(message, Response.Status.BAD_REQUEST.getStatusCode());
        return RestResponse.status(Response.Status.BAD_REQUEST, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse("Internal server error",
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, error);
    }
}
