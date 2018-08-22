# Content

![Design-Pattern](http://p82ueiq23.bkt.clouddn.com/Design-Pattern.PNG)

## 设计模式原则

1. 单一职责原则(Single Responsibility Principle, SRP)：一个类只负责一个功能领域中的相应职责，或者可以定义为：就一个类而言，应该只有一个引起它变化的原因。
2. 开闭原则(Open-Closed Principle, OCP)：一个软件实体应当对扩展开放，对修改关闭。即软件实体应尽量在不修改原有代码的情况下进行扩展。
3. 里氏代换原则(Liskov Substitution Principle, LSP)：所有引用基类（父类）的地方必须能透明地使用其子类的对象。
4. 依赖倒转原则(Dependency Inversion  Principle, DIP)：抽象不应该依赖于细节，细节应当依赖于抽象。换言之，要针对接口编程，而不是针对实现编程。
5. 接口隔离原则(Interface  Segregation Principle, ISP)：使用多个专门的接口，而不使用单一的总接口，即客户端不应该依赖那些它不需要的接口。
6. 迪米特法则(Law of  Demeter, LoD)：一个软件实体应当尽可能少地与其他实体发生相互作用。


## 创建型模式

对象实例化的模式，创建型模式用于解耦对象的实例化过程。

- [单例模式](singleton-pattern.md)
- [工厂方法模式](factory-pattern.md)
- 抽象工厂模式
- 建造者模式
- [原型模式]()

## 结构型模式

把类或对象结合在一起形成一个更大的结构。

- 适配器模式
- 桥接模式
- [组合模式](component-pattern.md)
- [装饰模式](decorator-pattern.md)
- 外观模式
- 亨元模式
- 代理模式

## 行为型模式

类和对象如何交互，及划分责任和算法。

- 访问者模式
- 模板模式
- [策略模式](stategy-pattern.md)
- 状态模式
- [观察者模式]()
- [备忘录模式]()
- 中介者模式
- [迭代器模式](iterator-pattern.md)
- 解释器模式
- 命令模式
- 责任链模式