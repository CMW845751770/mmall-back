# mmall-back


## 项目介绍

`mmall-back`项目是一套基于SpringCloud+SpringBoot+MyBatis的电商系统的后端实现，因为时间原因只实现了前台商城系统，后台管理系统有时间我也会弄。前台商城系统包含首页门户、商品搜索、商品展示、购物车、订单流程、会员中心、用户支付等模块。

### 项目演示

#### 前台商城系统前端

前端项目是基于`MMall-front-end-web`这个仓库改造得来的，这是我在GitHub上找到的别人开源的一个商城前端项目，前端项目的部署完全可以参考这个项目。仓库地址：https://github.com/Paladinhanxiao/MMall-front-end-web.git 。


#### 前台商城系统演示
##### 部分截图
![enter image description here](http://img.sgxm.tech/001.PNG)
![enter image description here](http://img.sgxm.tech/002.PNG)
![enter image description here](http://img.sgxm.tech/003.PNG)
![enter image description here](http://img.sgxm.tech/004.PNG)
![enter image description here](http://img.sgxm.tech/005.PNG)
![enter image description here](http://img.sgxm.tech/006.PNG)
项目演示地址：http://cmwmall.xyz:4869
由于时间原因没有对服务器和域名进行备案，所以采用的是4869端口，测试的用户账号为admin，密码为admin123。注意：因为项目集成的支付功能并不是真实的支付，而是基于支付宝沙箱的在线支付，所以如果您想体验支付功能，请先在[支付宝开放平台](https://openhome.alipay.com/platform/home.htm)注册账号，并下载[支付宝沙箱客户端](https://sandbox.alipaydev.com/user/downloadWallet.htm),目前支付宝沙箱仅支持Android，如果您是IOS手机可以选择使用模拟器。最后提醒：因为本项目仅仅使用三台配置极低的学生机完成，所以请不要对系统进行压测。因为服务器配置较低，如果出现页面或者数据加载失败，刷新即可。如果出现支付功能不可用时，这是由于支付宝沙箱的不稳定，您可以选择其他时段进行测试，谢谢！

### 组织结构

``` lua
mmall
├── mmall-commons -- 工具类及通用代码
├── mmall-user -- 前台商城的用户模块
├── mmall-product -- 前台商城的商品模块，包含商品的搜索和商品的展示等
├── mmall-cart -- 前台商城的购物车模块
├── mmall-order -- 前台商城的订单模块
├── mmall-pay -- 前台商城的支付模块
├── mmall-eureka -- 商城系统的eureka注册中心
├── mmall-config -- 商城系统的配置中心
├── mmall-consumer -- 前台商城的消费者
└── mmall-zuul -- 商城系统的zuul网关
```

### 技术选型

| 技术                 | 说明                | 官网                                                         |
| -------------------- | ------------------- | ------------------------------------------------------------ |
| SpringBoot           | 容器+MVC框架        | [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)|
| SpringCloud          | 分布式微服务框架        | [https://spring.io/projects/spring-cloud](https://spring.io/projects/spring-cloud)|
| MyBatis              | ORM框架             | [http://www.mybatis.org/mybatis-3/zh/index.html](http://www.mybatis.org/mybatis-3/zh/index.html) |
| MyBatisGenerator     | 数据层代码生成      | [http://www.mybatis.org/generator/index.html](http://www.mybatis.org/generator/index.html) |
| PageHelper           | MyBatis物理分页插件 | [http://git.oschina.net/free/Mybatis_PageHelper](http://git.oschina.net/free/Mybatis_PageHelper) |
| Hibernator-Validator | 验证框架            | [http://hibernate.org/validator/](http://hibernate.org/validator/) |
| RabbitMq             | 消息队列            | [https://www.rabbitmq.com/](https://www.rabbitmq.com/)       |
| Redis                | 分布式缓存          | [https://redis.io/](https://redis.io/)                       |
| Docker               | 应用容器引擎        | [https://www.docker.com/](https://www.docker.com/)           |
| Rancher               | 全栈化容器管理平台        | [https://www2.cnrancher.com/](https://www2.cnrancher.com/)           |
| Druid                | 数据库连接池        | [https://github.com/alibaba/druid](https://github.com/alibaba/druid) |
| Lombok               | 简化对象封装工具    | [https://github.com/rzwitserloot/lombok](https://github.com/rzwitserloot/lombok) |

### 架构图

#### 系统架构图
![enter image description here](http://img.sgxm.tech/008.PNG)
首先收到用户请求的是nginx服务器，根据用户请求的url跳转到前端的Html页面，前端通过nginx的反向代理指向后端的zuul网关，zuul网关再根据请求的uri路由到不同的服务上。比如uri为/user/login.do映射到用户登录服务，/product/list.do则映射到商品展示服务。

zuul的作用是限流、鉴权以及路由转发。这里我限流的功能是通过guava的令牌桶来实现的。

应用服务器方面实现了动态刷新配置(`spring cloud config`+`bus`)以及单点登录(`redis + cookie`)等功能
#### 业务架构图

![enter image description here](http://img.sgxm.tech/捕获.PNG)

### 核心模块实现
- 用户模块

关于用户模块，核心的功能是登陆。再核心是如何验证以及如何存储用户信息。这里采取的方案为：

![enter image description here](http://img.sgxm.tech/user.png)使用redis+cookie的组合实现分布式系统的单点登录来替换单体项目的使用Session方式的登录

对于未登陆状态下用户找回密码，逻辑为：

![enter image description here](http://img.sgxm.tech/user-2.png)

- 购物车模块

![enter image description here](http://img.sgxm.tech/cart.png)

- 订单模块

![enter image description here](http://img.sgxm.tech/009.PNG)
订单模块是个人认为最复杂的一个模块。我是用的策略是用户创建完订单后，在数据库中插入状态为WAITING状态的订单，然后使用消息队列rabbitMQ异步通知商品模块扣库存来达到一个解耦的效果，这里我的rabbitMQ使用的是topic模式。使用rabbitMQ的confirm机制来保证消息能够可靠地到达broker，在消息地消费端也就是扣库存地那个方法，如果我扣库存失败了，我选择让其重试一个指定的次数，如果达到最大次数还是失败，那么就将这条消息计如异常日志里面。再进行人工补偿。待消息消费成功后需要通知订单模块修改订单的状态为未支付，如果订单状态不是未支付，用户不能支付该订单。

这里除了以上的问题还需要考虑消息的幂等性、分布式事务。对于消息的幂等性我准备使用redis的分布式锁实现。在消息的消费端使用redis存储一个分布式的唯一键，以此来保证每个订单消息只会被消费一次。分布式事务则可以考虑使用阿里的rocketMQ来替代rabbitMQ，因为rocketMQ天然就能够用来解决分布式事务。

- 支付模块

商户前台将商品参数发送至商户后台，商户后台生成**内部订单号**并用于请求支付平台创建**预下单**，支付平台创建完预订单后将订单二维码信息返还给商户，此时用户即可扫取二维码进行付款操作。

系统需要将一系列的数据按照支付宝的要求发送给支付宝平台，包括商品信息，生成的验签sign，公钥；支付宝去将sign解密，进行商品的各种信息校验。校验通过，**同步**返回二维码串。

支付业务流程图：

![enter image description here](http://img.sgxm.tech/pay-1.png)

在获取支付的二维码串之后，用工具包将其转换未二维码展示给用户扫码。

用户扫码后，会收到第一次支付宝的回调，展示要支付的金额，商品信息等。

用户输入密码成功后，正常情况会收到支付宝的第二次回调，即支付成功信息。

但是也可能会由于网络等原因，迟迟收不到支付宝的回调，这个时候就需要主动发起轮询去查看支付状态。

在支付成功之后，接收回调的接口返回`success`告诉支付宝系统已经收到你的回调了，不要再重复发送。

![enter image description here](http://img.sgxm.tech/pay-2.png)


## 环境搭建

### 开发工具

| 工具          | 说明                | 官网                                            |
| ------------- | ------------------- | ----------------------------------------------- |
| IDEA          | 开发IDE             | https://www.jetbrains.com/idea/download         |
| RedisDesktop  | redis客户端连接工具 | https://redisdesktop.com/download                                          |
| X-shell       | Linux远程连接工具   | http://www.netsarang.com/download/software.html |
| Navicat       | 数据库连接工具      | http://www.formysql.com/xiazai.html                                                                                             |
| ProcessOn     | 流程图绘制工具      | https://www.processon.com/                                              |
| Snipaste      | 屏幕截图工具        | https://www.snipaste.com/                       |
| Postman      | 接口测试工具        | https://www.getpostman.com/                     |

### 开发环境

| 工具          | 版本号 | 下载                                                         |
| ------------- | ------ | ------------------------------------------------------------ |
| JDK           | 1.8    | https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html |
| Mysql         | 8.0    | https://dev.mysql.com/downloads/mysql/                                       |
| Redis         | 5.0.4    | https://redis.io/download                                                        |
| RabbitMq      | 3.7.7 | http://www.rabbitmq.com/download.html                        |
| Nginx         | 1.16   | http://nginx.org/en/download.html                            |
| Docker         | 1.16   | https://www.docker.com/                           |
|
| Rancher         | 1.2.11   | https://www2.cnrancher.com/                           |
| Linux | Centos7   | http://mirror.centos.org/centos/7/isos/                           |
### 搭建步骤

> Windows环境部署
使用Git从仓库克隆至本地，使用IDEA导入项目，因为本项目是使用Gradle构建，所以请选择导入一个Gradle项目。![enter image description here](http://img.sgxm.tech/010.PNG)
导入项目后导入数据库sql文件，配置好redis ip、rabbitMQ ip、Config Server信息以及图片服务器等信息。因为我是用的配置方式是Config Server从GitHub上拉取配置然后通过应用通过 Stream 获取配置信息。所以你需要在GitHub、码云或者GitLab创建你的配置仓库，然后将模块下的application.yml文件上传到到仓库中。你可以按照我这个开源的配置仓库这样配置：https://github.com/CMW845751770/mmall-back-config.git 。最后按照Eureka -> Config -> User -> Product  -> Cart -> Order -> Pay -> Consumer -> Zuul 的顺序启动项目即可。前端项目的部署和我开头所说的一样请参照最上面的那个开源仓库。

> Docker环境部署
使用IDEA导出各个模块的SpringBoot Jar包，并上传至阿里云或者网易云的镜像仓库中，在你的服务器上安装Rancher，在Rancher中创建主机(这需要另外一台服务器)，然后再主机上通过从镜像仓库上拉去镜像的方式创建服务、应用。![enter image description here](http://img.sgxm.tech/007.PNG)


## 后期展望
- 商品搜索服务使用ElasticSearch 替换简单的数据库查询。
- 使用第三方对象存储如七牛云替换文件服务器。
- 使用RocketMQ替换rabbitMQ解决分布式事务问题
- 加入商品推荐系统
-  ... 
...
