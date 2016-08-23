### BuilderGenerator

*日常开发中，我们经常会使用到Builder模式，我们一般都是直接手动地去写一个Builder类来使用。这里我们采用APT的方式进行配置生成Builder类*

#### 一、 快速接入

##### 1. 配置apt依赖

外层gradle添加

```groovy
classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
```

内层build.gradle添加

```groovy
apply plugin: 'com.neenbedankt.android-apt'
```

##### 2. 配置该框架依赖

内层build.gradle添加

```groovy
compile 'com.puke:buildergenerator-api:1.0.0'
apt 'com.puke:buildergenerator-compiler:1.0.0'
```

到这里，配置部分就结束了，接下来就是使用过程。

##### 3. 使用姿势

这里我们模拟一个需要使用Builder模式的类Dog

```java
public class Dog {

    private String name;
    private int age;
    private String sex;
    private Color color;

    @Builder("configure")
    Dog(@Item("customName") String name, @Item int age, @Item String sex, Color color) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.color = color;
    }
}
```

然后只需要build一下Project就ok了

我们可以在该Module的“build/generated/source/apt”目录下找到对应的DogBuilder类，如下

```java
// this is a generated file.
package com.puke.buildergeneator;

public final class DogBuilder {
  private String customName;

  private int age;

  private String sex;

  public final DogBuilder configureCustomName(String customName) {
    this.customName = customName;
    return this;
  }

  public final DogBuilder configureAge(int age) {
    this.age = age;
    return this;
  }

  public final DogBuilder configureSex(String sex) {
    this.sex = sex;
    return this;
  }

  public final Dog build() {
    return new Dog(customName, age, sex, null);
  }
}
```

##### 4. 几点说明

整体比较轻巧，使用起来也比较简单，我们可以重点看一下该类的构造方法，接下来几点说明：

>  构造方法上面加了@Builder注解

表示该类是要生成Builder类

> @Builder中注入了“configure”

表示生成注解类的组装子Item的方法前面全部都要加上”configure“关键字，当然这个是可选的，如果不加的话就会直接就直接取方法名

> 构造方法中加入了@Item注解

加入了@Item注解的参数表示Builder类中可以加入的参数，未加则不能通过Builder加入

> @Item中注入“customName”

表示在Builder类中给对应的属性设置的别名，不设时默认去参数名

#### 二、 项目源码 

https://github.com/puke3615/BuilderGeneator.git