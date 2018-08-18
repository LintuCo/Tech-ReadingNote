## 前言

## 分词器

## Lucene加快查询的机制

## 如何使用Lucene构建分布式索引

## 副本的作用

副本是一个分片的精确复制，每个分片可以有零个或多个副本。ES中可以有许多相同的分片，其中之一被选择更改索引操作，这种特殊的分片称为主分片。 当主分片丢失时，如：该分片所在的数据不可用时，集群将副本提升为新的主分片。副本提高了查询吞吐量与实现高可用

## 2TB的数据的集群规划

## master节点的选举算法

master节点的职责主要包括集群、节点和索引的管理，不负责文档级别的管理。

Elasticsearch的选主是ZenDiscovery模块负责的，主要包含Ping（节点之间通过这个RPC来发现彼此）和Unicast（单播模块包含一个主机列表以控制哪些节点需要ping通）这两部分；

- 对所有可以成为master的节点（node.master: true）根据nodeId字典排序，每次选举每个节点都把自己所知道节点排一次序，然后选出第一个（第0位）节点，暂且认为它是master节点。
  
- 如果对某个节点的投票数达到一定的值（可以成为master节点数n/2+1）并且该节点自己也选举自己，那这个节点就是master。否则重新选举一直到满足上述条件。

**脑裂**

- 当集群master候选数量不小于3个时，可以通过设置最少投票通过数量，超过所有候选节点一半以上来解决脑裂问题；
- 当候选数量为两个时，只能修改为唯一的一个master候选，其他作为data节点，避免脑裂问题。

## Elasticsearch搜索的过程

1. 在初始查询阶段时，查询会广播到索引中每一个分片拷贝（主分片或者副本分片）。 每个分片在本地执行搜索并构建一个匹配文档的大小为 from + size 的优先队列。
2. 每个分片返回各自优先队列中 所有文档的 ID 和排序值 给协调节点，它合并这些值到自己的优先队列中来产生一个全局排序后的结果列表。
3. 接下来就是 取回阶段

![Query-Phase](http://upload-images.jianshu.io/upload_images/3709321-88f589037638c93d.jpeg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## Query VS Filter

## 字典树

![字典树](http://upload-images.jianshu.io/upload_images/3709321-7b6f0fab6f412f51.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## ES自动补齐功能（completion suggester）

ES(elasticsearch)的suggester共有四类(term suggester, phrase suggester, completion suggester, context suggester), 其中completion suggester作为搜索框中的自动补齐功能

## ES倒排索引

Term Index -> Term Dictionary -> Posting List

**term dictionary**:用二分查找的方式，比全遍历更快地找出目标的term。
**term index**: 尽量少的读磁盘，有必要把一些数据缓存到内存里,使用index作为章节表

Mysql只有term dictionary这一层，是以b-tree排序的方式存储在磁盘上的。检索一个term需要若干次的random access的磁盘操作。而Lucene在term dictionary的基础上添加了term index来加速检索，term index以树的形式缓存在内存中。从term index查到对应的term dictionary的block位置之后，再去磁盘上找term，大大减少了磁盘的random access次数。

**构建步骤**

1. 通过一系列的处理将文档集合转化为“词项ID—文档ID”对；
2. 对词项ID、文档ID进行排序，将具有相同词项对文档ID归并到该词项所对应的倒排记录表中，效果如图3所示；
3. 将上述步骤产生的倒排索引写入磁盘，生成中间文件；
4. 将上述所有的中间文件合并成最终的倒排索引；

### 联合索引查询

- 使用skip list数据结构。同时遍历gender和age的posting list，互相skip；
- 使用bitset数据结构，对gender和age两个filter分别求出bitset，对两个bitset做AND操作

## Block 压缩

