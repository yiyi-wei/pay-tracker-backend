package ltd.weihan.paytracker.service;

import ltd.weihan.paytracker.domain.model.Payment;
import ltd.weihan.paytracker.domain.model.PaymentProof;

import java.util.concurrent.CompletionStage;

/**
 * OCR服务接口
 * 处理OCR相关的业务逻辑
 */
public interface OcrService {
    /**
     * 异步处理支付凭证
     * @param proof 支付凭证
     * @return 处理后的支付记录
     */
    CompletionStage<Payment> processProofAsync(PaymentProof proof);
    
    /**
     * 同步处理支付凭证
     * @param proof 支付凭证
     * @return 处理后的支付记录
     */
    Payment processProof(PaymentProof proof);
    
    /**
     * 重试失败的OCR处理
     * @param proofId 支付凭证ID
     * @return 处理后的支付记录
     */
    CompletionStage<Payment> retryProcessing(Long proofId);
    
    /**
     * 获取OCR处理状态
     * @param proofId 支付凭证ID
     * @return 处理状态和结果
     */
    PaymentProof getProcessingStatus(Long proofId);
} 