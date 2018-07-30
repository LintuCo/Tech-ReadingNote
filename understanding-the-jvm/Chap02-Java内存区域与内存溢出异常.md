# Java内存区域与内存溢出异常

### 运行时数据区域

<div align=center>

![jvm_architecture](http://p82ueiq23.bkt.clouddn.com/jvm_architecture.jpg)
![Java内存](http://p82ueiq23.bkt.clouddn.com/Java%E5%86%85%E5%AD%98.PNG)
</div>

#### 程序计数器（PC Register）

- 多线程是通过线程轮流切换并分配处理器执行时间的方式来实现的。
- 如果线程正在执行Java方法，PC的记录是正在执行的虚拟机字节码指令的地址，如果是`Native`方法，计数器值为空

#### Java虚拟栈

**栈帧（Stack Frame）**
- 局部变量表：编译器可知的基本数据类型、对象引用
- 操作数栈
- 动态链接
- 方法出口

**StackOverflowError异常**
- 线程请求的栈深度大于虚拟机所允许的深度
- 虚拟机栈动态扩展时无法申请到足够的内存

#### 本地方法栈

为虚拟机使用到的Native方法服务

**Native method**
"A native method is a Java method whose implementation is provided by non-java code."

#### Java堆

Java虚拟机所管理的内存中最大的一块，被所有线程共享的一块内存区域，**用于存放对象实例**。

Java堆是垃圾收集管理的主要区域，可以被细分为新生代、老生代

![GC-heap](http://p82ueiq23.bkt.clouddn.com/GC-heap.gif)

#### 方法区

用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码数据

**运行时常量池**：用汉语存放编译器生成的各种字面量和符号引用。

### HotSpot虚拟机对象

#### 对象的创建

1. 虚拟机遇到一条new的指令
2. 检查这个指令的参数是否能在常量池中定位到一个类的符号引用
3. 检查符号引用代表的类是否已被加载、解析和初始化
4. 虚拟机为新生对象分配内存
    1. 指针碰撞（Serial、ParNew）
    2. 空闲列表（CMS）
5. 虚拟机对对象进行必要设置（Object Header）
6. 执行new指令后执行`<init>`方法

#### 对象的内存布局

**对象头**

HotSpot虚拟机对象头包括两部分信息：
- 用于存储对象自身的运行时数据（哈希码、GC分代年龄、锁状态标志、线程持有的锁、偏向线程ID、偏向时间戳）
- 对象指向他的类元数据的指针

#### 对象的访问定位

**句柄访问** - Java堆中会划分出一块内存来存储句柄池，`reference`中存储的就是对象的句柄地址，句柄中含有对象实例数据与类型数据各自的地址信息。**在对象被移动时，只需要改变句柄中的实例数据指针，而`reference`不需要修改**
**指针访问** - `reference`存储的直接就是对象地址，速度快

[参考资料]
1. [JVM 的 工作原理，层次结构 以及 GC工作原理](https://segmentfault.com/a/1190000002579346)