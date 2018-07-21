## 取消与关闭

### 任务取消

#### 中断

**线程终端**：
```java
public class Thread{
    public void interrupt(){...}
    public boolean isInterrupted(){...}
    public static boolean interrupted(){...}
}
```

**interrupt** - 中断目标线程
调用中断操作不意味着立即停止目标线程正在进行的工作，而是传递请求中断的消息，由线程在下一个合适的时刻中断自己（取消点）
**isInterrupted** - 返回目标线程的中断状态
**interrupted** - 清除当前线程的中断状态

#### 中断策略

某种形式的线程级取消操作或服务级取消操作：尽快退出，在必要时清理，通知某个所有者该线程已经退出。

#### 响应中断

**传递异常** - （可能在执行某个特定任务的清除工作之后），从而使方法成为可中断的阻塞方法。
**恢复中断状态** - 使调用栈中的上层代码能够对其进行处理

#### 通过Future来实现取消
#### 处理不可中断的阻塞
#### 采用newTaskFor来封装非标准的取消
### 停止基于线程的服务
### 处理非正常的线程终止
### JVM关闭

**关闭钩子** - 通过`Runtime.addShutdownHook`注册的但尚未开始的线程，**可以用于实现服务或应用程序的清理工作**
JVM不能保证关闭钩子的调用顺序，如果在关闭应用程序线程时，如果有（守护或非守护）线程在运行，则这些线程将和关闭进程并发执行。当所有的关闭钩子都执行结束时，JVM再运行终结器，然后再停止。**如果关闭钩子或者终结器没有运行完成，那么正常关闭进程挂起，并且JVM被强制关闭。**
```java
public void start(){
    Runtime.getRuntime().addShutdownHook(new Thread(){
        public void run(){
            try{LogService.this.stop();}
            catch(InterruptedException ignore){}
        }
    });
}
```

**守护线程** - 执行一些辅助工作，但是不阻碍JVM关闭的线程(Daemon Thread)。

**终结器** - 在回收器释放系统资源时，调用他们的`finalize`方法，保证持久化的资源被释放。