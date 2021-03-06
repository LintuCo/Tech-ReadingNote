
# 数据库引擎

数据库引擎是用于存储、处理和保护数据的核心服务。利用数据库引擎可控制访问权限并快速处理事务，从而满足企业内大多数需要处理大量数据的应用程序的要求。

## InnoDB

InnoDB是一个事务型的存储引擎，有行级锁定和外键约束。
Innodb引擎提供 **对数据库ACID事务的支持**，并且实现了SQL标准的四种 **隔离级别**。该引擎还提供了行级锁和外键约束，它的设计目标是处理大容量数据库系统。

## MyIsam

MyIASM是MySQL默认的引擎，但是它没有提供对数据库事务的支持，也不支持行级锁和外键，因此当INSERT(插入)或UPDATE(更新)数据时即写操作需要锁定整个表，效率便会低一些。

**特点**
1. 查询速度很快
2. MyISAM极度强调快速读取操作
3. MyIASM中存储了表的行数

**缺点**
1. 不能在表损坏后恢复数据
2. 不支持事务的设计


## InnoDB和MyIsam使用及其原理对比：

**插入效率**：速度上MyISAM快，但是增删改是涉及事务安全的，所以用InnoDB相对好很多。
**行数保存**：InnoDB 中不保存表的具体行数，也就是说，执行select count() from table时，InnoDB要扫描一遍整个表来计算有多少行，但是MyISAM只要简单的读出保存好的行数即可。
**索引存储**：对于AUTO_INCREMENT类型的字段，InnoDB中必须包含只有该字段的索引，但是在MyISAM表中，可以和其他字段一起建立联合索引。MyISAM支持全文索引（FULLTEXT）、压缩索引，InnoDB不支持
**锁的支持**：MyISAM只支持表锁。InnoDB支持表锁、行锁 行锁大幅度提高了多用户并发操作的新能。但是InnoDB的行锁，只是在WHERE的主键是有效的，非主键的WHERE都会锁全表的

## InnoDB和MyIsam引擎原理

聚簇索引的叶子节点就是数据节点，而非聚簇索引的叶子节点仍然是索引节点，只不过有指向对应数据块的指针。

### MyIASM引擎的索引结构

**MyISAM索引结构**：MyISAM索引用的B+ tree来储存数据，MyISAM索引的指针指向的是键值的地址，地址存储的是数据。

**索引检索**：MyISAM中索引检索的算法为首先按照B+Tree搜索算法搜索索引，如果指定的Key存在，则取出其data域的值，然后以data域的值为地址，根据data域的值去读取相应数据记录。

### InnoDB引擎的索引结构

**InnoDB索引结构**：Innodb的索引文件本身就是数据文件，即B+Tree的数据域存储的就是实际的数据，这种索引就是聚集索引。这个索引的key就是数据表的主键，因此InnoDB表数据文件本身就是主索引。

**辅助索引**：辅助索引需要检索两遍索引：首先检索辅助索引获得主键，然后用主键到主索引中检索获得记录

*不建议使用过长的字段作为主键，因为所有辅助索都引用主索引，过长的主索引会令辅助索引变得过大，并且非单调的主键会造成新纪录的插入而发生分裂调整，使用自增字段作为主键*

## 索引

- 主码索引
- 聚集索引
- 辅助索引
- 非主码索引
- 唯一索引
- 外键索引
- 全文索引
- 复合索引
- 覆盖索引
- 单列索引
- 普通索引

**主码索引**：在主码基础上建立的索引。primary key。

**辅助索引**：在MyISAM中，辅助索引的结构和主码索引的结构是一样的，都是采用的是B+树结构，且叶子节点存储的都是数据记录的地址。而InnoDB中虽然也采用的是Ｂ＋树存储，但是辅助索引的叶子节点存储的是对应于主码索引的主键

**外键索引**：外键索引其实就是主索引或者辅助索引，主要用于表之间的连接操作等与外键有关的操作。如果为某个外键字段定义了一个外键约束条件，MySQL就会定义一个内部索引来帮助自己以最有效率的方式去管理和使用外键约束条件。目前MySQL默认的存储引擎中，只有InnoDB支持外键约束且不要求存在对应列的索引，但是通常考虑效率都应加上。

**全文索引**：文本字段上的普通索引只能加快对出现在字段内容最前面的字符串(也就是字段内容开头的字符)进行检索操作。如果字段里存放的是由几个、甚至是多个单词构成的较大段文字，普通索引就没什么作用了。这种检索往往以LIKE %word%的形式出现，这对MySQL来说很复杂，如果需要处理的数据量很大，响应时间就会很长。这类场合正是全文索引(full-text index)可以大显身手的地方。在生成这种类型的索引时，MySQL将把在文本中出现的所有单词创建为一份清单，查询操作将根据这份清单去检索有关的数 据记录。全文索引即可以随数据表一同创建，也可以等日后有必要时再使用下面这条命令添加：

**覆盖索引**：指满足了查询中给定表用到的所有的列。where子句、order by、group by以及select语句中的所有的列，全覆盖。

## 主存存取原理

当系统需要读取主存时，则将地址信号放到地址总线上传给主存，主存读到地址信号后，解析信号并定位到指定存储单元，然后将此存储单元数据放到数据总线上，供其它部件读取。

## 磁盘存取原理

索引一般以文件形式存储在磁盘上，索引检索需要磁盘I/O操作。当需要从磁盘读取数据时，系统会将数据逻辑地址传给磁盘，磁盘的控制电路按照寻址逻辑将逻辑地址翻译成物理地址，即确定要读的数据在哪个磁道，哪个扇区。为了读取这个扇区的数据，需要将磁头放到这个扇区上方，为了实现这一点，磁头需要移动对准相应磁道，这个过程叫做寻道，所耗费时间叫做 **寻道时间**，然后磁盘旋转将目标扇区旋转到磁头下，这个过程耗费的时间叫做 **旋转时间**。

## 局部性原理与磁盘预读

由于存储介质的特性，磁盘本身存取就比主存慢很多，再加上机械运动耗费，磁盘的存取速度往往是主存的几百分分之一，因此为了提高效率，要尽量减少磁盘I/O。为了达到这个目的，磁盘往往不是严格按需读取，而是每次都会预读，即使只需要一个字节，**磁盘也会从这个位置开始，顺序向后读取一定长度的数据放入内存**。这样做的理论依据是计算机科学中著名的局部性原理：
当一个数据被用到时，其附近的数据也通常会马上被使用。程序运行期间所需要的数据通常比较集中。由于磁盘顺序读取的效率很高（不需要寻道时间，只需很少的旋转时间），因此对于具有局部性的程序来说，预读可以提高I/O效率。
**预读的长度一般为页（page）的整倍数。页是计算机管理存储器的逻辑块**，硬件及操作系统往往将主存和磁盘存储区分割为连续的大小相等的块，每个存储块称为一页（在许多操作系统中，页得大小通常为4k），主存和磁盘以页为单位交换数据。当程序要读取的数据不在主存中时，会触发一个缺页异常，此时系统会向磁盘发出读盘信号，磁盘会找到数据的起始位置并向后连续读取一页或几页载入内存中，然后异常返回，程序继续运行。

## 最左前缀原理

**全列匹配**

索引对顺序是敏感的，但是MySQL的查询优化器会自动调整where子句的条件顺序