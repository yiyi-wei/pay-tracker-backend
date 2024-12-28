package ltd.weihan.paytracker.domain.exception;

public class PaymentNotFoundException extends RuntimeException {
    
    public PaymentNotFoundException(Long id) {
        super("Payment not found with id: " + id);
    }
    
    public PaymentNotFoundException(String message) {
        super(message);
    }
} 