-- 插入测试支付记录
INSERT INTO payment_records (id, amount, currency, payment_method, category, description, payment_date, user_id, status, source_type, version, deleted, created_at, updated_at)
VALUES 
(1, 100.50, 'CNY', 'ALIPAY', 'FOOD', 'Test payment 1', CURRENT_TIMESTAMP, 1, 'COMPLETED', 'MANUAL', 0, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 200.00, 'CNY', 'WECHAT', 'SHOPPING', 'Test payment 2', CURRENT_TIMESTAMP, 1, 'COMPLETED', 'MANUAL', 0, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 300.00, 'CNY', 'ALIPAY', 'FOOD', 'Test payment 3', CURRENT_TIMESTAMP, 2, 'COMPLETED', 'MANUAL', 0, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入测试支付凭证
INSERT INTO payment_proofs (id, payment_record_id, file_path, file_type, original_filename, created_at, updated_at)
VALUES 
(1, 1, 'uploads/2024/1/test1.jpg', 'image/jpeg', 'receipt1.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 'uploads/2024/1/test2.jpg', 'image/jpeg', 'receipt2.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); 