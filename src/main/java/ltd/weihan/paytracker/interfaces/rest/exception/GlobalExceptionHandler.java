package ltd.weihan.paytracker.interfaces.rest.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ltd.weihan.paytracker.domain.exception.FileStorageException;
import ltd.weihan.paytracker.domain.exception.PaymentNotFoundException;
import ltd.weihan.paytracker.domain.exception.PaymentValidationException;
import ltd.weihan.paytracker.interfaces.rest.response.ApiResponse;
import org.jboss.logging.Logger;

import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {
    
    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);
    
    @Override
    public Response toResponse(Exception exception) {
        LOG.error("Error processing request", exception);
        
        if (exception instanceof PaymentNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse<>(false, exception.getMessage(), null))
                    .build();
        }
        
        if (exception instanceof PaymentValidationException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse<>(false, exception.getMessage(), null))
                    .build();
        }
        
        if (exception instanceof FileStorageException) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse<>(false, "File storage error: " + exception.getMessage(), null))
                    .build();
        }
        
        if (exception instanceof ConstraintViolationException) {
            String message = ((ConstraintViolationException) exception).getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse<>(false, "Validation error: " + message, null))
                    .build();
        }
        
        if (exception instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse<>(false, exception.getMessage(), null))
                    .build();
        }
        
        // 记录未处理的异常详细信息
        LOG.error("Unhandled exception", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse<>(false, "Internal server error", null))
                .build();
    }
} 