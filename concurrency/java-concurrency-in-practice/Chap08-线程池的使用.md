## 线程池的使用

### 配置ThreadPoolExecutor
```java
    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default rejected execution handler.
     *
     * @param corePoolSize the number of threads to keep in the pool, even
     *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *        pool
     * @param keepAliveTime when the number of threads is greater than
     *        the core, this is the maximum time that excess idle threads
     *        will wait for new tasks before terminating.
     * @param unit the time unit for the {@code keepAliveTime} argument
     * @param workQueue the queue to use for holding tasks before they are
     *        executed.  This queue will hold only the {@code Runnable}
     *        tasks submitted by the {@code execute} method.
     * @param threadFactory the factory to use when the executor
     *        creates a new thread
     * @throws IllegalArgumentException if one of the following holds:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException if {@code workQueue}
     *         or {@code threadFactory} is null
     */
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory) {
    }
```

**newFixedThreadPool** - 创建一个固定长度的线程池，每当提交一个任务时，创建一个线程，知道达到线程池的最大数量。

**newCachedThreadPool** - 如果线程池当前规模超过了处理需求时，将回收空闲的线程，当需求增加时，可以添加新的线程，线程池的规模不受限制。

**newSingleThreadExecutor** - 单线程Executor

**newScheduledThreadPool** - 固定长度的线性池，以延时或者定时的方式来执行任务。

### 饱和策略

**方法** - 通过`setRejectedExecutionHandler`来修改

**饱和策略**
- `AbortPolicy` - 默认的饱和策略，抛出为检查的`RejectedExecutionException`
- 调用者运行 - 将任务回退到调用者
- `DiscardPolicy` - 悄悄抛弃任务
- `DiscardOldestPolicy` - 抛弃下一个将被执行的任务

### 线程工厂

```java
public interface ThreadFactory {

    /**
     * Constructs a new {@code Thread}.  Implementations may also initialize
     * priority, name, daemon status, {@code ThreadGroup}, etc.
     *
     * @param r a runnable to be executed by new thread instance
     * @return constructed thread, or {@code null} if the request to
     *         create a thread is rejected
     */
    Thread newThread(Runnable r);
}
```
