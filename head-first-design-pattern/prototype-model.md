## 原型模式

*用原型实例指定所有创建对象的类型，并且通过复制这个拷贝创建新的对象。*

**特点**
- 必须存在一个现有的对象，也就是原型实例，通过原型实例创建新对象。
- 在Java中，实现`Cloneable`，并且因为所有的类都继承Object类，重写clone()方法来实现拷贝。