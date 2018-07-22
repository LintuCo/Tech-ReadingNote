## Class类文件

### Class类文件结构

Class文件是由一组以8位字节为基础单位的 __二进制流__。

__存储数据类型：__

- 无符号数：用来描述数字、索引引用、数量值或者按照UTF-8编码构成的无符号字符串。
- 表：用于描述有层次关系的复合结构的数据


    |      类型      |         名称        |          数量         |   定义   |
    |----------------|---------------------|-----------------------|----------|
    | u4             | magic               | 1                     |          |
    | u2             | minor_version       | 1                     | 次版本号 |
    | u2             | major_version       | 1                     | 主版本号 |
    | u2             | constant_pool_count | 1                     |          |
    | cp_info        | constant_pool       | constant_pool_count-1 |          |
    | u2             | access_flags        | 1                     | 访问标志 |
    | u2             | this_class          | 1                     | 类索引   |
    | u2             | super_class         | 1                     | 父索引   |
    | u2             | interfaces_count    | 1                     |          |
    | u2             | interfaces          | interfaces_count      |          |
    | u2             | fields_count        | 1                     |          |
    | field_info     | fields              | fields_count          |          |
    | u2             | methods_count       | 1                     |          |
    | method_info    | methods             | methods_count         |          |
    | u2             | attributes_count    | 1                     |          |
    | attribute_info | attributes          | attributes+count      |          |


- magic: 确定这个文件是否为一个能够被虚拟机接受的class文件
- constant_pool:
    + 字面量
    + 符号引用：
        * 类和接口的全限定名
        * 字段的名称和描述符
        * 方法的名称和描述符
- fields: 用于描述接口或者类中声明的变量（类级变量、实例级变量） __不包括方法内部声明变量__
    + 字段的作用域（public,private,protected）
    + 实例变量还是类变量
    + 可变性
    + 并发可见性
    + 是否被序列化
    + 字段数据类型

