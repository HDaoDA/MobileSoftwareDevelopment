# Lab13 实验报告

## 1. 为什么使用 Apifox Mock 接口
- 提供稳定、可控的测试数据
- 不受后端服务影响
- 适合学习网络请求、状态管理

## 2. Retrofit 服务接口定义
- 使用 @GET 注解声明请求
- 方法用 suspend 修饰，支持协程
- 返回 List<BookDto> 解析 JSON

## 3. Repository 隔离作用
- ViewModel 不直接依赖 Retrofit
- 可轻松切换网络/离线数据源
- 便于测试和解耦

## 4. UI 状态切换逻辑
- 进入页面 → Loading
- 请求成功 → Success（显示网格）
- 请求失败 → Error（显示重试）
- 点击条目 → 弹出详情弹窗

## 5. 运行截图
见 screenshot.png

## 6. 遇到的问题
- 图片加载失败：检查 URL 与网络权限
- 状态不更新：检查 viewModelScope
- 弹窗不显示：确保 selectedBook 状态正确