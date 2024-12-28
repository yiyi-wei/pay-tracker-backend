package ltd.weihan.paytracker.service;

import ltd.weihan.paytracker.domain.model.Payment;
import ltd.weihan.paytracker.domain.model.PaymentProof;

import java.util.concurrent.CompletionStage;

/**
 * OCR处理器接口
 * 定义了OCR处理的方法
 */
public interface OcrProcessor {
    /**
     * 获取处理器提供商名称
     * @return 提供商名称
     */
    String getProvider();
    
    /**
     * 获取处理器优先级
     * @return 优先级，数字越小优先级越高
     */
    int getPriority();
    
    /**
     * 检查是否支持指定的文件类型
     * @param contentType 文件类型
     * @return 是否支持
     */
    boolean supportsContentType(String contentType);
    
    /**
     * 异步处理支付凭证
     * @param proof 支付凭证
     * @return 处理后的支付记录
     */
    CompletionStage<Payment> processAsync(PaymentProof proof);
    
    /**
     * 同步处理支付凭证
     * @param proof 支付凭证
     * @return 处理后的支付记录
     */
    Payment process(PaymentProof proof);
} 