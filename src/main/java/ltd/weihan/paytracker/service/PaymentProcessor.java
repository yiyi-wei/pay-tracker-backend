package ltd.weihan.paytracker.service;

import ltd.weihan.paytracker.domain.model.Payment;
import java.io.InputStream;
import java.util.Map;

/**
 * 支付处理器接口
 * 定义了不同类型支付记录的处理方法
 */
public interface PaymentProcessor {
    /**
     * 获取处理器类型
     * @return 处理器类型标识
     */
    String getType();
    
    /**
     * 处理文本形式的支付记录
     * @param data 支付记录数据
     * @return 处理后的支付记录
     * @throws ltd.weihan.paytracker.domain.exception.PaymentValidationException 当数据验证失败时
     */
    Payment processTextPayment(Map<String, String> data);
    
    /**
     * 处理文件形式的支付记录
     * @param inputStream 文件输入流
     * @param filename 原始文件名
     * @param contentType 文件类型
     * @return 处理后的支付记录
     * @throws ltd.weihan.paytracker.domain.exception.FileStorageException 当文件处理失败时
     * @throws ltd.weihan.paytracker.domain.exception.PaymentValidationException 当数据验证失败时
     */
    Payment processFilePayment(InputStream inputStream, String filename, String contentType);
    
    /**
     * 检查处理器是否支持指定的文件类型
     * @param contentType 文件类型
     * @return 是否支持
     */
    boolean supportsContentType(String contentType);
} 