## 运行时栈帧结构

**栈帧**：用于支持虚拟机进行方法调用和方法执行的数据结构，是虚拟机运行时数据区中的虚拟机栈的栈元素。栈帧存储了方法的局部变量表、操作数栈、动态连接和方法返回地址

## 局部变量表

用于存放方法参数和方法内部定义的局部变量，局部变量表的容量以变量槽（Slot）为最小单位。

**reference**：从此引用中直接或间接查找到对象在Java堆中的数据存放的起始地址索引；此索引中直接或间接地查找到对象所属数据类型在方法去中地存储地类型信息。

## 操作数栈

**两个栈帧之间地数据共享**

## 动态连接

每个栈帧都包含一个指向运行时常量池中该栈帧所属方法地运用，持有这个引用是为了支持方法调用过程中地动态连接。
**静态解析**：Class文件地常量池中存有大量地符号引用，字节码中地方法调用指令以常量池中指向方法地符号引用作为参数，这些符号引用一部分会在类加载阶段或者第一次使用的时候转化为直接引用
**动态连接**：将在每一次运行期间转化为直接引用

## 方法返回地址

退出方法：
- 正常完成出口：遇到返回指令，可能有返回值传递给上层的方法调用者
- 异常完成出口

## 方法调用

方法调用不等同于方法执行，方法调用的任务是确定被调用方法的版本，暂时不涉及方法内部的具体运行过程

### 解析

在类加载的解析阶段，会有其中的一部分符号引用转化为直接引用，前提是：方法在程序真正运行之前就有可确定的调用版本，并且这个方法的调用版本在运行期是不可改变的。（静态方法和私有方法，前者与类型直接关联，后者在外部不可被访问）

方法调用字节码指令

- invokestatic：调用静态方法
- invokespecial：调用实例构造器方法、私有方法和父类方法
- invokevirtual：调用所有的虚方法
- uinvokeinterface：调用接口方法，会在运行时在确定一个实现此接口的对象
- invokedynamic：现在运行时动态解析出调用点限定符所引用的方法，然后执行该方法

**非虚方法**：
只能调被`invokestatic`和`invokespecial`指令调用的方法，可以在解析阶段中确定唯一的调用版本，符合这个条件的有 静态方法、私有方法、实例构造器、父类方法，直接解析成直接引用

### 分派

#### 静态分派

```java
public class StaticDispatch {
    static abstract class Human{}
    static class Man extends  Human{}
    static class Woman extends  Human{}
    
    public void sayHello(Human guy){
        System.out.println("Hello human");
    }

    public void sayHello(Man guy){
        System.out.println("Hello man");
    }

    public void sayHello(Woman guy){
        System.out.println("Hello woman");
    }

    public static void main(String[] args) {
        Human man = new Man();
        Human woman = new Woman();
        StaticDispatch sr = new StaticDispatch();
        sr.sayHello(man);
        sr.sayHello(woman);
    }
}

//Hello human
//Hello human
```

静态变量的变化仅仅在使用时发生，变量本身的静态类型不会被改变，并且最终的静态类型是在编译器可知的；实际类型变化的结果运行期才可确定，编译器在编译程序的时候并不知道一个对象的实际类型

#### 动态分派

```java
public class DynamicDispatch {
    static abstract class Human{
        protected abstract void sayHello();
    }
    static class Man extends Human{
        @Override
        protected void sayHello() {
            System.out.println("man say hello");
        }
    }

    static class Woman extends Human{
        @Override
        protected void sayHello() {
            System.out.println("woman say hello");
        }
    }

    public static void main(String[] args) {
        Human man = new Man();
        Human woman = new Woman();
        man.sayHello();
        woman.sayHello();
        man = new Woman();
        man.sayHello();
    }
}
//man say hello
//woman say hello
//woman say hello
```

1. 找到操作数栈顶的第一个元素所指向的对象的实际类型，记作C (这里就是Man或者是Woman)
2. 如果在类型C中找到和常量中描述符合简单名称都相符合的方法，则进行访问权限校验，如果通过就返回这个方法的直接引用，查找结束；不通过，返回 IllegalAccessError异常
3. 否则（没找到符合的方法），按照继承关系从下往上对C的各个父类进行第2步的搜索和验证过程
4. 如果始终没有找到合适的方法，则跑出java.lang.AbstractMethodError异常

invokevirtual指令 第一步就是确定接受者的实际类型
所以两次调用把常量池相同的类方法符号引用解析到了不同的直接引用上，这就是Java方法重写的本质
这种在运行期间根据实际类型确定方法执行版本的分派过程就是 动态分派

#### 单分派与多分派

根据一个宗量来对目标方法选择就是单分派，多于一个，则是多分派。

## 虚拟机动态分派的实现

为类在方法区中建立一个虚方法表，用虚方法表索引来代替元数据查找以提高性能。
虚方法表中存放着各个方法的实际入口地址，如果子类没有重写父类方法，那么子类的虚方法表中该函数的入口地址和父类相同方法的地址是一致的。
为了程序实现的方便，具有相同签名的方法，父类和子类虚方法表中都应当具有相同的索引，这样类型改变，只要换一张虚方法表就可以了，迅速通过相同的索引找到对应方法的入口地址。

方法表一般在类加载的连接阶段进行初始化。

静态分派，编译期间选择，多和重载有关。（类内部的方法多态性）
动态分派，运行期才执行，与重写有关。（类继承的多态性）