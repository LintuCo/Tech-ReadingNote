# 构建自定义的同步工具

### 显示的Condition对象

**内置条件队列的缺陷** - 每个内置锁只能有一个相关联的条件队列，多个线程可能在同一个条件队列上等待不同的条件谓词，并且在最常见的加锁模式下公开条件队列对象。**无法满足在使用notifyAll时所有等待线程为同一类型的需求**

**Condition和Lock关联** - 创建Condition，并且调用Lock的Lock.newCondition,**在每个锁上可存在多个等待、条件等待可以使可中断的或不可中断的、基于时限的等待，以及公平或非公平的队列操作**

*在Condition对象中，与wait、notify和notifyAll方法对应的分别是await、signal和signalAll*

### AbstractQueuedSynchronizer

**Introduction**

AQS(AbstractQueuedSynchronizer)，AQS是JDK下提供的一套用于实现基于FIFO等待队列的阻塞锁和相关的同步器的一个同步框架。这个抽象类被设计为作为一些可用原子int值来表示状态的同步器的基类。

```java
/**
 * Provides a framework for implementing blocking locks and related
 * synchronizers (semaphores, events, etc) that rely on
 * first-in-first-out (FIFO) wait queues.  This class is designed to
 * be a useful basis for most kinds of synchronizers that rely on a
 * single atomic {@code int} value to represent state. Subclasses
 * must define the protected methods that change this state, and which
 * define what that state means in terms of this object being acquired
 * or released. 
 */
```

**Usage**

```java
/**
 * To use this class as the basis of a synchronizer, redefine the
 * following methods, as applicable, by inspecting and/or modifying
 * the synchronization state using {@link #getState}, {@link
 * #setState} and/or {@link #compareAndSetState}:
 *
 * <ul>
 * <li> {@link #tryAcquire}
 * <li> {@link #tryRelease}
 * <li> {@link #tryAcquireShared}
 * <li> {@link #tryReleaseShared}
 * <li> {@link #isHeldExclusively}
 * </ul>
 */
```

基于AQS构建的有`ReentrantLock` 、`Semaphore`、 `CountDownLatch`、 `ReentrantReadWriteLock`、`Synchronousueue`和`FutureTask`

以`CountDownLatch`为例：

```java
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }

        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }

```

### Semaphore 与 CountDownLatch

**Semaphore** - 将AQS的同步状态用于保存当前可用许可的数量。
**CountDownLatch** - 调用`release`，从而导致计数值递减，当计数值为零时，接触所有等待线程的阻塞

### ReentrantReadWriteLock

`ReadWriteLock`接口表示存在两个锁：一个读取锁一个写入锁。基于AQS实现的`ReentrantReadWriteLock`，**单个AQS子类同时管理读取加锁和写入加锁**。分别使用了一个16位的状态来表示写入锁和读取锁的计数。写入锁为独占锁，读取锁为共享锁。

