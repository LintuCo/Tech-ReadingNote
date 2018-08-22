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

```java
    /**
    * Table initialization and resizing control.  When negative, the
    * table is being initialized or resized: -1 for initialization,
    * else -(1 + the number of active resizing threads).  Otherwise,
    * when table is null, holds the initial table size to use upon
    * creation, or 0 for default. After initialization, holds the
    * next element count value upon which to resize the table.
    */
    private transient volatile int sizeCtl;//控制标识符
```
- 负数代表正在进行初始化或扩容操作 ,其中-1代表正在初始化 ,-N 表示有N-1个线程正在进行扩容操作
- 正数或0代表hash表还没有被初始化，这个数值表示初始化或下一次进行扩容的大小，类似于扩容阈值。它的值始终是当前ConcurrentHashMap容量的0.75倍，这与loadfactor是对应的。实际容量>=sizeCtl，则扩容。

## Get

```java
public V get(Object key) {
    Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
    int h = spread(key.hashCode());
    if ((tab = table) != null && (n = tab.length) > 0 &&
    ////tabAt调用Unsafe.getObjectVolatile获取指定内存的数据
        (e = tabAt(tab, (n - 1) & h)) != null) {
        if ((eh = e.hash) == h) {
            if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                return e.val;
        }
        else if (eh < 0)
            return (p = e.find(h, key)) != null ? p.val : null;
        while ((e = e.next) != null) {
            if (e.hash == h &&
                ((ek = e.key) == key || (ek != null && key.equals(ek))))
                return e.val;
        }
    }
    return null;
}
```

## HashEntry

HashEntry用来封装具体的键值对，是个典型的四元组。与HashMap中的Entry类似，HashEntry也包括同样的四个域，分别是key、hash、value和next。不同的是，**在HashEntry类中，key，hash和next域都被声明为final的，value域被volatile所修饰，因此HashEntry对象几乎是不可变的，这是ConcurrentHashmap读操作并不需要加锁的一个重要原因**。

## size方法

size方法主要思路是先在没有锁的情况下对所有段大小求和，这种求和策略最多执行RETRIES_BEFORE_LOCK次(默认是两次)：在没有达到RETRIES_BEFORE_LOCK之前，求和操作会不断尝试执行（这是因为遍历过程中可能有其它线程正在对已经遍历过的段进行结构性更新）；在超过RETRIES_BEFORE_LOCK之后，如果还不成功就在持有所有段锁的情况下再对所有段大小求和。


## putVal方法

```java
   /** Implementation for put and putIfAbsent */
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) throw new NullPointerException();
        int hash = spread(key.hashCode());//计算hash值，两次hash操作
        int binCount = 0;
        for (Node<K,V>[] tab = table;;) {//类似于while(true)，死循环，直到插入成功 
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)//检查是否初始化了，如果没有，则初始化
                tab = initTable();
                /*
                    i=(n-1)&hash 等价于i=hash%n(前提是n为2的幂次方).即取出table中位置的节点用f表示。
                    有如下两种情况：
                    1、如果table[i]==null(即该位置的节点为空，没有发生碰撞)，则利用CAS操作直接存储在该位置，
                        如果CAS操作成功则退出死循环。
                    2、如果table[i]!=null(即该位置已经有其它节点，发生碰撞)
                */
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                if (casTabAt(tab, i, null,
                             new Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED)//检查table[i]的节点的hash是否等于MOVED，如果等于，则检测到正在扩容，则帮助其扩容
                tab = helpTransfer(tab, f);//帮助其扩容
            else {//运行到这里，说明table[i]的节点的hash值不等于MOVED。
                V oldVal = null;
                synchronized (f) {//锁定,（hash值相同的链表的头节点）
                    if (tabAt(tab, i) == f) {//避免多线程，需要重新检查
                        if (fh >= 0) {//链表节点
                            binCount = 1;
                            /*
                            下面的代码就是先查找链表中是否出现了此key，如果出现，则更新value，并跳出循环，
                            否则将节点加入到里阿尼报末尾并跳出循环
                            */
                            for (Node<K,V> e = f;; ++binCount) {
                                K ek;
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)//仅putIfAbsent()方法中onlyIfAbsent为true
                                        e.val = value;//putIfAbsent()包含key则返回get，否则put并返回  
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {//插入到链表末尾并跳出循环
                                    pred.next = new Node<K,V>(hash, key,
                                                              value, null);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) { //树节点，
                            Node<K,V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                           value)) != null) {//插入到树中
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                //插入成功后，如果插入的是链表节点，则要判断下该桶位是否要转化为树
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)//实则是>8,执行else,说明该桶位本就有Node
                        treeifyBin(tab, i);//若length<64,直接tryPresize,两倍table.length;不转树 
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        addCount(1L, binCount);
        return null;
    }
```


1. 不允许有nullkey和nullvalue。
2. 只有在第一次put的时候才初始化table。初始化有并发控制。
3. 当hash对应的下标是null时，使用CAS插入元素。
4. 调用addCount方法，对size加一，并判断是否需要扩容。
5. 如果 hash 冲突了，同步头节点，进行链表操作，如果链表长度达到 8 ，分成红黑树。
6. Cmap 的并发性能是 hashTable 的 table.length 倍。只有出现链表才会同步，否则使用 CAS 插入。性能极高

## cas操作

```java
    /*
        3个用的比较多的CAS操作
    */

    @SuppressWarnings("unchecked") // ASHIFT等均为private static final  
    static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int i) { // 获取索引i处Node  
        return (Node<K,V>)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);  
    }  
    // 利用CAS算法设置i位置上的Node节点（将c和table[i]比较，相同则插入v）。  
    static final <K,V> boolean casTabAt(Node<K,V>[] tab, int i,  
                                        Node<K,V> c, Node<K,V> v) {  
        return U.compareAndSwapObject(tab, ((long)i << ASHIFT) + ABASE, c, v);  
    }  
    // 设置节点位置的值，仅在上锁区被调用  
    static final <K,V> void setTabAt(Node<K,V>[] tab, int i, Node<K,V> v) {  
        U.putObjectVolatile(tab, ((long)i << ASHIFT) + ABASE, v);  
    }
```

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

helpTransfer

```java
    /*
     * Helps transfer if a resize is in progress.
     *在多线程情况下，如果发现其它线程正在扩容，则帮助转移元素。
     （只有这种情况会被调用）从某种程度上说，其“优先级”很高，只要检测到扩容，就会放下其他工作，先扩容。
     */
    final Node<K,V>[] helpTransfer(Node<K,V>[] tab, Node<K,V> f) {// 调用之前，nextTable一定已存在。
        Node<K,V>[] nextTab; int sc;
        if (tab != null && (f instanceof ForwardingNode) &&
            (nextTab = ((ForwardingNode<K,V>)f).nextTable) != null) {
            int rs = resizeStamp(tab.length);//标志位
            while (nextTab == nextTable && table == tab &&
                   (sc = sizeCtl) < 0) {
                if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                    sc == rs + MAX_RESIZERS || transferIndex <= 0)
                    break;
                if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                    transfer(tab, nextTab);//调用扩容方法，直接进入复制阶段  
                    break;
                }
            }
            return nextTab;
        }
        return table;
    }
```