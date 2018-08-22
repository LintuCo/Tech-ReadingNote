# Java基础问题

- [equal方法与hashCode](.\Java基础\equal方法与hashCode解读.md)
- asm与反射的区别优缺点，能否获取运行时泛型
- 自动装箱优化
- StringBuffer和StringBuilder有什么区别
- c++与java的区别，c++与java的内存管理差异，c++的内存分配方式，new/malloc的区别，引用与指针的区别，简单提及jvm
- handle的作用；
- JDK同步机制是怎样？

### 设计模式

- 策略模式
- 适配器模式
- 单例设计模式
- 工厂模式
- 动态代理

# 底层原理

### Java源码

- hashmap原理
- hashset原理
- 对象头
- 对象内存布局，然后讲下对象的死亡过程
- hashmap的扩容
- 自动装箱与拆箱
- hashCode和equals方法的关系


### JVM

- [JVM调优](..\understanding-the-jvm\Java虚拟内存.md)
- Jvm新生代旧生
- ROOT对象

### Concurrency

- concurrentHashmap原理
- AQS
- 锁膨胀
- synchronized 与lock区别
- synchronized，可重入怎么实现
- wait方法底层原理
- join,notify,notifyall
- 线程池原理
- 承Thread类和实现Runnable接口的比较

# 算法问题

- 丑数

# 框架相关

### 注解
- Controller与RestController
- Autowired
- Transactional
- Service
- Bean
- SpringMVC的URL映射原理

- bean的生命周期
- AOP和IOC
- 动态代理2种实现
- JDBC的反射，反射都是什么
- Spring Boot框架
- 说一下controller注解
- springmvc底层处理请求顺序，bean生命周期，autowire机制，controller机制
处理器适配器怎么找到对应的controller
IO流熟悉吗，用的什么设计模式。
spring中各种context有什么功能，他们之间有什么联系

# 基础课程

### 计算机网络

- OSI参考模型与TCP/IP参考模型
- TCP与UDP
- ICMP协议
- arp协议，arp攻击
- [HTTP请求流程]()
- HTTP头
- GET和POST区别
- http和https的区别
- get提交是否有字节限制，如果有是在哪限制的
- HTTP报文内容
- 超时重传机制
- token相关
- 三次握手、四次握手
- token相关，加salt相关，最后得出结论加盐之后也最好不要用md5算法
- SSL套接层
- session和cookie区别
- redirect与forward区别


### 操作系统

# 数据库

- mysql的联合索引
- mysql引擎（Innodb, MyIsam），什么时候用Innodb，什么时候用MyIsam
- 一致性Hash(Consistent Hashing)原理剖析
- 数据库优化
- 数据库水平切分，垂直切分
- 事务传播机制
- 组合索引什么时候失效
- 唯一索引，符合索引
- redis的pipeline有什么用处
- redis高级特性
- redis中的高级应用
- Hadoop的HDFS的优劣
- Redis集群搭建过程和主从复制原理
- 数据库索引有哪些，什么时候会失效，索引底层是怎么实现的
- redis是单线程还是多线程的
- redis为什么是单线程的
- server和client通信方式
- redis中事务
- 排序算法
- es分词算法
- es分词选型。ik分词器
- 优化sql的一些方式（explain，profile，设置慢日志查询等）


# 数据结构

# 其他

- NIO和BIO区别
- Servlet请求流程？
- servelet的生命周期
- HttpServlet中一些的方法
- 系统高并发

# 分布式

- 分布式集群中如何保证线程安全
- MyCAT分片规则和原理
- 惊群和雪崩效应
- 负载均衡
- 