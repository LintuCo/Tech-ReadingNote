# IoC容器

## 前言

IOC容器就是具有依赖注入功能的容器，IOC容器负责实例化、定位、配置应用程序中的对象及建立这些对象间的依赖。

Spring IOC容器通过读取配置文件中的配置元数据，通过元数据对应用中的各个对象进行实例化及装配。一般使用基于xml配置文件进行配置元数据，而且Spring与配置文件完全解耦的，可以使用其他任何可能的方式进行配置元数据

### IoC的类型

1. 构造函数注入：通过调用类的构造函数，将接口实现类通过构造函数变量传入。
2. 熟悉注入
3. 接口注入：将调用类所有依赖注入的方法抽取到一个接口中，调用类通过实现接口提供相应的注入方式。

## 相关Java基础知识

### 类装载器ClassLoader

1. 装载：查找和导入Class文件
2. 链接：执行校验、准备和解析步骤，其中解析步骤是可以选择的
    1. 校验：检查载入Class文件数据的正确性
    2. 准备：给类的静态变量分配存储空间
    3. 解析：将符号引用转换成直接应用
3. 初始化：对类的静态变量、静态代码块执行初始化操作

### Java反射机制

Class反射对象描述类的语义结构，从Class对象中获取构造函数、成员标量、方法类等元素的反射对象

## BeanFactory和ApplicationContext

### BeanFactory

BeanFactory为一个类工厂，可以创建并管理各种类的对象

**区别**
BeanFactory 可以理解为含有bean集合的工厂类。BeanFactory 包含了种bean的定义，以便在接收到客户端请求时将对应的bean实例化。

BeanFactory还能在实例化对象的时生成协作类之间的关系。此举将bean自身与bean客户端的配置中解放出来。BeanFactory还包含了bean生命周期的控制，调用客户端的初始化方法（initialization methods）和销毁方法（destruction methods）。

从表面上看，application context如同bean factory一样具有bean定义、bean关联关系的设置，根据请求分发bean的功能。但application context在此基础上还提供了其他的功能。

1. 提供了支持国际化的文本消息
2. 统一的资源文件读取方式
3. 已在监听器中注册的bean的事件