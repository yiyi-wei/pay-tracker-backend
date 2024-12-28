package ltd.weihan.paytracker.domain.exception;

public class PaymentValidationException extends RuntimeException {
    
    public PaymentValidationException(String message) {
        super(message);
    }
    
    public PaymentValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 