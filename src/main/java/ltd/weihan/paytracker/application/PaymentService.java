package ltd.weihan.paytracker.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ltd.weihan.paytracker.domain.model.Payment;
import ltd.weihan.paytracker.domain.model.PaymentProof;
import ltd.weihan.paytracker.domain.repository.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 支付记录应用服务
 * 处理支付记录的业务逻辑
 */
@ApplicationScoped
public class PaymentService {
    
    @Inject
    PaymentRepository paymentRepository;
    
    /**
     * 创建支付记录
     */
    @Transactional
    public Payment createPayment(BigDecimal amount, String paymentMethod, String category,
                               String description, LocalDateTime paymentDate, Long userId) {
        Payment payment = Payment.create(amount, paymentMethod, category, description, paymentDate, userId);
        return paymentRepository.save(payment);
    }
    
    /**
     * 添加支付凭证
     */
    @Transactional
    public Payment addPaymentProof(Long paymentId, String filePath, String fileType, String originalFilename) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        
        PaymentProof proof = PaymentProof.create(filePath, fileType, originalFilename);
        payment.addProof(proof);
        
        return paymentRepository.save(payment);
    }
    
    /**
     * 更新支付记录
     */
    @Transactional
    public Payment updatePayment(Long id, BigDecimal amount, String paymentMethod,
                               String category, String description) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));
        
        payment.update(amount, paymentMethod, category, description);
        return paymentRepository.save(payment);
    }
    
    /**
     * 删除支付记录
     */
    @Transactional
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));
        paymentRepository.delete(payment);
    }
    
    /**
     * 查找所有支付记录
     */
    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }
    
    /**
     * 根据ID查找支付记录
     */
    public Optional<Payment> findPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
    
    /**
     * 根据用户ID查找支付记录
     */
    public List<Payment> findPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId);
    }
    
    /**
     * 根据日期范围查找支付记录
     */
    public List<Payment> findPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * 根据类别查找支付记录
     */
    public List<Payment> findPaymentsByCategory(String category) {
        return paymentRepository.findByCategory(category);
    }
} 