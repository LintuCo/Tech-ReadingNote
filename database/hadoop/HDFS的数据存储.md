## HDFS内存存储

用机器的内存作为存储数据的载体。LAZY_PERSIST直接作为数据存放的载体

### HDFS内存存储原理

HDFS内存存储对于内存的特性产生的问题：HDFS的内存过小，可以存储的数据有限，并且在服务停止的时候，数据会全部丢失。而如果在结束之前将数据做持久化操作，当数据过多时，数据持久化的速度会比较慢。

**解决方案**：

异步持久化：在内存存储新的数据时，持久化距离当前时刻最远的数据。

**异步存储的步骤**

1. 对目标文件目录设置`StoragePolicy`为`LAZY_PERSIST`的内存存储策略。
2. 客户端进程向`NameNode`发起创建/写文件的请求
3. 哭护短请求到具体的`DateNode`后，`DateNode`会吧这些数据块写入`RAM`内存中，同时启动异步线程服务将内存数据持久化到磁盘上

### Linux虚拟内存盘

将内存模拟为一个块盘的技术

### HDFS的内存存储流程分析

#### Lazy_PERSIST内存存储

**FsDatasetImpl**：管理DataNode所有磁盘读写的管家
**RamDiskAsyncLazyPersistService**：异步持久化线程服务，针对每一个磁盘块设置一个对应的线程池。需要持久化到给定磁盘的数据块会被提交到对应的线程池
**LazyWriter**：线程服务，不断从数据块列表中取出数据块，将数据块加入到异步持久化线程池`RamDiskAsyncLazyPersistService`中去执行
**RanDiskReplicaLruTracker**：副本块跟踪类，维护已持久化、未持久化的副本以及总副本数据信息

#### RanDiskReplicaLruTracker

维护多个关系的数据块信息（blockpool ID对副本信息的映射图、待写入磁盘的副本队列、已持久化写入磁盘的映射图）

**异步持久化操作相关方法**

**异步持久化操作无直接关联方法**

1. discardReplica：当检测到不再需要某副本的时候，可以从内存中移除、撤销副本
2. touch：访问了一次某特定的副本块，并会更新副本块的`lastUsedTime`
3. getNextCandidateForEviction：`DataNode`内存空间不足的时候，需要内存额预留空间给新的副本块时被调用

### 异构存储

**HDFS异构存储的总结**：
- DataNode 通过心跳汇报自身数据存储目录的`StorageType`给`NameNode`
- `NameNode`进行汇总并更新集群内各个节点的存储情况
- 待复制文件根据自身设定的存储策略信息向`NameNode`请求拥有此类型存储介质的`DataNode`作为候选节点

## HDFS的数据管理与策略选择

### HDFS缓存与缓存块

HDFS的缓存块由普通的文件快转化而来，提高用户读取文件的速度。HDFS缓存在`DataNode`内存中，不需要进行读取操作

**相关问题**
1. 物理层面缓存块
2. 缓存块的生命周期状态
3. 触发和取消缓存块的情况
4. CacheBlock、UnCacheBlock缓存块的确定
5. 缓存块列表的更新
6. 缓存块的使用

#### HDFS物理层面缓存块

利用`mmap`、`mlock`系统调用将文件或者其他对象映射到内存中，以此达到在`NameNode`上缓存数据的效果。

#### 缓存块的生命周期状态

- CACHING：表示块正在被使用
- CACHING_CANCELLED：正在被缓存的块处于被取消的状态
- CACHED：数据块已被缓存
- UNCACHING：缓存块处于被取消缓存的状态

#### CacheBlock、UnCacheBlock场景触发

- 当块执行append写操作时
- 当把块处理为无效快的时候
- 上层`NameNode`发送`uncache`回复命令时

### HDFS中心缓存管理

HDFS中心缓存管理机制主要依赖于中心缓存管理器以及缓存块监控服务

#### HDFS缓存适用场景

缓存HDFS中的热点公共资源文件和短期临时的热点数据文件

#### HDFS缓存的结构设计

**CacheDirective**

缓存的基本单元，属于对应的缓存池

**CachePool**

缓存池中维护一个缓存单元列表，缓存池被`CacheManager`所掌控

缓存副本监控器：周期性的扫描当前最新的缓存路径，并分发缓存块到对应的`DataNode`节点上

![HDFS缓存总结构关系](https://www.google.com/url?sa=i&source=images&cd=&cad=rja&uact=8&ved=2ahUKEwiwzrifsOvcAhUENo8KHQfvA7sQjRx6BAgBEAU&url=http%3A%2F%2Fm.itboth.com%2Fd%2FURnYJr%2Fhdfs-cache&psig=AOvVaw3CMYpMdKtGOOI2EYEmOOSD&ust=1534296018065744)

#### HDFS缓存管理机制



### HDFS快照管理

#### 快照概念

快照不是数据的简单拷贝，而只是做差异的记录

**快照目录**：一个快照目录下可以有多个快照文件，快照目录可以创建、删除自身目录下的快照文件，同时快照目录本身被快照目录管理器管理

#### HDFS内部的快照管理机制

1. 快照的关系结构
   
快照管理器管理多个快照目录
一个快照目录拥有多个快照文件

2. 快照调用流程

- 上游请求的接收
- 下游请求的处理

3. 快照原理实现

创建快照之前需要由快照目录，并且不允许创建出网状关系的快照目录。

**快照如何完全一致的反映出那一时刻的文件目录信息**

- HDFS中只为每个快照保存相对当时快照创建时间点发生过变更的INode信息
- 获取快照信息时，根据快照Id和当前没有发生过变更的INode信息，进行对应的恢复

#### HDFS的快照使用

1. 丢失数据的恢复
2. 元数据的差异比较

### HDFS副本放置策略

副本的最终存放，提高读写能力

`BlockPlacementPolicyDefault`：
1. 如果请求方所在机器是一个`DataNode`。则直接存放在本地，否则随机在集群中选择一个`DataNode`
2. 第二个副本存放在不同于第一个副本所在的机架
3. 第三个副本存放在第二个副本所在的机架，但是属于不同的节点

#### 策略核心方法chooseTargets

```java
  public DatanodeStorageInfo[] chooseTarget(String srcPath,
                                    int numOfReplicas,
                                    Node writer,
                                    List<DatanodeStorageInfo> chosenNodes,
                                    boolean returnChosenNodes,
                                    Set<Node> excludedNodes,
                                    long blocksize,
                                    final BlockStoragePolicy storagePolicy,
                                    EnumSet<AddBlockFlag> flags) {
    return chooseTarget(numOfReplicas, writer, chosenNodes, returnChosenNodes,
        excludedNodes, blocksize, storagePolicy, flags);
  }
```

在`chooseTarget`传入偏爱的节点参数会使方法选择节点的时候优选偏爱节点参数的节点；

- 初始化操作
- 选择目标节点
- 对目标节点列表进行排序，形成Pipeline

### HDFS内部的认证机制

### HDFS内部的磁盘目录服务

## HDFS的流量处理

HDFS的内部限流、HDFS的Balancer数据平衡、DiskBalancer磁盘间数据平衡

### HDFS的内部限流

#### 数据的限流

- Balancer数据平衡数据流传输
- Fsimage镜像文件的上传下载数据流传输
- VolumeScanner磁盘扫描时的数据流传输

#### DataTransferThrottle 限流原理

通过传入指定的带宽速率作为最大的限制值


### 数据平衡

造成不同节点间的负载不均衡， `Balancer`的作用是将数据块从高数据使用量节点移动到低数据使用量节点，从而达到数据平衡的效果

#### 数据不平衡现象

- 哭护短长期写文件数据导致不均等的现象，部分机器写的数据偏大、部分偏小
- 新节点上线

#### Balancer性能优化

- 大数据快在相同时间段内数据平衡效率高于小数据块，因此可以在Balancer平衡程序中加入待移动块最小字节大小限制的参数，过滤掉小数据块

- 加大数据平衡的带宽
- 指定目标节点进行数据平衡


### HDFS节点内数据平衡

