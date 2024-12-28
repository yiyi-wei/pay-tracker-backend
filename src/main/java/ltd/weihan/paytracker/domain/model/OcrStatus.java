package ltd.weihan.paytracker.domain.model;

/**
 * OCR处理状态枚举
 */
public enum OcrStatus {
    /**
     * 等待处理
     */
    PENDING,
    
    /**
     * 处理中
     */
    PROCESSING,
    
    /**
     * 处理完成
     */
    COMPLETED,
    
    /**
     * 处理失败
     */
    FAILED
} 