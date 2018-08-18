## 原子变量与原子操作

**Compare and Swap（CAS）操作**：每次不加锁而是假设没有冲突而去完成某项操作，如果因为冲突失败就重试，直到成功为止。

CAS包括三个操作数，旧的预期值A，要修改的新值B。当且仅当预期值A和内存值V相同时，将内存值V修改为B，否则什么都不做。

**非阻塞算法**

一个线程的失败或者挂起不应该影响其他线程的失败或挂起的算法。

**AtomicInteger**

借助`volatile`原语，保证线程间数据的可见性

```java
public final int incrementAndGet() {
    for (;;) {
        int current = get();
        int next = current + 1;
        if (compareAndSet(current, next))
            return next;
    }
}

public final boolean compareAndSet(int expect, int update) {   
    return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
}
```

**CAS的缺陷**

ABA问题

## 锁

### volatile

**保证可见性、不保证原子性**
lock前缀的指令在多核处理器下会引发：

- 将当前处理器缓存行的数据会写回到系统内存。
- 写回内存的操作会引起在其他CPU里缓存了该内存地址的数据无效。

**禁止指令重排序**

指令的重排序：
   
- 编译器重排序。编译器在不改变单线程程序语义的前提下，可以重新安排语句的执行顺序；
- 处理器重排序。如果不存在数据依赖性，处理器可以改变语句对应机器指令的执行顺序；

**happens-before**：如果两个操作的执行顺序无法从happens-before原则中推倒出来，则不能保证其有序性，可以随意重排序

### synchronized

#### synchronized作用于实例方法

用synchronized修饰实例对象中的实例方法

#### synchronized作用于静态方法

其锁就是当前类的class对象锁

#### synchronized同步代码块

#### synchronized底层语义

Java 虚拟机中的同步(Synchronization)基于进入和退出管程(Monitor)对象实现， 同步方法调用指令读取运行时常量池中方法的 ACC_SYNCHRONIZED 标志来隐式实现的

![MarkWord](https://img-blog.csdn.net/20170603172215966?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvamF2YXplamlhbg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

**synchronized对象锁**

每个对象都存在着一个 monitor 与之关联，对象与其 monitor 之间的关系有存在多种实现方式，如monitor可以与对象一起创建销毁或当线程试图获取对象锁时自动生成，但当一个 monitor 被某个线程持有后，它便处于锁定状态。monitor是由ObjectMonitor实现的
ObjectMonitor中有两个队列，_WaitSet 和 _EntryList，用来保存ObjectWaiter对象列表( 每个等待锁的线程都会被封装成ObjectWaiter对象)，_owner指向持有ObjectMonitor对象的线程，当多个线程同时访问一段同步代码时，首先会进入 _EntryList 集合，当线程获取到对象的monitor 后进入 _Owner 区域并把monitor中的owner变量设置为当前线程同时monitor中的计数器count加1，若线程调用 wait() 方法，将释放当前持有的monitor，owner变量恢复为null，count自减1，同时该线程进入 WaitSe t集合中等待被唤醒。若当前线程执行完毕也将释放monitor(锁)并复位变量的值，以便其他线程进入获取monitor(锁)

![对象锁](https://img-blog.csdn.net/20170604114223462?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvamF2YXplamlhbg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

#### synchronized方法底层原理

方法级的同步是隐式，即无需通过字节码指令来控制的，它实现在方法调用和返回操作之中。JVM可以从方法常量池中的方法表结构(method_info Structure) 中的 ACC_SYNCHRONIZED 访问标志区分一个方法是否同步方法。当方法调用时，调用指令将会 检查方法的 ACC_SYNCHRONIZED 访问标志是否被设置，如果设置了，执行线程将先持有monitor， 然后再执行方法，最后再方法完成(无论是正常完成还是非正常完成)时释放monitor。

**锁的膨胀**

![微信图片_20180816202904](http://p82ueiq23.bkt.clouddn.com/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20180816202904.jpg)

**锁优化**

- 自旋锁
- 锁粗化
- 锁消除

## 显示锁

### ReentrantLock

**优点**

- 可以轮询和可定时的锁请求
- 可中断的所获取操作
- 公平锁

### ReadWriteLock

### ReentrantReadWriteLock

### 关于读写锁

- WriteLock可以降级为ReadLock，顺序是：先获得WriteLock再获得ReadLock，然后释放WriteLock,这时候线程将保持Readlock的持有
- ReadLock可以被多个线程持有并且在作用时排斥任何的WriteLock，而WriteLock则是完全的互斥.
- 不管是ReadLock还是WriteLock都支持Interrupt
- WriteLock支持Condition并且与ReentrantLock语义一致，而ReadLock则不能使用Condition，否则抛出UnsupportedOperationException异常。

### AQS

[AQS](https://zhuanlan.zhihu.com/p/27134110)
