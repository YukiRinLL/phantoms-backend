-- 创建房屋销售监控配置表（规范化设计）

-- 主配置表
CREATE TABLE IF NOT EXISTS config.housing_notify_target (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 服务器关联表
CREATE TABLE IF NOT EXISTS config.housing_notify_server (
    id BIGSERIAL PRIMARY KEY,
    target_id BIGINT NOT NULL REFERENCES config.housing_notify_target(id) ON DELETE CASCADE,
    server_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 住宅区关联表
CREATE TABLE IF NOT EXISTS config.housing_notify_area (
    id BIGSERIAL PRIMARY KEY,
    target_id BIGINT NOT NULL REFERENCES config.housing_notify_target(id) ON DELETE CASCADE,
    area_id INT NOT NULL CHECK (area_id BETWEEN 0 AND 4),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- QQ群关联表
CREATE TABLE IF NOT EXISTS config.housing_notify_group (
    id BIGSERIAL PRIMARY KEY,
    target_id BIGINT NOT NULL REFERENCES config.housing_notify_target(id) ON DELETE CASCADE,
    group_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_housing_notify_target_enabled ON config.housing_notify_target(enabled);
CREATE INDEX IF NOT EXISTS idx_housing_notify_server_target ON config.housing_notify_server(target_id);
CREATE INDEX IF NOT EXISTS idx_housing_notify_area_target ON config.housing_notify_area(target_id);
CREATE INDEX IF NOT EXISTS idx_housing_notify_group_target ON config.housing_notify_group(target_id);

-- 初始化示例数据
-- 示例1: 拂晓之间全区域监控（发送到787909466群）
INSERT INTO config.housing_notify_target (name, description, enabled) VALUES
('拂晓之间全区域监控', '监控拂晓之间服务器的所有住宅区', TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO config.housing_notify_server (target_id, server_id) VALUES
((SELECT id FROM config.housing_notify_target WHERE name = '拂晓之间全区域监控'), '1121')
ON CONFLICT DO NOTHING;

INSERT INTO config.housing_notify_area (target_id, area_id) VALUES
((SELECT id FROM config.housing_notify_target WHERE name = '拂晓之间全区域监控'), 0),
((SELECT id FROM config.housing_notify_target WHERE name = '拂晓之间全区域监控'), 1),
((SELECT id FROM config.housing_notify_target WHERE name = '拂晓之间全区域监控'), 2),
((SELECT id FROM config.housing_notify_target WHERE name = '拂晓之间全区域监控'), 3),
((SELECT id FROM config.housing_notify_target WHERE name = '拂晓之间全区域监控'), 4)
ON CONFLICT DO NOTHING;

INSERT INTO config.housing_notify_group (target_id, group_id) VALUES
((SELECT id FROM config.housing_notify_target WHERE name = '拂晓之间全区域监控'), '787909466')
ON CONFLICT DO NOTHING;

-- 示例2: 神意之地全区域监控（发送到595883141群）
INSERT INTO config.housing_notify_target (name, description, enabled) VALUES
('神意之地全区域监控', '监控神意之地服务器的所有住宅区', TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO config.housing_notify_server (target_id, server_id) VALUES
((SELECT id FROM config.housing_notify_target WHERE name = '神意之地全区域监控'), '1081')
ON CONFLICT DO NOTHING;

INSERT INTO config.housing_notify_area (target_id, area_id) VALUES
((SELECT id FROM config.housing_notify_target WHERE name = '神意之地全区域监控'), 0),
((SELECT id FROM config.housing_notify_target WHERE name = '神意之地全区域监控'), 1),
((SELECT id FROM config.housing_notify_target WHERE name = '神意之地全区域监控'), 2),
((SELECT id FROM config.housing_notify_target WHERE name = '神意之地全区域监控'), 3),
((SELECT id FROM config.housing_notify_target WHERE name = '神意之地全区域监控'), 4)
ON CONFLICT DO NOTHING;

INSERT INTO config.housing_notify_group (target_id, group_id) VALUES
((SELECT id FROM config.housing_notify_target WHERE name = '神意之地全区域监控'), '595883141')
ON CONFLICT DO NOTHING;

COMMIT;
