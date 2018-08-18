# 任务执行

## Executor框架

```java
public interface Executor{
    void execute（Runnable command）
}
```
Executor基于生产者消费者模式

## Executor的生命周期

Executor扩展了ExecutorService接口，添加了一些生命周期管理的方法：
- **shutdown** 执行平缓的关闭过程，不再接受新的任务，同时等待已经提交的任务执行完成。
- **shutdownNow** 尝试执行所有运行中的任务，并且不再启动队列中尚未开始执行的任务。
- **awaitTermination** 等待ExecutorService到达终止状态

![Executor类图](http://p82ueiq23.bkt.clouddn.com/TOv1IaD138RtSuhGrIkLNi0YtRWJ5_PvW7GcQc0cWPbCdw2Un5Eu5hoDU0q7xHIqkWdX-_8dMHV3R3L5Dx95WjdvFVbgcZzUNj-VtsQEnIZVe2VevW-qeRnZJyvr0Al7vV9O7oVXmIrWfno0lWWDLrK4jv70QqeeONxgdlu_UOYNksf19ndQ_08.png)

## 延迟任务与周期任务

**Time类的缺陷**：在执行所有定时任务时只会创建一个线程，如果某个任务时间过长，或破坏其他定时的准确性；Timer线程无法捕获未检查的异常，将中止线程。

**ScheduledThreadPoolExecutor**能够处理表现出错误行为的任务， DelayQueue中返回的对象将根据他们的延迟时间进行排序



## 找出可利用的并行性

### 携带结果的任务Callable与Future

**Future** 一个任务的生命周期，提供了相应的方法来判断是否已经完成或取消，以及获取任务的结果和取消任务