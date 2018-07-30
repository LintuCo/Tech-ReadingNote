# 虚拟内存

Java 虚拟机具有一个堆，堆是运行时数据区域，所有类实例和数组的内存均从此处分配。堆是在 Java 虚拟机启动时创建的。

## JVM内存调优

Heap设定与垃圾回收Java Heap分为3个区，Young，Old和Permanent

1. **-Xms**：初始Heap大小
2. **-Xmx**：java heap最大值
3. **-Xmn**：young generation的heap大小

频繁发生GC的原因：
1. 程序内调用了System.gc()或Runtime.gc()。
2. Java的Heap太小，一般默认的Heap值都很小。
3. 频繁实例化对象，Release对象。此时尽量保存并重用对象