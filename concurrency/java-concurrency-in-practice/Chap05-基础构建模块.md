## 同步容器类

包括`Vector`和`Hashtable`， 对于每个共有方法进行同步，使得每次访问只有一个线程可以访问容器的状态

## 并发容器

`BlockingQueue`：增加了可阻塞的插入和获取等操作

`CopyOnWriteArrayList`：知道正确地发布一个事实不可变的对象，那么在访问该对象时就不需要进一步同步。在每次修改时，都会创建并重新发布一个新的容器样本，从而实现可变性

容器的迭代器保留一个志向底层基础数组地引用，这个数组当前位置位于迭代器地起始位置，由于它不会被修改，因此只需要确保数组内容地可见性。**每当修改容器时都会复制底层数组，因此当容器地规模较大时，仅当迭代操作远远多于修改操作时，才用该容器**

## BlockingQueue

**LinkedBlockingQueue**
**ArrayBlockingQueue**
**PriorityBlockingQueue**
**SynchronousQueue**：不为队列中元素维护存储空间，它维护一组线程，这些线程等待把元素加入或者移出队列

## 阻塞方法与中断方法

线程阻塞或暂停执行地原因：

- 等待I/O操作结束吧
- 等待获得一个锁
- 等待从`Thread.sleep`方法中醒来
- 等待另一个线程地计算结果

*阻塞操作地线程必须等待某个不受他控制地时间发生后才能继续执行*

**处理对中断地响应：**

- 传递InterruptedException：把`InterruptedException`传递给方法地调用者
- 恢复中断：调用当前线程上地`interrupt`方法恢复中断状态

## 同步工具类

- 闭锁 CountDownLatch
- FutureTask
- 信号量
- 栅栏 CyclicBarrier

