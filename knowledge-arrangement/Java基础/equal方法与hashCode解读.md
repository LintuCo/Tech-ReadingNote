

## Object中equals方法的实现原理

```java
    public boolean equals(Object obj) {
        eturn (this == obj);     
        }
```

## equals()与‘==’的区别

默认情况下也就是从超类Object继承而来的equals方法与‘==’是完全等价的，比较的都是对象的内存地址，但我们可以重写equals方法，使其按照我们的需求的方式进行比较，如String类重写了equals方法，使其比较的是字符的序列，而不再是内存地址。

## equals()的重写规则

- 自反性。对于任何非null的引用值x，x.equals(x)应返回true。
- 对称性。对于任何非null的引用值x与y，当且仅当：y.equals(x)返回true时，x.equals(y)才返回true。
- 传递性。对于任何非null的引用值x、y与z，如果y.equals(x)返回true，y.equals(z)返回true，那么x.equals(z)也应返回true。
- 一致性。对于任何非null的引用值x与y，假设对象上equals比较中的信息没有被修改，则多次调用x.equals(y)始终返回true或者始终返回false。
- 对于任何非空引用值x，x.equal(null)应返回false。

## equals()的重写规则之必要性深入解读


## 重写equals()中getClass与instanceof的区别

 instanceof 的作用是判断其左边对象是否为其右边类的实例，返回 boolean 类型的数据。可以用来判断继承中的子类的实例是否为父类的实现


[1]:https://blog.csdn.net/javazejian/article/details/51348320 "深度解读equal方法与hashCode方法渊源"