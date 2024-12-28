package ltd.weihan.paytracker.domain.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 支付记录聚合根
 * 代表一次支付行为的完整记录，包含支付金额、方式、类别等基本信息，以及关联的支付凭证
 */
@Entity
@Table(name = "payment_records", indexes = {
    @Index(name = "idx_payment_records_payment_date", columnList = "payment_date"),
    @Index(name = "idx_payment_records_category", columnList = "category"),
    @Index(name = "idx_payment_records_payment_method", columnList = "payment_method"),
    @Index(name = "idx_payment_records_source_type", columnList = "source_type")
})
@Getter
@NoArgsConstructor
public class Payment extends PanacheEntity {
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(length = 3)
    private String currency = "CNY";
    
    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;
    
    @Column(nullable = false, length = 50)
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.COMPLETED;
    
    @Column(name = "source_type", nullable = false, length = 50)
    private String sourceType = "MANUAL";
    
    @Version
    private Integer version = 0;
    
    @Column(nullable = false)
    private boolean deleted = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "payment_record_id")
    private Set<PaymentProof> proofs = new HashSet<>();
    
    /**
     * 创建新的支付记录
     */
    public static Payment create(BigDecimal amount, String paymentMethod, String category, 
                               String description, LocalDateTime paymentDate, Long userId) {
        Payment payment = new Payment();
        payment.amount = amount;
        payment.paymentMethod = paymentMethod;
        payment.category = category;
        payment.description = description;
        payment.paymentDate = paymentDate;
        payment.userId = userId;
        return payment;
    }
    
    /**
     * 添加支付凭证
     */
    public void addProof(PaymentProof proof) {
        proofs.add(proof);
    }
    
    /**
     * 更新支付记录信息
     */
    public void update(BigDecimal amount, String paymentMethod, String category, String description) {
        if (amount != null) this.amount = amount;
        if (paymentMethod != null) this.paymentMethod = paymentMethod;
        if (category != null) this.category = category;
        if (description != null) this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新支付状态
     */
    public void updateStatus(PaymentStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 标记为删除
     */
    public void markAsDeleted() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 