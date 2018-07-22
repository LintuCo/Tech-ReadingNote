# 前言

有一些对象，我们只需要一个，比如说：线程池、缓存、对话框、注册表的对象，这类对象只能有一个实例

## 饿汉模式

在第一次引用该类的时候就创建对象实例，而不管实际是否需要创建。代码如下：
```java
public class Singleton {   
    private static Singleton = new Singleton();
    private Singleton() {}
    public static getSignleton(){
        return singleton;
    }
}
```

## 懒汉模式

### 单线程

由私有构造器和一个公有静态工厂方法构成，在工厂方法中对singleton进行null判断，如果是null就new一个出来，最后返回singleton对象。这种方法可以实现延时加载，缺点在于：**线程不安全。如果有两条线程同时调用getSingleton()方法，就有很大可能导致重复创建对象。**

```java
public class Singleton {
    private static Singleton singleton = null;
    private Singleton(){}
    public static Singleton getSingleton() {
        if(singleton == null) singleton = new Singleton();
        return singleton;
    }
}
```

### 多线程

将对singleton的null判断以及new的部分使用synchronized进行加锁。同时，对singleton对象使用volatile关键字进行限制，保证其对所有线程的可见性，并且禁止对其进行指令重排序优化。

```java
public class Singleton {
    private static volatile Singleton singleton = null;
 
    private Singleton(){}
 
    public static Singleton getSingleton(){
        synchronized (Singleton.class){
            if(singleton == null){
                singleton = new Singleton();
            }
        }
        return singleton;
    }    
}
```

上面的写法每次调用getSingleton()方法，都必须在synchronized这里进行排队

```java
public class Singleton {
    private static volatile Singleton singleton = null;
    private Singleton(){}
    public static Singleton getSingleton(){
        if(singleton == null){
            synchronized (Singleton.class){
                if(singleton == null){
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }    
}
```

枚举法
```java
public enum Singleton {
    INSTANCE;
    private String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
}
```
**优点**：使用枚举除了线程安全和防止反射强行调用构造器之外，还提供了自动序列化机制，防止反序列化的时候创建新的对象。