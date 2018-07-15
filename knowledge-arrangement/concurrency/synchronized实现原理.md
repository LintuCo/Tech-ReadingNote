# synchronized实现原理


- [synchronized实现原理](#synchronized%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86)
    - [synchronized应用](#synchronized%E5%BA%94%E7%94%A8)
        - [修饰实例方法](#%E4%BF%AE%E9%A5%B0%E5%AE%9E%E4%BE%8B%E6%96%B9%E6%B3%95)
        - [synchronized作用于静态方法](#synchronized%E4%BD%9C%E7%94%A8%E4%BA%8E%E9%9D%99%E6%80%81%E6%96%B9%E6%B3%95)
        - [synchronized同步代码块](#synchronized%E5%90%8C%E6%AD%A5%E4%BB%A3%E7%A0%81%E5%9D%97)
    - [synchronized底层语义原理](#synchronized%E5%BA%95%E5%B1%82%E8%AF%AD%E4%B9%89%E5%8E%9F%E7%90%86)
        - [synchronized方法底层原理](#synchronized%E6%96%B9%E6%B3%95%E5%BA%95%E5%B1%82%E5%8E%9F%E7%90%86)
    - [Java虚拟机对synchronized的优化](#java%E8%99%9A%E6%8B%9F%E6%9C%BA%E5%AF%B9synchronized%E7%9A%84%E4%BC%98%E5%8C%96)
        - [偏向锁](#%E5%81%8F%E5%90%91%E9%94%81)
        - [轻量级锁](#%E8%BD%BB%E9%87%8F%E7%BA%A7%E9%94%81)
        - [自旋锁与自适应自旋](#%E8%87%AA%E6%97%8B%E9%94%81%E4%B8%8E%E8%87%AA%E9%80%82%E5%BA%94%E8%87%AA%E6%97%8B)
        - [锁消除](#%E9%94%81%E6%B6%88%E9%99%A4)

## synchronized应用

### 修饰实例方法

*作用于当前实例加锁，进入同步代码前要获得当前实例的锁*

```java
/**
* synchronized 修饰实例方法
*/
public synchronized void increase(){
    i++;
}
```

### synchronized作用于静态方法

*作用于当前类对象加锁，进入同步代码前要获得当前类对象的锁*

当synchronized作用于静态方法时，其锁就是当前类的class对象锁。由于静态成员不专属于任何一个实例对象，是类成员，因此通过class对象锁可以控制静态成员的并发操作，访问非静态 synchronized 方法占用的锁是当前实例对象锁。

因此如果一个线程`A`调用一个实例对象的非`static synchronized`方法，而线程B需要调用这个实例对象所属类的静态`synchronized`方法，是允许的，不会发生互斥现象。

### synchronized同步代码块

```java
//this,当前实例对象锁
synchronized(this){
    for(int j=0;j<1000000;j++){
        i++;
    }
}

//class对象锁
synchronized(AccountingSync.class){
    for(int j=0;j<1000000;j++){
        i++;
    }
}
```

## synchronized底层语义原理

Java 虚拟机中的同步(Synchronization)基于进入和退出管程(Monitor)对象实现， 无论是显式同步(有明确的 monitorenter 和 monitorexit 指令,即同步代码块)还是隐式同步都是如此。

同步方法由方法调用指令读取运行时常量池中方法的 ACC_SYNCHRONIZED 标志来隐式实现的。

### synchronized方法底层原理

方法级的同步是隐式，即无需通过字节码指令来控制的，它实现在方法调用和返回操作之中。JVM可以从方法常量池中的方法表结构(method_info Structure) 中的 ACC_SYNCHRONIZED 访问标志区分一个方法是否同步方法。当方法调用时，调用指令将会 检查方法的 ACC_SYNCHRONIZED 访问标志是否被设置，如果设置了，执行线程将先持有monitor（虚拟机规范中用的是管程一词）， 然后再执行方法，最后再方法完成(无论是正常完成还是非正常完成)时释放monitor。

## Java虚拟机对synchronized的优化

锁的四种状态：无状态锁<偏向锁<轻量级锁<重量级锁

### 偏向锁

**核心思想** 当锁对象第一次被线程获取的时候，虚拟机将会把对象头的标志位设置为"01"，偏向模式。同时使用`CAS`操作把获取这个锁的线程的ID记录到对象的`Mark Work`之中，如果`CAS`操作成功，持有偏向锁的线程以后每次进入这个锁相关的同步块时，虚拟机不再进行任何同步操作。直到另外一个线程尝试获取这个锁。

![偏向锁轻量级锁转化](http://p82ueiq23.bkt.clouddn.com/%E5%81%8F%E5%90%91%E9%94%81%E8%BD%BB%E9%87%8F%E7%BA%A7%E9%94%81%E8%BD%AC%E5%8C%96.png "偏向锁、轻量级锁的状态转化")

### 轻量级锁

**加锁过程** 当偏向锁失败时，转化为轻量级锁，虚拟机检查对象的`Mark Word`是否指向当前线程的栈帧，如果当前线程已经拥有这个对象的锁，则可以直接进入同步块继续执行，否则说明这个锁的对象已经被其他线程抢占，轻量级锁就需要膨胀为重量级锁。

### 自旋锁与自适应自旋

**自旋锁** 虽然避免了线程切换的开销，但是占用了处理器时间。

**自适应自旋** 自旋的时间由前一次在同一个锁上的自旋时间及锁的拥有者的状态决定

### 锁消除

*虚拟机即时编译器在运行时，对一些代码上要求同步，但是被检测不可能存在共享数据竞争的锁进行消除，消除依据来源于逃逸分析的数据支持*