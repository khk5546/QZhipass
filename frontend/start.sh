#!/bin/bash
# 前端测试页面启动脚本
# 使用：./start.sh
# 前提：只需 Python3（macOS/Linux 自带）

PORT=8080
echo "========================================="
echo "  企智通前端测试页面"
echo "  地址：http://localhost:$PORT"
echo "========================================="
echo ""
echo "管理员页面：http://localhost:$PORT/index.html"
echo "用户测试页：http://localhost:$PORT/user-test.html"
echo ""
echo "提示：确保后端已启动（http://localhost:7511）"
echo "停止服务：Ctrl+C"
echo "========================================="
echo ""

cd "$(dirname "$0")"
python3 -m http.server $PORT
