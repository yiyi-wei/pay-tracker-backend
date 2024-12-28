-- 创建支付记录表
CREATE TABLE payment_records (
    id BIGSERIAL PRIMARY KEY,
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'CNY',
    payment_method VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    payment_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    source_type VARCHAR(50) NOT NULL DEFAULT 'MANUAL',  -- 记录来源：MANUAL, OCR, etc.
    version INTEGER DEFAULT 0,
    deleted BOOLEAN DEFAULT FALSE
);

-- 创建支付类别表
CREATE TABLE payment_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 创建支付方式表
CREATE TABLE payment_methods (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 创建支付凭证表
CREATE TABLE payment_proofs (
    id BIGSERIAL PRIMARY KEY,
    payment_record_id BIGINT NOT NULL,
    file_path TEXT NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    ocr_result JSONB,  -- OCR识别结果
    ocr_confidence DOUBLE PRECISION,  -- OCR识别置信度
    ocr_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- OCR处理状态：PENDING, PROCESSING, COMPLETED, FAILED
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_record_id) REFERENCES payment_records(id)
);

-- 创建OCR配置表
CREATE TABLE ocr_configs (
    id BIGSERIAL PRIMARY KEY,
    provider VARCHAR(50) NOT NULL,  -- OCR服务提供商
    config JSONB NOT NULL,  -- OCR配置信息
    enabled BOOLEAN DEFAULT TRUE,
    priority INTEGER DEFAULT 0,  -- 优先级，数字越小优先级越高
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建OCR处理记录表
CREATE TABLE ocr_processing_records (
    id BIGSERIAL PRIMARY KEY,
    payment_proof_id BIGINT NOT NULL,
    provider VARCHAR(50) NOT NULL,
    request_data JSONB,  -- 请求数据
    response_data JSONB,  -- 响应数据
    processing_time INTEGER,  -- 处理时间（毫秒）
    status VARCHAR(20) NOT NULL,  -- 处理状态
    error_message TEXT,  -- 错误信息
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_proof_id) REFERENCES payment_proofs(id)
);

-- 创建索引
CREATE INDEX idx_payment_records_payment_date ON payment_records(payment_date);
CREATE INDEX idx_payment_records_category ON payment_records(category);
CREATE INDEX idx_payment_records_payment_method ON payment_records(payment_method);
CREATE INDEX idx_payment_records_source_type ON payment_records(source_type);
CREATE INDEX idx_payment_proofs_payment_record_id ON payment_proofs(payment_record_id);
CREATE INDEX idx_payment_proofs_ocr_status ON payment_proofs(ocr_status);
CREATE INDEX idx_ocr_processing_records_payment_proof_id ON ocr_processing_records(payment_proof_id);
CREATE INDEX idx_ocr_processing_records_status ON ocr_processing_records(status);
CREATE INDEX idx_ocr_configs_provider ON ocr_configs(provider);
CREATE INDEX idx_ocr_configs_enabled_priority ON ocr_configs(enabled, priority);