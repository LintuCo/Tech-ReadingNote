## 简介

**fail-fast机制**是java集合(Collection)中的一种错误机制。当多个线程对同一个集合的内容进行操作时，就可能会产生fail-fast事件。
例如：当某一个线程A通过iterator去遍历某集合的过程中，若该集合的内容被其他线程所改变了；那么线程A访问集合时，就会抛出ConcurrentModificationException异常，产生fail-fast事件。

## fail-fast解决方法

使用JUC包下对应的类

## fail-fast原理

在调用`next()`和`remove()`是会执行`checkForComodification`，如果modCount不等于excepedModCount，则抛出CME异常

## 解决fail-fast的原理

CopyOnWriteArrayList继承接口List，自己实现Iterator，