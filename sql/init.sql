create table t_user(
  id VARCHAR(64) PRIMARY KEY COMMENT '主健',
  username VARCHAR(64) COMMENT '用户名',
  password VARCHAR(128) COMMENT '密码',
  salt VARCHAR(64) COMMENT '盐',
  creator VARCHAR(64) COMMENT '创建者',
  create_time TIMESTAMP DEFAULT NOW() COMMENT '创建时间',
  editor VARCHAR(64) COMMENT '修改者',
  edit_time TIMESTAMP DEFAULT NOW() COMMENT '修改时间',
  is_delete INT DEFAULT 0 COMMENT '删除标志'
) COMMENT '用户基础表' CHARSET utf8mb4;

create table t_role(
  id VARCHAR(64) PRIMARY KEY COMMENT '主健',
  parent_id VARCHAR(64) COMMENT '父角色id',
  name VARCHAR(64) COMMENT '角色名称',
  description VARCHAR(128) COMMENT '角色描述',
  creator VARCHAR(64) COMMENT '创建者',
  create_time TIMESTAMP DEFAULT NOW() COMMENT '创建时间',
  editor VARCHAR(64) COMMENT '修改者',
  edit_time TIMESTAMP DEFAULT NOW() COMMENT '修改时间',
  is_delete INT DEFAULT 0 COMMENT '删除标志'
) COMMENT '角色表' CHARSET utf8mb4;

create table t_user_role(
  id VARCHAR(64) PRIMARY KEY COMMENT '主健',
  user_id VARCHAR(64) COMMENT '用户id',
  role_id VARCHAR(64) COMMENT '角色id',
  creator VARCHAR(64) COMMENT '创建者',
  create_time TIMESTAMP DEFAULT NOW() COMMENT '创建时间',
  editor VARCHAR(64) COMMENT '修改者',
  edit_time TIMESTAMP DEFAULT NOW() COMMENT '修改时间',
  is_delete INT DEFAULT 0 COMMENT '删除标志'
) COMMENT '用户角色关联表' CHARSET utf8mb4;

create table t_permission(
  id VARCHAR(64) PRIMARY KEY COMMENT '主健',
  parent_id VARCHAR(64) COMMENT '父权限id',
  permission VARCHAR(64) COMMENT '权限',
  description VARCHAR(64) COMMENT '权限描述',
  creator VARCHAR(64) COMMENT '创建者',
  create_time TIMESTAMP DEFAULT NOW() COMMENT '创建时间',
  editor VARCHAR(64) COMMENT '修改者',
  edit_time TIMESTAMP DEFAULT NOW() COMMENT '修改时间',
  is_delete INT DEFAULT 0 COMMENT '删除标志'
) COMMENT '权限表' CHARSET utf8mb4;

create table t_role_permission(
  id VARCHAR(64) PRIMARY KEY COMMENT '主健',
  role_id VARCHAR(64) COMMENT '角色id',
  permission_id VARCHAR(64) COMMENT '权限id',
  creator VARCHAR(64) COMMENT '创建者',
  create_time TIMESTAMP DEFAULT NOW() COMMENT '创建时间',
  editor VARCHAR(64) COMMENT '修改者',
  edit_time TIMESTAMP DEFAULT NOW() COMMENT '修改时间',
  is_delete INT DEFAULT 0 COMMENT '删除标志'
) COMMENT '角色权限关联表' CHARSET utf8mb4;