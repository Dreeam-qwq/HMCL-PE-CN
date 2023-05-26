<h1 align="center">HMCL-PE非官方版</h1>

- 适合服务器客户端使用，内置了直装模块 [assets/.minecraft]
- 去除res资源目录混淆以便用户修改
- 支持删除原整合包数据后在释放文件
- 支持使用文本编写外置皮肤站地址 [assets/authlib_injector_server.json]
- 可启动1.19.3+ [Boat、PojavLauncher后端均可]
- 修复1.13.X-1.15.X版本中游戏内材质大面积透视BUG [仅PojavLauncher后端修复]
- 支持自定义布局文件 [assets/control/Default]
- 提供源码让用户自行改动项目，有文本说明提示部分类文件作用
- 支持在配置文件中自定义某些设置 [assets/config.properties]
- 修复皮肤站链接非https协议某些用户无法登录BUG [其实是官方要求强制https协议，只是删除这些强制要求]
- 修复部分皮肤站使用Java8启动游戏崩溃BUG
