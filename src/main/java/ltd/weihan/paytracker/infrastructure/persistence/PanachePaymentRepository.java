package ltd.weihan.paytracker.infrastructure.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import ltd.weihan.paytracker.domain.model.Payment;
import ltd.weihan.paytracker.domain.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 基于Panache的支付记录仓储实现
 */
@ApplicationScoped
public class PanachePaymentRepository implements PaymentRepository {
    
    @Override
    public Payment save(Payment payment) {
        payment.persist();
        return payment;
    }
    
    @Override
    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(Payment.findById(id));
    }
    
    @Override
    public List<Payment> findAll() {
        return Payment.list("deleted = ?1", false);
    }
    
    @Override
    public List<Payment> findByUserId(Long userId) {
        return Payment.list("userId = ?1 and deleted = ?2", userId, false);
    }
    
    @Override
    public List<Payment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return Payment.list("paymentDate >= ?1 and paymentDate <= ?2 and deleted = ?3",
                startDate, endDate, false);
    }
    
    @Override
    public List<Payment> findByCategory(String category) {
        return Payment.list("category = ?1 and deleted = ?2", category, false);
    }
    
    @Override
    public void delete(Payment payment) {
        payment.markAsDeleted();
        payment.persist();
    }
} 