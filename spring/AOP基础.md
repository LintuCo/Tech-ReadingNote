# Spring AOP基础

### AOP术语

![AOP](http://p82ueiq23.bkt.clouddn.com/AOP.PNG)

**连接点**：一个类或者一段程序代码拥有一些具有边界性质的特定点
**切点**：定位特定的连接点
**增强**：织入目标类连接点的一段程序代码
**目标对象**
**引介**：特殊的增强，为类添加一些属性和方法
**织入**：将增强添加到目标类的具体连接点上的过程。（Spring采用动态代理织入、AspectJ采用编译期织入和类装载期织入）
  1. 编译器织入
  2. 类装载期织入
  3. 动态代理织入

### AOP的实现者

1. AspectJ
2. AspectWerkz
3. JBoss AOP
4. Spring AOP- 纯Java实现，不需要专门的编译过程，运行期通过代理方式向目标类织入增强代码

### 基础知识

#### JDK静态代理

创建一个接口，然后创建被代理的类实现该接口并且实现该接口中的抽象方法。之后再创建一个代理类，同时使其也实现这个接口。在代理类中持有一个被代理对象的引用，而后在代理类方法中调用该对象的方法。

```java
public interface HelloInterface{
  void sayHello();
}

public class Hello implements HelloInterface{
  @Override
  public void sayHello(){
    System.out.println("hello World");
  }
}

public class HelloProxy implements HelloInterface{
  private HelloInterface helloInterface = new Hello();

  @Override
  public void sayHello(){
    System.out.println("Before");
    helloInterface.sayHello();
    System.out.println("After");
  }
}

public class Client{
  public static void main(Streing[] args){
    HelloProxy helloProxy = new HelloProxy();
    helloiProxy.sayHello();
  }
}
```

**缺点**

静态代理只能为一个类服务

#### JDK动态代理

通过实现该`InvocationHandler`接口定义横切逻辑，并通过反射机制调用目标类的代码，动态的将横切逻辑和业务编织在一起

```java
public interface HelloInterface {
    void sayHello();
}

public class HelloProxy implements InvocationHandler {

    private Object subject;

    public HelloProxy(Object subject){
        this.subject = subject;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Before invoke");
        method.invoke(subject,args);
        System.out.println("After invoke");
        return null;
    }
}
public class Client {
    public static void main(String[] args) {
        Hello hello = new Hello();

        InvocationHandler handler = new HelloProxy(hello);
        HelloInterface helloInterface = (HelloInterface) Proxy.newProxyInstance(
                handler.getClass().getClassLoader(),
                hello.getClass().getInterfaces(), handler);
        helloInterface.sayHello();
    }
}

public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
```

JDK动态代理，基于接口实现的，通过接口指向实现类实例的多态方式，有效的将具体实现与调用解耦

**静态VS动态**

- JDK静态代理通过直接编码创建，JDK动态代理利用反射机制在运行时创建代理
- 每一个代理的实例都会有一个关联的调用处理程序(InvocationHandler)。对待代理实例进行调用时，将对方法的调用进行编码并指派到它的调用处理器(InvocationHandler)的invoke方法。所以对代理对象实例方法的调用都是通过InvocationHandler中的invoke方法来完成的，而invoke方法会根据传入的代理对象、方法名称以及参数决定调用代理的哪个方法。

#### CGLib动态代理

CGLib采用底层的字节码技术，为一个类创建子类，在子类中采用方法拦截技术拦截所有父类的调用并顺势织入横切逻辑

**步骤**
- 生成代理类继承被代理类，*如果委托类被final修饰，则不可被继承，不可被代理*
- 代理类为委托方法生成两种方法：1. 重写sayHello，2. CGLIB$sayHello$0，直接调用父类方法
- 当执行代理对象的sayHello方法时，会首先判断一下是否存在实现了MethodInterceptor接口的CGLIB$CALLBACK_0;，如果存在，则将调用MethodInterceptor中的intercept方法

**实现**

代理类将委托类作为自己的父类并为其中的非final委托方法创建两个方法，一个是与委托方法签名相同的方法，它在方法中会通过super调用委托方法；另一个是代理类独有的方法。在代理方法中，它会判断是否存在实现了MethodInterceptor接口的对象，若存在则将调用intercept方法对委托方法进行代理

**特点**

底层将方法全部存入一个数组中，通过数组索引直接进行方法调用

#### 区别

JDK动态代理只能为接口创建代理实例，CGLib采用动态创建子类的方法生成代理对象，所以不能对目标类中的final或private进行代理