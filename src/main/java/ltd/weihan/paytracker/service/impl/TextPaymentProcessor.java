package ltd.weihan.paytracker.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import ltd.weihan.paytracker.domain.exception.PaymentValidationException;
import ltd.weihan.paytracker.domain.model.Payment;
import ltd.weihan.paytracker.service.PaymentProcessor;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * 文本支付处理器
 * 处理文本形式的支付记录数据
 */
@ApplicationScoped
public class TextPaymentProcessor implements PaymentProcessor {
    
    private static final Logger LOG = Logger.getLogger(TextPaymentProcessor.class);
    
    @Override
    public String getType() {
        return "TEXT";
    }
    
    @Override
    public Payment processTextPayment(Map<String, String> data) {
        try {
            validateRequiredFields(data);
            
            return Payment.create(
                    new BigDecimal(data.get("amount")),
                    data.get("paymentMethod"),
                    data.get("category"),
                    data.get("description"),
                    LocalDateTime.parse(data.get("paymentDate")),
                    data.containsKey("userId") ? Long.parseLong(data.get("userId")) : null
            );
        } catch (NumberFormatException e) {
            LOG.error("Invalid number format in payment data", e);
            throw new PaymentValidationException("Invalid amount or user ID format");
        } catch (DateTimeParseException e) {
            LOG.error("Invalid date format in payment data", e);
            throw new PaymentValidationException("Invalid payment date format");
        } catch (Exception e) {
            LOG.error("Error processing text payment", e);
            throw new PaymentValidationException("Error processing payment data: " + e.getMessage());
        }
    }
    
    @Override
    public Payment processFilePayment(InputStream inputStream, String filename, String contentType) {
        throw new UnsupportedOperationException("Text processor does not support file processing");
    }
    
    @Override
    public boolean supportsContentType(String contentType) {
        return false; // 文本处理器不支持文件处理
    }
    
    /**
     * 验证必填字段
     * @param data 支付数据
     * @throws PaymentValidationException 当必填字段缺失时
     */
    private void validateRequiredFields(Map<String, String> data) {
        if (!data.containsKey("amount")) {
            throw new PaymentValidationException("Amount is required");
        }
        if (!data.containsKey("paymentMethod")) {
            throw new PaymentValidationException("Payment method is required");
        }
        if (!data.containsKey("category")) {
            throw new PaymentValidationException("Category is required");
        }
        if (!data.containsKey("paymentDate")) {
            throw new PaymentValidationException("Payment date is required");
        }
    }
} 