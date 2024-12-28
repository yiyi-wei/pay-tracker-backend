package ltd.weihan.paytracker.domain.model;

/**
 * 支付状态枚举
 * 表示支付记录的当前状态
 */
public enum PaymentStatus {
    /**
     * 待处理
     */
    PENDING,
    
    /**
     * 已完成
     */
    COMPLETED,
    
    /**
     * 失败
     */
    FAILED,
    
    /**
     * 已取消
     */
    CANCELLED
} 