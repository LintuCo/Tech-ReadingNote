# 显示锁

`ReentrantLock`实现了`Lock`接口，并提供了与`synchronized`相同的**互斥性**和内存可见性。**在获取`ReentrantLock`时，有着与进入同步代码块相同的内存语义，在释放`ReentrantLock`时，有着与退出同步代码块相同的内存语义**，同时提供了**可重入的加锁语义**。

```java
public class ReentrantLock implements Lock, java.io.Serializable
```

```java
public interface Lock {
        void lock();
        //在获得锁的同时保持对中断的响应
        void lockInterruptibly() throws InterruptedException;
        boolean tryLock();
        boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
        void unlock();
        Condition newCondition();
}
```

**内置锁与外置锁的区别**
- **内置锁的局限性** 无法中断一个正在等待获取锁的线程，无法在请求获取一个锁时无限等待下去。
- **内置锁的优势** 内置锁必须在获取改锁的代码块中释放，简化编码工作。在线程转储中能给出在哪些调用帧中获得哪些锁，并能检测和识别发生死锁的线程。
- **`ReentrantLock`的优势** 提供更好的活跃性或性能。
- **`ReentrantLock`的不足** 必须在`finally`中释放锁，否则在被保护的代码中抛出异常则无法释放锁。

### 轮询锁与定时锁

*避免死锁的发生*

**实现机制** 可定时的与可轮询的锁获取模式是由`tryLock`方法实现的。如果不能获取所有需要的锁，通过使用可定时或者可轮询的锁，从而重新获得控制权，释放已经获得的锁，然后重新尝试获取所有的锁（如果不能同时获取，则回退），如果在指定时间内不能获取所有需要的锁，则返回一个失败状态。

### 可中断的锁获取操作

`lockInterruptibly`

### 非块结构的加锁

通过降低锁的颗粒度来提高代码的可伸缩性。

**分段锁** 为每个链表节点使用一个独立的锁，使不同的线程能独立地对链表不同部分进行操作。**每个节点的锁将保护连接指针以及在该节点中存储的数据，遍历或者修改该链表时，必须持有该节点的锁**

## 公平性

**非公平的锁** 允许插队
**公平的锁** 按照他们发出请求的顺序来获得锁

**非公平的锁VS公平的锁**
当执行加锁操作时，公平性将由于在挂起线程和恢复线程时存在的开销降低性能

## `ReadWriteLock`

```java
public class ReentrantReadWriteLock
        implements ReadWriteLock, java.io.Serializable
```

- `ReentrantReadWriteLock`为读写锁提供了可重入的加锁语义
- `ReentrantReadWriteLock`在构造时可以选择非公平锁或者公平锁
- 写线程可以降为读线程，读线程不能升级为写线程
- 如果由读线程持有锁，而另一个线程请求写入锁，其他线程都不能获取读取锁