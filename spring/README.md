# Spring Framework Runtime

![spring framework](http://p82ueiq23.bkt.clouddn.com/spring%20framework.png)

## Spring核心容器

最核心的部分，用于管理Spring应用中bean的创建、配置和管理

- IoC容器
    - IoC概述
    - 相关Java基础知识
    - 资源访问利器
    - BeanFactory和ApplicationContext
    - Bean的生命周期
- 在IoC容器中装配Bean
    - 依赖注入
    - 注入参数详解
    - 方法注入
    - \<bean\>之间的关系
    - Bean的作用域
    - FactoryBean
    - 基于注解的配置
    - 基于Java类的配置

## AOP模块

Spring对面向切面编程提供了支持

- 基础知识
- 创建增强类
- 创建切面
- 自动创建代理

## 数据访问与集成

**JDBC和DAO**：抽象了样板代码，使数据库代码变得简单，还避免因为关闭数据库资源失败而引发的问题

**ORM**

## Spring的事务管理

- 数据库事务基础知识
- ThreadLocal基础知识
- Spring对事务管理的支持
- 编程式的事务管理