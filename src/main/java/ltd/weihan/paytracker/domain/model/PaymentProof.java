package ltd.weihan.paytracker.domain.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 支付凭证值对象
 * 代表支付记录的证明文件，可以是图片、PDF等
 */
@Entity
@Table(name = "payment_proofs", indexes = {
    @Index(name = "idx_payment_proofs_payment_record_id", columnList = "payment_record_id"),
    @Index(name = "idx_payment_proofs_ocr_status", columnList = "ocr_status")
})
@Getter
@NoArgsConstructor
public class PaymentProof extends PanacheEntity {
    
    @Column(name = "payment_record_id", insertable = false, updatable = false)
    private Long paymentId;
    
    @Column(name = "file_path", nullable = false, columnDefinition = "TEXT")
    private String filePath;
    
    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;
    
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;
    
    @Column(name = "ocr_result", columnDefinition = "jsonb")
    private String ocrResult;
    
    @Column(name = "ocr_confidence")
    private Double ocrConfidence;
    
    @Column(name = "ocr_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OcrStatus ocrStatus = OcrStatus.PENDING;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    /**
     * 创建新的支付凭证
     */
    public static PaymentProof create(String filePath, String fileType, String originalFilename) {
        PaymentProof proof = new PaymentProof();
        proof.filePath = filePath;
        proof.fileType = fileType;
        proof.originalFilename = originalFilename;
        return proof;
    }
    
    /**
     * 更新OCR识别结果
     */
    public void updateOcrResult(String ocrResult, Double confidence) {
        this.ocrResult = ocrResult;
        this.ocrConfidence = confidence;
        this.ocrStatus = OcrStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 标记OCR识别失败
     */
    public void markOcrFailed(String errorMessage) {
        this.ocrResult = errorMessage;
        this.ocrStatus = OcrStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 