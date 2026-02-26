package com.autoflex.exception;

import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.jboss.resteasy.reactive.RestResponse;

public class GlobalExceptionHandler {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleWebApplicationException(jakarta.ws.rs.WebApplicationException ex) {
        int statusCode = ex.getResponse().getStatus();
        Response.Status status = Response.Status.fromStatusCode(statusCode);
        ErrorResponse error = new ErrorResponse(ex.getMessage(), statusCode);
        return RestResponse.status(status, error);
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
    public RestResponse<ErrorResponse> handlePersistenceException(PersistenceException ex) {
        LOG.error("Database error", ex);

        String rootMessage = getRootCauseMessage(ex);

        if (rootMessage.contains("ORA-02292") || rootMessage.contains("integrity constraint")) {
            ErrorResponse error = new ErrorResponse(
                    "Cannot delete this record because it is referenced by other records.",
                    Response.Status.CONFLICT.getStatusCode());
            return RestResponse.status(Response.Status.CONFLICT, error);
        }

        if (rootMessage.contains("ORA-00001") || rootMessage.contains("unique constraint")) {
            ErrorResponse error = new ErrorResponse(
                    "A record with the same unique value already exists.",
                    Response.Status.CONFLICT.getStatusCode());
            return RestResponse.status(Response.Status.CONFLICT, error);
        }

        ErrorResponse error = new ErrorResponse(
                "A database error occurred. Please try again.",
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleGenericException(Exception ex) {
        LOG.error("Unexpected error", ex);
        ErrorResponse error = new ErrorResponse("Internal server error",
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, error);
    }

    private String getRootCauseMessage(Throwable ex) {
        Throwable cause = ex;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage() != null ? cause.getMessage().toLowerCase() : "";
    }
}
