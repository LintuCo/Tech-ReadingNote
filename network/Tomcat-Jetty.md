# Tomcat 总体结构

![tomcat总体结构](https://www.ibm.com/developerworks/cn/java/j-lo-tomcat1/image001.gif)

## Server

**connector**主要负责对外交流，**container**主要处理connector接受的请求，主要处理内部事务。**server**将两者组装起来，向外界提供服务，**一个server可以设置多个Connector，但是只能有一个container**

![Server接口](https://www.ibm.com/developerworks/cn/java/j-lo-tomcat1/image002.png)

Server主要的作用就是关联Connector和Container，通过继承Lifecycle接口来控制组件的生命周期

## connector

**主要任务**：负责接受浏览器发过来的TCP连接请求，创建`Request`和`Responce`对象分别用于和请求端交换数据，会产生一个线程用来处理这个请求并把产生的`Request`和`Responce`对象传递给处理这个请求的线程，container处理这个请求的线程。

![connector](http://p82ueiq23.bkt.clouddn.com/connector.png)

- 使用`ProtocalHandler`处理请求，不同的`ProtocalHandler`代表不同的连接类型

**ProtocalHandler**
- Endpoint：处理底层socket的网络连接，用来实现TCP/IP协议
    - Acceptor：监听请求
    - AsyncTimeout：检查异步Request的超时
    - Handler：处理接收的socket
- Processor：用于将Endpoint接收到的`Socket`封装成`Request`，实现HTTP协议
- Adapter：用于将`Request`交给`Container`进行具体处理（适配）

## container

Container 是容器的父接口，所有子容器都必须实现这个接口，Container 容器的设计用的是典型的责任链的设计模式，它有四个子容器组件构成，分别是：Engine、Host、Context、Wrapper，这四个组件是父子关系，通常一个 Servlet class 对应一个 Wrapper，如果有多个 Servlet 就可以定义多个 Wrapper，如果有多个 Wrapper 就要定义一个更高的 Container 了

**Engine容器**：定义了基本的关联关系

**Host容器**：一个Host代表一个虚拟主机，用于运行多个运用，负责安装和展开应用，并且标识应用以便区分

**context容器**：管理servlet实例，servlet实例在Context中是以Wrapper出现的

**Wrapper容器**：负责管理一个servlet，包括servlet的装载、初始化、执行以及资源回收

## Tomcat 的运行模式

- bio
- nio
- apr

## 优化

- 执行器优化：通过使用线程池提高性能

# Jetty

![Jetty基本框架](https://www.ibm.com/developerworks/cn/java/j-lo-jetty/image003.jpg)

**核心组件**

- Server：基于Handler容器工作
- Connector：负责接受客户端的连接请求，并将请求分配给一个处理队列去执行

![Hetty主要组件](https://www.ibm.com/developerworks/cn/java/j-lo-jetty/image005.jpg)

Server 类继承了 Handler，关联了 Connector 和 Container。Container 是管理 Mbean 的容器。Jetty 的 Server 的扩展主要是实现一个个 Handler 并将 Handler 加到 Server 中，Server 中提供了调用这些 Handler 的访问规则。

**Handler**

- HandlerWrapper：将一个Handler委托给另外一个类去执行，`ScopeHandler`可以拦截Handler的执行
- HandlerCollection：多个Handler组装在一起，构成Handler连

## Jetty的启动过程

1. 启动设置到 Server 的 Handler。Server 会依次启动这个链上的所有 Handler。
2. 启动注册在 Server 上 JMX 的 Mbean，让 Mbean 也一起工作起来
3. 会启动 Connector，打开端口，接受客户端请求

## Jetty 创建连接

1. 创建一个队列线程池，用于处理每个建立连接产生的任务，这个线程池可以由用户来指定
2. 创建 ServerSocket，用于准备接受客户端的 socket 请求，以及客户端用来包装这个 socket 的一些辅助类。
3. 创建一个或多个监听线程，用来监听访问端口是否有连接进来。



## HTTP请求处理

1. Acceptor接受Socket连接
2. Accetptor 线程将会为这个请求创建 ConnectorEndPoint
3. HttpConnection 用来表示这个连接是一个 HTTP 协议的连接，它会创建 HttpParse 类解析 HTTP 协议，并且会创建符合 HTTP 协议的 Request 和 Response 对象
4. 将这个线程交给队列线程池去执行了。

## Tomcat VS Jetty

### 架构

- Jetty所有组件都是基于 Handler 来实现，当然它也支持 JMX；Tomcat 是以多级容器构建起来的

### 性能

- Tomcat 在处理少数非常繁忙的连接上更有优势
- Jetty 可以同时处理大量连接而且可以长时间保持这些连接

[Servlet工作原理](https://www.ibm.com/developerworks/cn/java/j-lo-servlet/)