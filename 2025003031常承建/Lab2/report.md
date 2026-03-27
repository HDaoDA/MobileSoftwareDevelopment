一、名片展示信息
本次实验完成的电子名片应用展示了以下个人信息：
姓名:常承建
职位：Android 开发工程师
联系电话：15752407238
电子邮箱：639242434@qq.com
社交账号：wechat
二、布局结构说明
整体界面使用 Jetpack Compose 声明式 UI 实现，布局结构清晰、层次分明：
根布局：使用 Column 纵向排列，分为上、下两大区域，并设置自定义深蓝色背景。
上半部分（CardTop）：
采用 Column 居中排列
包含头像图标（Icon）、姓名（Text）、职位（Text）
下半部分（CardBottom）：
采用 Column 纵向排列联系方式
封装 ContactRow 组件，每行使用 Row 横向组合图标 + 文字
使用 Spacer 控制间距，界面美观规整
核心组件：Column、Row、Icon、Text、Modifier、Spacer
三、遇到的问题与解决方法
问题：Unresolved reference: ui / BussinesscardTheme解决：删除不存在的主题引用，直接使用 Surface + 自定义颜色实现界面。
问题：Unresolved reference: drawable 图片资源报错解决：使用系统自带 Icons.Default.Person 图标代替本地图片，彻底规避资源引用错误。
问题：Compose 组件调用报错、排版错乱解决：规范 @Composable 函数结构，使用 Modifier 统一控制尺寸、间距、对齐方式。
问题：模拟器运行提示设备激活中解决：重启 ADB 服务、冷启动模拟器后正常运行。