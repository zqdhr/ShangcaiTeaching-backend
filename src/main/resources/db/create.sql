CREATE TABLE role(
    id VARCHAR(32) NOT NULL,
    role_name VARCHAR(128),
    permission_product BIGINT,
    created_time DATE,
    updated_time DATE,
    PRIMARY KEY (id)
);;

COMMENT ON TABLE role IS '用户管理角色信息';;
COMMENT ON COLUMN role.id IS '主键';;
COMMENT ON COLUMN role.role_name IS '角色名称';;
COMMENT ON COLUMN role.permission_product IS '素数权限的乘积';;
COMMENT ON COLUMN role.created_time IS '创建时间';;
COMMENT ON COLUMN role.updated_time IS '更新时间';;


