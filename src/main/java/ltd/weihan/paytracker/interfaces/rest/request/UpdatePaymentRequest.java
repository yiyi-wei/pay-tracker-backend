package ltd.weihan.paytracker.interfaces.rest.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdatePaymentRequest {
    
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    private String paymentMethod;
    
    private String category;
    
    private String description;
} 