# 企智通 — 前端测试页面

## 快速开始

```bash
# 1. 启动前端（只需 Python3，macOS/Linux 自带）
cd frontend
./start.sh
# 或手动：python3 -m http.server 8080

# 2. 确保后端已启动（另开终端）
# API 地址：http://localhost:7511
```

打开浏览器访问：
- **管理员页面**：http://localhost:8080/index.html
- **用户测试页面**：http://localhost:8080/user-test.html

---

## 测试账号

| 手机号 | 姓名 | 状态 |
|--------|------|------|
| 13800138001 | 张三 | 正常 |
| 13800138002 | 李四 | **已注销** |
| 13800138003 | 王五 | **已注销** |
| 13800138004 | 赵六 | 正常 |
| 13800138005 | 孙七 | 正常 |

> 完整账号清单见[测试文档](./README.md)（本目录下的 `index.html` / `user-test.html` 旁）

---

## 后端 API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/admin/users` | GET | 获取用户列表 |
| `/api/admin/users/{id}` | DELETE | 注销用户 |
| `/api/user/profile` | GET | 获取用户资料（受拦截器保护） |

> 详细 API 文档和测试步骤见**测试文档**（在同一目录下打开 `index.html` 即可开始测试）

---

## 目录结构

```
frontend/
├── index.html       # 管理员视角 — 用户管理界面
├── user-test.html   # 用户视角 — 注销拦截测试
├── README.md        # 本文档（含测试步骤）
└── start.sh         # 一键启动前端 HTTP 服务
```
