# AbstractQueuedSynchronizer的介绍和原理分析

```java
/**
 * Provides a framework for implementing blocking locks and related
 * synchronizers that rely on
 * first-in-first-out (FIFO) wait queues.  This class is designed to
 * be a useful basis for most kinds of synchronizers that rely on a
 * single atomic {@code int} value to represent state. Subclasses
 * must define the protected methods that change this state, and which
 * define what that state means in terms of this object being acquired
 * or released.  Given these, the other methods in this class carry
 * out all queuing and blocking mechanics. Subclasses can maintain
 * other state fields, but only the atomically updated {@code int}
 * value manipulated using methods {@link #getState}, {@link
 * #setState} and {@link #compareAndSetState} is tracked with respect
 * to synchronization.
 **/
```

同步器面向的是线程访问和资源控制，它定义了线程对资源是否能够获取以及线程的排队等操作。

## Node

同步器的开始提到了其实现依赖于一个FIFO队列，那么队列中的元素Node就是保存着线程引用和线程状态的容器，每个线程对同步器的访问，都可以看做是队列中的一个节点。Node的主要包含以下成员变量：

```java
Node {
    /**
     * <pre>
     *      +------+  prev +-----+       +-----+
     * head |      | <---- |     | <---- |     |  tail
     *      +------+       +-----+       +-----+
     * </pre>
     * **/

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
    int waitStatus;
    Node prev;
    Node next;
    //存储条件队列的后继队列
    Node nextWaiter;
    Thread thread;
}

```

## acquire

以排他的方式获取锁

```java
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```

上述逻辑包括：

1. 尝试获取（调用tryAcquire更改状态，需要保证原子性）；
2. 如果获取不到，将当前线程构造成节点Node并加入sync队列；
3. 再次尝试获取，如果没有获取到那么将当前线程从线程调度器上摘下，进入等待状态。

```java
private Node addWaiter(Node mode) {
    Node node = new Node(Thread.currentThread(), mode);
    // 快速尝试在尾部添加
    Node pred = tail;
    if (pred != null) {
        node.prev = pred;
        if (compareAndSetTail(pred, node)) {
            pred.next = node;
            return node;
        }
    }
    enq(node);
    return node;
}
//如果队尾添加失败或者第一个入队的节点
private Node enq(final Node node) {
    for (;;) {
        Node t = tail;
        if (t == null) { // Must initialize
            if (compareAndSetHead(new Node()))
                tail = head;
        } else {
            node.prev = t;
            if (compareAndSetTail(t, node)) {
            t.next = node;
            return t;
        }
    }
}
```

```java
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            //获前驱节点，头节点占有锁并且正在运行
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
                failed = false;
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                interrupted = true;
                }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```
上述逻辑主要包括：
1. 获取当前节点的前驱节点；需要获取当前节点的前驱节点，而 **头结点所对应的含义是当前站有锁且正在运行**。
2. 当前驱节点是头结点并且能够获取状态，代表该当前节点占有锁；如果满足上述条件，那么代表能够占有锁，根据节点对锁占有的含义，设置头结点为当前节点。
3. 否则进入等待状态。如果没有轮到当前节点运行，那么将当前线程从线程调度器上摘下，也就是进入等待状态。

这里针对acquire做一下总结：

1. 状态的维护；
需要在锁定时，需要维护一个状态(int类型)，而对状态的操作是原子和非阻塞的，通过同步器提供的对状态访问的方法对状态进行操纵，并且利用compareAndSet来确保原子性的修改。

2. 状态的获取；
一旦成功的修改了状态，当前线程或者说节点，就被设置为头节点。

3. sync队列的维护。

<div align=center>

![AQS获取锁](http://p82ueiq23.bkt.clouddn.com/AQS%E8%8E%B7%E5%8F%96%E9%94%81.png)
</div>

## release

release则表示将状态设置回去，也就是将资源释放，或者说将锁释放。

```java
public final boolean release(int arg) {
    if (tryRelease(arg)) {
        Node h = head;
        if (h != null &amp;&amp; h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```
上述逻辑主要包括：

1. 尝试释放状态；
tryRelease能够保证原子化的将状态设置回去，当然需要使用compareAndSet来保证。如果释放状态成功过之后，将会进入后继节点的唤醒过程。

2. 唤醒当前节点的后继节点所包含的线程。
通过LockSupport的unpark方法将休眠中的线程唤醒，让其继续acquire状态。

## acquireInterruptibly

```java
public final void acquireInterruptibly(int arg)
    throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    if (!tryAcquire(arg))
        doAcquireInterruptibly(arg);
}

private void doAcquireInterruptibly(int arg)
    throws InterruptedException {
    final Node node = addWaiter(Node.EXCLUSIVE);
    boolean failed = true;
    try {
        for (;;) {
            final Node p = node.predecessor();
            if (p == head &amp;&amp; tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
                failed = false;
                return;
            }
            // 检测中断标志位
            if (shouldParkAfterFailedAcquire(p, node) &amp;&amp;
            parkAndCheckInterrupt())
                throw new InterruptedException();
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```

在每次被唤醒时，进行中断检测，如果发现当前线程被中断，那么抛出InterruptedException并退出循环。

## doAcquireNanos

如果在指定的nanosTimeout内没有获取到状态，那么返回false，反之返回true。可以将该方法看做acquireInterruptibly的升级版，也就是在判断是否被
中断的基础上增加了超时控制。

<dive align=center>

![AQSNanos](http://p82ueiq23.bkt.clouddn.com/AQSNanos.png)
</dive>


[1]:https://segmentfault.com/a/1190000000372011 "AbstractQueuedSynchronizer的介绍和原理分析"