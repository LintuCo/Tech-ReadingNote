## HDFS 组件 区别


- NameNode: 
  - 管理文件系统命名空间并控制客户端对文件的访问。
  - 确定块与`DataNode`的映射，并处理`DataNode`的故障。
- `SecondaryNameNode`:
  - 执行`NameNode`文件系统的状态的检查点，不是故障切换节点



#### HDFS复制

#### HDFS安全模式

- `NameNode`启动时，它将进入块不能被复制或者删除的只读安全模式
  1. 通过将`fsimage`文件加载到内存并重播编辑日志重建以前的文件系统状态
  2. 在块与数据节点之间创建映射的方法是为等待足够的数据节点注册一边至少有一份数据可用。

~~~java
 WildcardOperatorEnum(String format){
        this.format = format;
    }
    private String format;

    public String getSql(String key, Object value){
        return String.format(format, key, FieldUtil.toString(value));
    }
~~~



##### HDFS检查点和备份

`NameNode`将HDFS文件系统的元数据存储在`fsimage`的文件中，文件系统的修改被写入一个编辑日志文件，并且在启动时，`NameNode`将所做的编辑都合并到新的`fsimage`中。

**HDFS BackupNode** 同时在内存和磁盘上保持文件系统的命名空间的最新副本，不需要从活动的`NameNode`下载`fsimage`和编辑文件。

##### HDFS快照

- 快照可以从文件系统的一个子树或整个文件系统创建。
- 快照可用于数据的备份，防止用户错误和用于灾难恢复。
- 创建快照是瞬时的。
- `DataNode`上的块不会被复制，快照文件记录了块列表和文件大小，没有进行数据备份。
- 快照不影响常规的HDFS操作。




<head> 
    <script defer src="https://use.fontawesome.com/releases/v5.0.13/js/all.js"></script> 
    <script defer src="https://use.fontawesome.com/releases/v5.0.13/js/v4-shims.js"></script> 
</head> 
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.13/css/all.css">