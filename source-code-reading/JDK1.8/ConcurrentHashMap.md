# 前言

 The primary design goal of this hash table is to maintain concurrent readability (typically method get(), but also iterators and related methods) while minimizing update contention. Secondary goals are to keep space consumption about the same or better than `java.util.HashMap`, and to support high initial insertion rates on an empty table by many threads.

 ConcurrentHashMap本质上是一个Segment数组，而一个Segment实例又包含若干个桶，每个桶中都包含一条由若干个 HashEntry 对象链接起来的链表。ConcurrentHashMap的高效并发机制是通过以下三方面来保证的(具体细节见后文阐述)：
 - 通过锁分段技术保证并发环境下的写操作；
 - 通过 HashEntry的不变性、Volatile变量的内存可见性和加锁重读机制保证高效、安全的读操作；
 - 通过不加锁和加锁两种方案控制跨段操作的的安全性。
<div align=center>

![ConcurrentHashMap](http://p82ueiq23.bkt.clouddn.com/ConcurrentHashMap.jpg)
</div>

## 常量

```java
    //table的最大容量
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    //table数量的最大值
    private static final int DEFAULT_CAPACITY = 16;
    //数组可能最大值，需要与toArray（）相关方法关联
    static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private static final float LOAD_FACTOR = 0.75f;
    static final int TREEIFY_THRESHOLD = 8;
    static final int UNTREEIFY_THRESHOLD = 6;
    private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;
    private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;
```

## HashEntry

HashEntry用来封装具体的键值对，是个典型的四元组。与HashMap中的Entry类似，HashEntry也包括同样的四个域，分别是key、hash、value和next。不同的是，**在HashEntry类中，key，hash和next域都被声明为final的，value域被volatile所修饰，因此HashEntry对象几乎是不可变的，这是ConcurrentHashmap读操作并不需要加锁的一个重要原因**。

## size方法

size方法主要思路是先在没有锁的情况下对所有段大小求和，这种求和策略最多执行RETRIES_BEFORE_LOCK次(默认是两次)：在没有达到RETRIES_BEFORE_LOCK之前，求和操作会不断尝试执行（这是因为遍历过程中可能有其它线程正在对已经遍历过的段进行结构性更新）；在超过RETRIES_BEFORE_LOCK之后，如果还不成功就在持有所有段锁的情况下再对所有段大小求和。


## putVal方法

1. 不允许有nullkey和nullvalue。
2. 只有在第一次put的时候才初始化table。初始化有并发控制。
3. 当hash对应的下标是null时，使用CAS插入元素。
4. 调用addCount方法，对size加一，并判断是否需要扩容。
5. 如果 hash 冲突了，同步头节点，进行链表操作，如果链表长度达到 8 ，分成红黑树。
6. Cmap 的并发性能是 hashTable 的 table.length 倍。只有出现链表才会同步，否则使用 CAS 插入。性能极高

## transfer 方法总结

1. 该方法会根据 CPU 核心数平均分配给每个 CPU 相同数量的桶。但如果不够 16 个，默认就是 16 个。
2. 扩容是按照 2 倍进行扩容。
3. 每个线程在处理完自己领取的区间后，还可以继续领取，如果有的话。这个是 transferIndex 变量递减 16 实现的。
4. 每次处理空桶的时候，会插入一个 forward 节点，告诉 putVal 的线程：“我正在扩容，快来帮忙”。但如果只有 16 个桶，只能有一个线程扩容。
5. 如果有了占位符，那就不处理，跳过这个桶。
6. 如果有真正的实际值，那就同步头节点，防止 putVal 那里并发。
7. 同步块里会将链表拆成两份，根据 hash & length 得到是否是 0，如果是0，放在低位，反之，反之放在 length + i 的高位。这里的设计是为了防止下次取值的时候，hash 不到正确的位置。
8. 如果该桶的类型是红黑树，也会拆成 2 个，这是必须的。然后判断拆分过的桶的大小是否小于等于 6，如果是，改成链表。
9. 线程处理完之后，如果没有可选区间，且任务没有完成，就会将整个表检查一遍，防止遗漏。