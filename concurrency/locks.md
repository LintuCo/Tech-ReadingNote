
## AQS

### Unsafe

### LockSupport

LockSupport是通过调用Unsafe函数中的接口实现阻塞和解除阻塞的。

```java
// 返回提供给最近一次尚未解除阻塞的 park 方法调用的 blocker 对象，如果该调用不受阻塞，则返回 null。
static Object getBlocker(Thread t)
// 为了线程调度，禁用当前线程，除非许可可用。
static void park()
// 为了线程调度，在许可可用之前禁用当前线程。
static void park(Object blocker)
// 为了线程调度禁用当前线程，最多等待指定的等待时间，除非许可可用。
static void parkNanos(long nanos)
// 为了线程调度，在许可可用前禁用当前线程，并最多等待指定的等待时间。
static void parkNanos(Object blocker, long nanos)
// 为了线程调度，在指定的时限前禁用当前线程，除非许可可用。
static void parkUntil(long deadline)
// 为了线程调度，在指定的时限前禁用当前线程，除非许可可用。
static void parkUntil(Object blocker, long deadline)
// 如果给定线程的许可尚不可用，则使其可用。
static void unpark(Thread thread)
```

*park和wait的区别。wait让线程阻塞前，必须通过synchronized获取同步锁。*

### AbstrackQueueSynchronized

提供了一个基于FIFO队列，可以用于构建锁或者其他相关同步装置的基础框架。该同步器（以下简称同步器）利用了一个int来表示状态，期望它能够成为实现大部分同步需求的基础。使用的方法是继承，子类通过继承同步器并需要实现它的方法来管理其状态，管理的方式就是通过类似acquire和release的方式来操纵状态。然而多线程环境中对状态的操纵必须确保原子性，因此子类对于状态的把握，需要使用这个同步器提供的以下三个方法对状态进行操作：

```java
java.util.concurrent.locks.AbstractQueuedSynchronizer.getState()
java.util.concurrent.locks.AbstractQueuedSynchronizer.setState(int)
java.util.concurrent.locks.AbstractQueuedSynchronizer.compareAndSetState(int, int)
```

队列中的节点的定义：

```java
    /** waitStatus value to indicate thread has cancelled */
    static final int CANCELLED =  1;
    /** waitStatus value to indicate successor's thread needs unparking */
    static final int SIGNAL    = -1;
    /** waitStatus value to indicate thread is waiting on condition */
    static final int CONDITION = -2;
    /**
        * waitStatus value to indicate the next acquireShared should
        * unconditionally propagate
        */
    static final int PROPAGATE = -3;

    Node {
        int waitStatus;
        Node prev;//前驱节点，比如当前节点被取消，那就需要前驱节点和后继节点来完成连接。
        Node next;//后继节点
        Node nextWaiter;//存储condition队列中的后继节点
        Thread thread;//当前线程
    }
```

节点成为`sync队列`和`condition队列`构建的基础，在同步器中就包含了sync队列。同步器拥有三个成员变量：sync队列的头结点`head`、sync队列的尾节点`tail`和状态`state`。对于锁的获取，请求形成节点，将其挂载在尾部，而锁资源的转移（释放再获取）是从头部开始向后进行。对于同步器维护的状态state，多个线程对其的获取将会产生一个链式的结构。

### api说明

```java
/**
 *排它的获取这个状态。这个方法的实现需要查询当前状态是否
 *允许获取，然后再进行获取（使用compareAndSetState来做）状态。
 */
protected boolean tryAcquire(int arg)
//释放状态
protected boolean tryRelease(int arg)
//共享模式下获取状态
protected int tryAcquireShared(int arg)
//共享模式下释放状态
protected boolean tryReleaseShared(int arg)	
//在排它模式下，状态是否被占用。
protected boolean isHeldExclusively()	
```

### ConditionObject

AQS还提供了一个ConditionObject类来表示监视器风格的等待通知操作，主要用在Lock中，和传统的监视器的区别在于一个Lock可以创建多个Condition。ConditionObject使用相同的内部队列节点，但是维护在一个单独的条件队列中，当收到signal操作的时候将条件队列的节点转移到等待队列。

## Lock

```java
public interface Lock {
    void lock();
    /**
     * Acquires the lock unless the current thread is
     * {@linkplain Thread#interrupt interrupted}.
     *
     * <p>Acquires the lock if it is available and returns immediately.
     *
     * <p>If the lock is not available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until
     * one of two things happens:
     *
     * <ul>
     * <li>The lock is acquired by the current thread; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of lock acquisition is supported.
     * </ul>
     */
    void lockInterruptibly() throws InterruptedException;
    boolean tryLock();
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
    void unlock();
    Condition newCondition();
}
```

抽象类 **AbstractOwnableSynchronizer** 该类是主要定义让线程以独占方式拥有同步器，此类为创建锁和相关同步器提供了基础，类本身不管理或使用此信息 很简单的两个方法 **setExclusiveOwnerThread**(Thread t)设置当前拥有独占访问的线程 和 **getExclusiveOwnerThread()** 返回由 setExclusiveOwnerThread最后设置的线程；如果从未设置，则返回 null。

![locks](http://p82ueiq23.bkt.clouddn.com/locks.jpg)