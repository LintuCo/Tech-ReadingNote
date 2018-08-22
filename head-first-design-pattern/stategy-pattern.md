## 策略模式

定义了算法族，分别封装起来，让他们之间可以互相替换

- 封装类：也叫上下文，对策略进行二次封装，目的是避免高层模块对策略的直接调用。
- 抽象策略：通常情况下为一个接口，当各个实现类中存在着重复的逻辑时，则使用抽象类来封装这部分公共的代码，此时，策略模式看上去更像是模版方法模式。
- 具体策略：具体策略角色通常由一组封装了算法的类来担任，这些类之间可以根据需要自由替换

```java
interface IStrategy {
	public void doSomething();
}
class ConcreteStrategy1 implements IStrategy {
	public void doSomething() {
		System.out.println("具体策略1");
	}
}
class ConcreteStrategy2 implements IStrategy {
	public void doSomething() {
		System.out.println("具体策略2");
	}
}
class Context {
	private IStrategy strategy;
	
	public Context(IStrategy strategy){
		this.strategy = strategy;
	}
	
	public void execute(){
		strategy.doSomething();
	}
}
 
public class Client {
	public static void main(String[] args){
		Context context;
		System.out.println("-----执行策略1-----");
		context = new Context(new ConcreteStrategy1());
		context.execute();
 
		System.out.println("-----执行策略2-----");
		context = new Context(new ConcreteStrategy2());
		context.execute();
	}
}
```

## 优点

- 策略类之间可以自由切换，由于策略类实现自同一个抽象，所以他们之间可以自由切换。
- 易于扩展，增加一个新的策略对策略模式来说非常容易，基本上可以在不改变原有代码的基础上进行扩展。
- 避免使用多重条件

## 缺点

- 维护各个策略类会给开发带来额外开销
- 必须对客户端（调用者）暴露所有的策略类