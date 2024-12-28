package ltd.weihan.paytracker.domain.repository;

import ltd.weihan.paytracker.domain.model.Payment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 支付记录仓储接口
 * 定义支付记录的持久化操作
 */
public interface PaymentRepository {
    /**
     * 保存支付记录
     */
    Payment save(Payment payment);
    
    /**
     * 根据ID查找支付记录
     */
    Optional<Payment> findById(Long id);
    
    /**
     * 查找所有未删除的支付记录
     */
    List<Payment> findAll();
    
    /**
     * 根据用户ID查找支付记录
     */
    List<Payment> findByUserId(Long userId);
    
    /**
     * 根据日期范围查找支付记录
     */
    List<Payment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 根据类别查找支付记录
     */
    List<Payment> findByCategory(String category);
    
    /**
     * 删除支付记录
     */
    void delete(Payment payment);
} 