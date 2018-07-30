# 垃圾收集器与内存分配策略

## 对象

#### 引用计数法
#### 可达性分析算法：
通过一系列 GC Roots的对象作为起始点，从节点开始向下搜索（引用链），当一个对象到GC Roots没有任何引用链，则证明此对象不可用。

**GC Roots**
- 虚拟机栈（本地变量表）中的引用对象
- 方法区中类静态属性引用的对象
- 方法区中常量引用的对象
- 本地方法栈中JNI引用的对象

#### 引用

- **强引用**：`new`
- **软引用**：对于软引用关联的对象，在系统将要发生内存溢出异常之前，将会把对象列回收范围之中进行第二次回收
- **弱引用**：只能生存到下一次垃圾收集发生之前，无论内存是否足够，都会收集只被弱引用关联的对象
- **虚引用**：为一个对象设置虚引用关联的目的为能在对象被收集器回收时收到一个系统通知

## 垃圾回收算法

**标记-清除算法**
**复制算法**
**标记-整理算法**

## HotSpot的算法实现

## 垃圾收集器

![收集器](http://p82ueiq23.bkt.clouddn.com/%E6%94%B6%E9%9B%86%E5%99%A8.png)

### Serial 收集器
Serial 收集器是一个单线程收集器。在它进行垃圾收集时，必须暂停其他所有的工作线程，直到它收集结束。

优点：简单而高效（与其他收集器的单线程相比）。

![serial](http://p82ueiq23.bkt.clouddn.com/serial.png)

### ParNew收集器

ParNew 收集器其实就是 Serial 收集器的多线程版本。
它是许多运行在 Server 模式下的虚拟机中首选的新生代收集器，其中一个与性能无关的很重要的原因是，除了 Serial 收集器外，目前只有它能与 CMS 收集器配合工作。

![parNew](http://p82ueiq23.bkt.clouddn.com/parNew.png)

#### Parallel Scavenge 收集器（吞吐量优先收集器）
Parallel Scavenge 收集器是一个新生代收集器，它也是使用 **复制算法** 的收集器，又是并行的多线程收集器。

区别：

- **Parallel Scavenge**：达到一个可控制的吞吐量。
- **CMS**：尽可能缩短垃圾收集时用户的等待时间

![parallel-scavenge](http://p82ueiq23.bkt.clouddn.com/parallel-scavenge.png)

Parallel Scavenge 收集器气提供了两个参数用于精确控制吞吐量
最大垃圾收集停顿时间： -XX:MaxGCPauseMills
吞吐量大小：-XX:GCTimeRatio
MaxGCPauseMills 参数允许的值是一个大于0的毫秒数，收集器将尽可能地保证内存回收所花费的时间不超过设定值。但 GC 的停顿时间缩短是以牺牲吞吐量和新生代空间来换取的。停顿时间下降，但吞吐量也降下来了。

GCTimeRatio 参数的值是一个大于0且小于100的整数，也就是垃圾收集时间占总时间的比例，相当于吞吐量的倒数。区间 1/(1+99) ~ 1/(1+1)，即 1% ~ 50%。

#### Serial Old收集器

Serial Old 是 Serial 收集器的老年代版本，它同样是一个单线程收集器，使用”标记-整理“算法。

![serial-old](http://p82ueiq23.bkt.clouddn.com/serial-old.png)

#### Parallel Old 收集器
Parallel Old 是 Parallel Scavenge 收集器的老年代版本，使用多线程和”标记-整理“算法。

![parallel-old](http://p82ueiq23.bkt.clouddn.com/parallel-old.png)

### CMS 收集器（标记-清除）

CMS （Concurrent Mark Sweep）收集器是一种以获取最短回收停顿时间为目标的收集器。基于”标记-清除“算法那实现的，他的运作过程更复杂一些，整个过程分为4个步骤，
- 初始标记（CMS initial mark）
- 并发标记（CMS concurrent mark）
- 重新标记（CMS remark）
- 并发清除（CMS Concurrent sweep）
  
![cms](http://p82ueiq23.bkt.clouddn.com/cms.png)

其中，初始标记、重新标记着两个步骤任然需要”Stop The World“。初始标记仅仅只是标记一下 GC Roots 能直接关联到的对象，速度很快，并发标记阶段就是进行 GC Roots Tracing 的过程，而重新标记阶段则是为了修正并发标记期间因用户程序继续运作而导致标记产生变动的那一部分对象的标记记录，这个阶段的停顿时间一般会比初始标记阶段稍长一些，但远比并发标记时间短。

**3个明显的缺点：**

- CMS 收集器 **读 CPU 资源非常敏感**。在并发阶段，它虽然不会导致用户线程停顿，但是会因为占用了一部分线程（或者说CPU资源）而导致应用程序变慢，总吞吐量会降低。CMS 默认启动的回收线程数是 (CPU数量+3)/4，也就是当 CPU 在4个以上时，并发回收时垃圾收集器线程不少于25%的 CPU 资源，并且伴随着CPU数量的增加而下降。

- CMS 收集器无法处理浮动垃圾（Floating Garbage），可能出现“Concurrent Mode Failure”失败而导致另外一次 Full GC 产生。由于 CMS 并发清理阶段用户线程还在运行着，伴随着程序运行自然就还会有新的垃圾不断产生，着一部分垃圾出现在标记过程之后，CMS无法在当次收集中处理掉它们，只好留待下一次 GC 时再清理掉。

- CMS 收集器不能像其他收集器那样等到老年代几乎完全被填满了再进行收集，需要预留一部分空间提供并发收集时的程序运行使用。

#### G1 收集器

G1基于“标记–整理”算法实现，不会产生空间碎片，对于长时间运行的应用系统来说非常重要；另外它可以非常精准地控制停顿，既能让使用者指定一个长度为M毫秒的时间片段内，消耗在垃圾收集上的时间不得超过N毫秒。

- 初始标记（initial mark）
- 并发标记（concurrent mark）
- 最终标记（final mark）
- 筛选回收（Live data counting and Evacuation）

**优点**

1. 并发与并行
2. 分代收集
3. 空间整合
4. 可预测的停顿

## 内存分配与回收策略

### 对象优先在Eden分配

### 大对象直接进入老年代

### 长期存活的对象进入老年代

### 动态对象年龄判定

### 空间分配担保