## 介绍

Java8 于2014年首次发布GA 版本，间隔上一个版本java7 有3年之久，所以更新幅度比较大，更新内容比较多。

- [接口默认方法](#接口默认方法) 
- [Lambda表达式](#Lambda表达式) 
  - 函数式接口
  - Lambda
- [方法引用](#方法引用)
  - 使用场景
  - 静态方法引用
  - 类构造器引用
  - 数组构造器引用
  - 特定对象的实例方法引用
  - 类的任意对象的实例方法引用
- [Stream API](#Stream API)
  - 流操作和管道
  - 获取流
  - 中间操作
  - 终止操作
  - 并行流
- [Optional 容器类](#Optional 容器类)
- [全新日期API](#全新日期API)
- [重复注解支持](#重复注解支持)
- [JVM](#JVM)
  - 默认GC
  - 移除Permgen
  - 元空间MetaSpace
- [CompletableFuture](#CompletableFuture)
  - 异步回调
  - Async后缀
  - 多任务串行化执行
  - 多任务并行化执行
- [其他类库增强](#其他类库增强)
  - [ConcurrentHashMap](#ConcurrentHashMap)
  - [Base64](#Base64)

注：

​	Java 新特性的规范都会在JSR中提出：https://jcp.org/en/jsr/overview

## 接口默认方法

Java8 允许在接口中定义默认实现方法：

```java
public interface Collection<E> extends Iterable<E> {
	
    int size();
    
    default <T> T[] toArray(IntFunction<T[]> generator) {
        return toArray(generator.apply(0));
    }
}
```

## Lambda表达式

​	Lambda 表达式并不是Java中独有的概念，而是一个通用的概念，在C++、Python、JavaScript 等编程语言中都有支持。

​	Java中的 Lambda 表达式 是用来精简代码的一种方式，使代码更加简洁。在学习Lambda 表达式之前，需要事先了解函数式接口。

### 函数式接口

​	函数式接口首先必须是一个 interface，接口里面只能有一个抽象方法（允许有默认方法、静态方法）；

​	`@FunctionalInterface` 注解用于标记当前接口是一个函数式接口，加上该注解，当你写的接口不符合函数式接口的定义时，编译器会报错。当然这不是必须的，如果接口类只有一个抽象方法，那么也算函数式接口。

```java
@FunctionalInterface
public interface HibernateCallback<T> {
	T doInHibernate(Session session) throws HibernateException;
}
```

**常用函数式接口**

Java8 提供了一些常用的函数式接口供开发人员使用，位于 `java.util.function` 包中，下面是几个最常用的函数式接口：

- Supplier  供给型接口：
- Consumer 消费型接口
- Function  函数型接口
- Predicate  断言型接口



Supplier  供给型接口：不需要入参，有返回值。

```
T get();
```

Consumer 消费型接口：有一个入参，无返回值。

```
void accept(T t);
```

Function  函数型接口：有一个入参，有返回值。

```
R apply(T t);
```

Predicate  断言型接口：有一个入参，返回布尔值。

```
boolean test(T t);
```

注：

​	在函数式接口中，抽象方法的名称已经不重要了，重要的是输入和输出分别是什么，即入参和返回值。

### Lambda 

​	Lambda 允许把函数作为一个方法的参数（函数作为参数传递进方法中），使用 Lambda 表达式**可以使代码变的更加简洁紧凑**。Lambda 表达式免去了使用匿名方法的麻烦，并且给予Java简单但是强大的函数化的编程能力。

以下是lambda表达式的重要特征:

- **可选类型声明：**不需要声明参数类型，编译器可以统一识别参数值。
- **可选的参数圆括号：**一个参数无需定义圆括号，但多个参数需要定义圆括号。
- **可选的大括号：**如果主体包含了一个语句，就不需要使用大括号。
- **可选的返回关键字：**如果主体只有一个表达式返回值则编译器会自动返回值，大括号需要指定表达式返回了一个数值。

示例：

```java
	public static void main(String[] args) {
		
		//Java8之前，匿名内部类的方式
		Calculator addCalculator = new Calculator() {
			
			@Override
			public int calculator(int a, int b) {
				return a+b;
			}
		};
		
		//1.Lambda 语法
		Calculator addCalculator1 = (int a,int b)->{
			return a+b;
		};
		//2.Lambda 语法：可以不需要声明参数类型
		Calculator addCalculator2 = (a,b)->{
			return a+b;
		};
		//3. 表达式语句只有一行，可以省略大括号和return
		Calculator addCalculator3 = (a,b)-> a+b;
		
		//4. 如果只有一个参数，还可以省略小括号
		Anmial bird = s->s;
		bird.say("吱吱叫！");
	}

	interface Anmial{
		String say(String something);
	}
	
	interface Calculator{
		int calculator(int a,int b);
	}
```

注：Lambda 表达式只适用于函数式接口。

**变量作用域** 

​	lambda 表达式中可以读写全局变量，也可以读取外层的局部变量，但**不能修改外层的局部变量**，因为外部的局部变量隐式的具有final 语义：

```java
public class LambdaDemo {
    
	String str1 ="";
	public void testVarScope() {
		String str2 ="";  //被final修饰与否，都具有final语义，不能在lambda 表达式中被修改；
		Calculator addCalculator1 = (a,b)->{
			String s = str2;	//	可以读取局部变量
			str2 = "test";  //报错：不可以修改局部变量		
			str1 = "test";  //可以修改全部变量
			return a+b;
		};
	}
}
```



## 方法引用

​	首先理解为什么会有方法引用？方法引用是对lambda表达式的进一步简化（**满足特定前提条件下**）。方法引用使用一对冒号 **`::`** 表示，方法引用可以使语言的构造更紧凑简洁，减少冗余代码。

下面先看一个例子：

```java
    public static void testStaticMethod(){
        //1. java8之前的写法：匿名内部类
        Comparator<Integer> comparator0 = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o1,o2);
            }
        };
        //2. lambda表达式
        Comparator<Integer> comparator = (o1, o2) -> Integer.compare(o1,o2);
        //3. 使用方法引用进一步简化
        Comparator<Integer>  comparator2 = Integer::compare;
    }
```

​	上述代码第三个比较器 Comparator 中直接使用了静态方法`compare`的引用，用`Integer::compare`表示。因此，所谓方法引用，是指如果某个方法签名和函数接口恰好一致，就可以直接使用方法引用。

​	**方法签名一致指的是方法入参和返回值相同即可，其他的像方法名、修饰符等都无所谓**。

上面代码Comparator接口的抽象方法为：

```java
int compare(T o1, T o2);
```

而 Integer的静态方法 compare的方法签名为：

```java
int compare(int x, int y)
```

### 使用场景

​	方法应用的使用场景主要有以下几种：

- 静态方法引用
- 类构造器引用
- 数组构造器引用
- 特定对象的实例方法引用
- 类的任意对象的实例方法引用

下面依次进行讲解，这里预先准备一个类用作测试：

```java
public class Car {

    public String name = "none";

    public Car() {}

    public Car(String name) {
        this.name = name;
    }

    public static Car create(Supplier<Car> supplier) {
        return supplier.get();
    }

    public static void collide(Car car) {
        System.out.println("Collided " + car.toString());
    }

    public void follow() {
        System.out.println("Following ");
    }

    public void follow(Car another) {
        System.out.println("Following the " + another.toString());
    }

    public void follow(Car another, String str) {
        System.out.println("Following the " + another.toString()+str);
    }
}
```

### 静态方法引用 

语法 Class::static_method

```java
     public static void testStaticMethod(){
        //1. java8之前的写法：匿名内部类
        Comparator<Integer> comparator0 = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o1,o2);
            }
        };
        //2. lambda表达式
        Comparator<Integer> comparator = (o1, o2) -> Integer.compare(o1,o2);
        //3. 使用方法引用进一步简化
        Comparator<Integer>  comparator2 = Integer::compare;

        List<Integer> integers = Arrays.asList(2, 3, 1, 7);
        integers.sort(comparator);

        //示例2：
        Consumer<Car> carConsumer = Car::collide;
        carConsumer.accept(new Car());
    }
```



### 类构造器引用

类构造方法引用 语法 Class::new

```java
public static void testConstructor(){

    Supplier<Car> car0  = () -> new Car();   //lambda写法，无参构造
    Supplier<Car> car1 = Car::new;            // 使用方法引用重构

    Function<String,Car> car3 = a -> new Car(a);    //同上  有参构造
    Function<String,Car> car4 = Car::new;

    System.out.println(car1.get().name);
    System.out.println(car4.apply("Ferrari").name);

    Car car = Car.create(Car::new);    //应用
}
```



### 数组构造器引用

构造数组的方法引用 语法  类型::new

```java
public static void testArray(){
    Function<Integer,String[]> StrArray0 = len -> new String[len];    //lambda写法 类数组
    Function<Integer,String[]> StrArray1 = String[]::new;           // 使用方法引用重构

    Function<Integer,int[]> StrArray3 = len -> new int[len];    //同上  基本数据类型的数组
    Function<Integer,int[]> StrArray4 = int[]::new;

    System.out.println(StrArray4.apply(10).length);
}
```



### 特定对象的实例方法引用

特定对象的方法引用 语法  instance::method

```java
public static void testObjectMethod(){
    Car car = new Car();
    //示例1：
    Consumer<Car> c = car::follow;  //有入参，无返回值
    None none = car::follow;        //无入参，无返回值
    //示例2：
    Consumer<Car> c0 = System.out::println;
    c0.accept(new Car());
}
```



### 类的任意对象的实例方法引用

类的任意对象的方法引用 语法 Class::method

```java
public static void testClassMethod(){
    //必须满足的条件是，lambda的第一个参数为实际执行方法的调用者，而执行方法的入参个数可以是0个、1个或多个
    Consumer<Car> c0 = car -> car.follow();
    Consumer2<Car,Car> c1 = (car1,car2) -> car1.follow(car2);
    Consumer3<Car,Car,String> c2 = (car1,car2,str) -> car1.follow(car2,str);

    Consumer<Car> c0_ = Car::follow;
    Consumer2<Car,Car> c1_ = Car::follow;
    Consumer3<Car,Car,String> c2_ = Car::follow;
    //应用
    List< Car > cars = Arrays.asList(new Car());
    cars.forEach(car -> car.follow(new Car()));
    cars.forEach(car -> car.follow());     //无入参，无返回值
    cars.forEach(Car::follow);
}
```



## Stream API

​	Stream API 是Java8 新引入的API，首先明确一点，Stream API 与 InputStream 和OutputStream 是完全不同的概念。Stream API 是对集合操作的增强，使之能够完成更高效的完成各种操作（过滤、排序、统计、分组），此外Stream API 与lambda 表达式结合使用会提高编码效率，并且提高可读性。

### 流操作和管道

​	流操作分为**中间操作**和**终止操作**，它们组合在一起形成流**管道** Pipeline。 流管道由源（例如集合、数组、生成器函数或 I/O 通道）组成； 后跟零个或多个中间操作，例如 Stream.filter 或 Stream.map； 和一个终止操作，例如 Stream.forEach 或 Stream.reduce。

​	中间操作返回一个新流。 他们总是懒惰执行； 执行诸如 filter() 之类的中间操作实际上并不执行任何过滤，而是创建一个新流，直到管道的终端操作被执行后才开始执行中间操作，中间操作不会影响源数据。

​	中间操作又分为**无状态操作**和**有状态操作**。 无状态操作，如 filter 和 map，在处理新元素时不保留先前看到的元素的状态——每个元素都可以独立于对其他元素的操作进行处理。 在处理新元素时，有状态操作可能会合并先前看到的元素的状态。有状态操作可能需要在产生结果之前处理整个输入。 例如，在看到流的所有元素之前，无法对流进行排序产生结果。 因此，在并行计算下，一些包含有状态中间操作的管道可能需要对数据进行多次传递，或者可能需要缓冲重要数据。 只包含无状态中间操作的流水线可以在单次通过中处理，无论是顺序的还是并行的，数据缓冲最少。
​	此外，一些操作被认为是**短路操作**。 如果一个中间操作在有无限输入时可能会产生一个有限的流，那么它就是短路的。 如果终端操作在无限输入时可能在有限时间内终止，则该终端操作是短路的。

​	终止操作，例如 Stream.forEach 或 IntStream.sum，执行终端操作后，stream pipeline被认为消耗掉了，不能再使用； 如果需要再次遍历同一个数据源，则必须通过数据源获取新的流。 几乎在所有情况下，终端操作都是立即的，在返回之前完成对数据源的遍历和管道的处理。

### 获取流

可以通过多种方式获取一个新的流：

```java
public void createStream(List<Student> students){
        //1.通过Collection接口的stream() 方法
        Stream<Student> stream = students.stream();
        //2. 通过Stream的静态方法of()
        Stream<String> jack = Stream.of("jack", "tom", "array");
        //3.创建无限流
        //迭代
        Stream<Integer> iterate = Stream.iterate(0, x -> x + 2);
        iterate.limit(10).forEach(System.out::println);  //如果不加limit操作，会无线循环计算
        //生成
        Stream<Double> generate = Stream.generate(() -> Math.random());
        generate.limit(10).forEach(System.out::println);
    }
```



### 中间操作

示例：

```java
    public static void test(List<Student> students){
        students.stream()
                .filter(student -> student.getAge()<28)         	//过滤操作 filter(Predicate)
                .sorted(Comparator.comparingInt(Student::getAge))   //排序 sorted(Comparator)  
                .limit(2)       		//限制返回个数
                .distinct()             //去重
                .skip(1)            	//跳过N个元素
                .map(Student::getName); //取出所有名称，映射出一个新流
     }
```

#### 过滤

​	`filter()`操作是对Stream 的所有元素进行迭代测试，不满足条件的就被过滤掉了，剩下的满足条件的元素就构成了一个新的Stream：

```java
Stream<T> filter(Predicate<? super T> predicate);
```

#### 排序

根据提供的比较器排序。对于有序流，排序是稳定的。对于无序流，没有稳定性保证。这是一个有状态的中间操作。

```java
Stream<T> sorted(Comparator<? super T> comparator);
```

#### 去重 

distinct  去重

​	对于有序流，不同元素的选择是稳定的(对于重复的元素，最先出现的元素被保留)。对于无序流，没有稳定性保证。=这是一个**有状态**的中间操作。

#### 限制条数

```java
Stream<T> limit(long maxSize);
```

这是一个短路的**有状态**中间操作。

#### 映射处理

**map 映射成一个新流**

```java
 Stream<String> stringStream = students.stream()
                .map(student -> student.getName());
```

**flatMap  扁平化映射**

如果`Stream`的每个元素是集合，而我们希望把每个集合中的元素都取出来放到一个Stream 中，，就可以使用`flatMap()`：

```java
Stream<List<String>> s = Stream.of(
                Arrays.asList("A", "B", "C"),
                Arrays.asList("D", "E"),
                Arrays.asList("F"));
Stream<String> str = s.flatMap(List::stream);
```

原理：

```ascii
┌─────────────┬─────────────┬─────────────┐
│┌───┬───┬───┐│┌───┬───┬───┐│┌───┬───┬───┐│
││ 1 │ 2 │ 3 │││ 4 │ 5 │ 6 │││ 7 │ 8 │ 9 ││
│└───┴───┴───┘│└───┴───┴───┘│└───┴───┴───┘│
└─────────────┴─────────────┴─────────────┘
                     │
                     │flatMap(List -> Stream)
                     │
                     │
                     ▼
   ┌───┬───┬───┬───┬───┬───┬───┬───┬───┐
   │ 1 │ 2 │ 3 │ 4 │ 5 │ 6 │ 7 │ 8 │ 9 │
   └───┴───┴───┴───┴───┴───┴───┴───┴───┘
```



#### 跳过

skip



### 终止操作

#### 查找

- findFirst()		查询第一个元素
- findAny()         查询随机一个元素
- max                 查询最大值元素
- min                  查询最小值元素

示例：

```java
public void test(List<Student> students){      
	Optional<Student> first = students.stream().findFirst();	
	Optional<Student> any = students.stream().findAny();		
	Optional<Student> max = students.stream().max((Comparator.comparingInt(Student::getAge)));
	Optional<Student> min = students.stream().min((Comparator.comparingInt(Student::getAge)));
}
```

#### 迭代

- forEach   对集合进行迭代操作

```java
students.stream().forEach(System.out::println);
```



#### 聚合

- reduce()       把所有元素按照聚合函数聚合成一个结果
- collect()        将流收集到指定集合中
- count()         用于返回元素个数；
- sum()：        求和
- average()   求平均数



**reduce 聚合**

`Stream.reduce()` 是Stream的一个聚合方法，它可以把一个Stream的所有元素按照聚合函数聚合成一个结果。

```java
public void testReduce() {
    int sum = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).reduce(0, (x, y) -> x + y);
    System.out.println(sum); // 45
}
```



**collect 收集**

收集到指定集合中 collect(Collector)

```java
        Set<String> set = students.stream()
                .map(Student::getName)
                .collect(Collectors.toSet());   //将姓名收集到Set 集合中去
//                .collect(Collectors.toList());  //收集到List中去
//                .collect(Collectors.toCollection(HashSet::new)); //收集到HashSet中去;
```



**count  计数** 

```java
long count = students.stream().count();
```

针对`IntStream`、`LongStream`和`DoubleStream`，还额外提供了以下聚合方法：

- `sum()`：求和
- `average()`：求平均数

#### 合并

`concat()`：将两个`Stream`合并为一个`Stream` 

```java
Stream<String> s1 = List.of("A", "B", "C").stream();
Stream<String> s2 = List.of("D", "E").stream();
// 合并:
Stream<String> s = Stream.concat(s1, s2);
List<String> list s.collect(Collectors.toList()); // [A, B, C, D, E]
```



#### 匹配

匹配是用来测试`Stream`的元素是否满足以下条件

```java
boolean allMatch(Predicate)   	//测试是否所有元素均满足测试条件；
boolean anyMatch(Predicate) 	//测试是否至少有一个元素满足测试条件。
boolean noneMatch(Predicate) 	//测试是否所有元素均不满足测试条件；
```



### 并行流

​	在集合元素数量非常大的情况，可以使用并行处理来加快处理速度。把一个普通`Stream`转换为可以并行处理的`Stream`非常简单，只需要用`parallel()`进行转换：

```java
Stream<String> s = ...
String[] result = s.parallel() // 变成一个可以并行处理的Stream
                   .sorted() // 可以进行并行排序
                   .toArray(String[]::new);
```

​	经过`parallel()`转换后的`Stream`只要可能，就会对后续操作进行并行处理。我们不需要编写任何多线程代码就可以享受到并行处理带来的执行效率的提升。

​	并行流底层基于并发包提供的Fork/Join 框架 ，将一个大任务拆分成多个子任务分别执行，多线程处理。可以充分利用CPU计算资源。

## Optional 容器类

​	Java 8引入了一个新的Optional类，这是一个可以为null的容器对象。如果值存在则 `isPresent()` 方法会返回true，调用`get()`方法会返回该对象。下面依次讲解该类的主要方法：

- of
- ofNullable
- isPresent
- get
- ifPresent
- orElse
- orElseGet
- orElseThrow
- map
- flatMap
- filter

示例代码：

```java
  public static void test(){
        /**
         * 为非null的值创建一个Optional
         * of方法通过工厂方法创建Optional类。需要注意传入的参数不能为null。如果传入参数为null，则抛出     	NullPointerException
         */
        Optional<String> nameOpt = Optional.of("jack");
        //为指定的值创建一个Optional，如果指定的值为null，则返回一个空的Optional。
        Optional<String> empty = Optional.ofNullable(null);

        if(nameOpt.isPresent()){  //如果值存在返回true，否则返回false
            String name = nameOpt.get();  //如果Optional有值则将其返回，否则抛出NoSuchElementException
        }
        //如果Optional实例有值则为其调用consumer，否则不做处理
        nameOpt.ifPresent(name -> System.out.println(name));
        //orElse() :  如果有值则将其返回，否则返回指定的其它值
        String tom = empty.orElse("Tom");
        /**
         *  orElseGet: orElseGet与orElse方法类似，区别在于得到默认值的方式
         *  orElse方法将传入的参数直接作为默认值，orElseGet方法可以接受 Supplier接口的实现用来生成默认值
         */
        String s = empty.orElseGet(() -> "Default Value");
        String s2 = empty.orElseGet(() -> {
            //TODO
            return "Default Value";
        });
        //orElseThrow: 如果有值则将其返回，否则抛出supplier接口创建的异常
        try {
            String s1 = empty.orElseThrow(RuntimeException::new);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    public static void test2(){
        Optional<String> nameOpt = Optional.of("jack");
        //map(Function)：使用lambda 对值进行操作，返回值可以是任意类型，并且返回结果会自动用Optional包装
        Optional<String> newOpt = nameOpt.map(name -> "des_" + name);
        Optional<Date> newOpt2 = nameOpt.map(name -> new Date());

        //flatMap(Function) 与 map(Function)相似，唯一的区别是flatMap的返回结果不会自动用Optional包装，必须手动包装一下
        Optional<Date> newOpt3 = nameOpt.flatMap(name -> Optional.of(new Date()));

        //filter(Predicate)  使用断言函数对值进行过滤
        Optional<String> stringOptional = nameOpt.filter(name -> name.equals("jack"));
    }
```



## 全新日期API

从Java 8开始，`java.time`包提供了新的日期和时间API，主要涉及的类型有：

- 本地日期和时间：`LocalDateTime`，`LocalDate`，`LocalTime`；
- 带时区的日期和时间：`ZonedDateTime`；
- 时刻：`Instant`；
- 时区：`ZoneId`，`ZoneOffset`；
- 时间间隔：`Duration`。

以及一套新的用于取代`SimpleDateFormat`的格式化类型`DateTimeFormatter`。

和旧的API相比，新API严格区分了时刻、本地日期、本地时间和带时区的日期时间，并且，对日期和时间进行运算更加方便。

此外，新API修正了旧API不合理的常量设计：

- Month 的范围用1~12表示1月到12月；
- Week 的范围用1~7表示周一到周日；



```

```



## 重复注解

在Java8 之前，不允许在同一个地方（类、字段、方法）多次使用相同的注解，如这样：

```java
public class RepeatAnnotationUseNewVersion {

    @Authority(role="Admin")
    @Authority(role="Manager")
    public void doSomeThing(){ }
}
```

Java8 中这种语法被支持，提高了代码的可读性。其实在java 8之前也有重复使用注解的解决方案，但可读性不是很好，比如下面的代码:

```java
public @interface Authority {
     String role();
}

public @interface Authorities {
    Authority[] value();
}

public class RepeatAnnotationUseOldVersion {

    @Authorities({@Authority(role="Admin"),@Authority(role="Manager")})
    public void doSomeThing(){
    }
}
```

由另一个注解来存储重复注解，在使用时候，用存储注解Authorities来扩展重复注解。

## JVM

### 默认GC

Java8 默认的垃圾收集器是 `ParallelGC`，即 Parallel Scavenge + Parallel Old，如果要使用G1 收集器，需要通过JVM参数显式指定。

```shell
# 使用 G1 (Garbage First) 垃圾收集器 
-XX:+UseG1GC
```



### 移除Permgen

​	在java8的 JVM中（Hotspot），移除了PermGen space， PermGen space的全称是Permanent Generation space,是指内存的永久保存区，这一部分用于存放 Class和 Meta的信息，Class在被加载的时候被放入PermGen space区域，它和和存放对象实例的 Heap区域不同，所以如果你的应用会加载很多 class ，就很可能出现PermGen space错误。如 `java.lang.OutOfMemoryError: PermGen space`。



### 元空间(MetaSpace)

​	JDK8 HotSpot JVM 将移除永久区，使用**本地内存**来存储类元数据信息并称之为: 元空间(Metaspace)。

<img src="https://mikai-blog-image-service.oss-cn-beijing.aliyuncs.com/img/java8-jvm-1.png" alt="java8-jvm-1" style="zoom:80%;" />





## CompletableFuture

​	JDK1.8新加入的功能特性，支持异步回调。Future 接口本身是不支持异步回调的，只能通过get() 方法阻塞获取异步结果。

### 异步回调

串行化执行多个异步任务：

```java
public static void testSerialExe1() throws Exception{

        CompletableFuture.supplyAsync(() -> {	//
            doWork();
            System.out.println(Thread.currentThread().getName()+" 开始执行");
            return 1;
        },executorService).thenApply(integer -> {
            System.out.println(Thread.currentThread().getName()+" 接收到上一个任务的处理结果为："+integer);
            return integer+1;
         }).thenApplyAsync(integer -> {
             System.out.println(Thread.currentThread().getName()+" 接收到上一个任务的处理结果为："+integer);
             return integer+10;
         }).whenComplete((result,exception)->{
             System.out.println("所有任务执行完成，返回结果： "+result);
         }).exceptionally(e-> {
            e.printStackTrace();
            return null;
        });
    }
```

说明：

- supplyAsync：开始异步任务，方法需要一个 Supplier函数，无输入，有输出
- thenApply：上一个异步任务执行完之后，拿到返回结果作为入参，继续执行新任务，方法需要Function函数，有输入，有输出
- thenApplyAsync：与 thenApply 的不同之处是，在其他线程池获取线程异步执行，而不是使用当前线程；
- whenComplete： 所有任务执行完成之后的回调，入参中有返回结果和异常信息；
- exceptionally：专门处理异常信息的回调方法；

### Async后缀

上面的示例，控制台打印：

```
pool-1-thread-1 开始执行
pool-1-thread-1 接收到上一个任务的处理结果为：1
ForkJoinPool.commonPool-worker-3 接收到上一个任务的处理结果为：2
所有任务执行完成，返回结果： 12
```

可以看出前两个异步任务使用的是指定线程池中的线程，而使用 async 后缀的操作，使用的是默认线程池（ForkJoinPool）中的线程。

注意CompletableFuture 的命名规则：

- `xxx()`：表示将使用已有的线程继续执行；
- `xxxAsync()`：表示将异步在线程池中执行。

### 多任务串行化执行

上面已经展示了多任务串行化执行的demo，还有以下几种执行链式调用的方法，主要区别在于有无输入和输出：

```java
    public static void testSerialExe2()  throws Exception{

        CompletableFuture.runAsync(() -> {     //runAsync: 无输入、无输出
            doWork();
            System.out.println(Thread.currentThread().getName()+" exe completableFuture1...");
        },executorService).thenAccept(integer -> {    //thenAccept: 有输入、无输出
            System.out.println(Thread.currentThread().getName()+" 接收到上一个任务的处理结果为："+integer);
        }).thenRun(() -> {          //thenRun: 无输入、无输出
            System.out.println(Thread.currentThread().getName()+" 执行");
        });
        //thenCompose :连接两个 CompletableFuture
        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName()+" cf1...");
            return 1;
        }).thenCompose(c -> {
            return CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName()+" cf3...");
                return c+3;
            });
        }).whenComplete((r,e)-> {
            System.out.println(r);
        });
    }
```

说明：

- runAsync：开始异步任务，无输入，无输出；
- thenAccept：继续执行异步任务，可以拿到上一个任务的执行结果。有输入、无输出
- thenRun：无输入，无输出
- thenCompose：与thenAccept 类似，区别在于thenCompose用于连接两个CompletableFuture；



### 多任务并行化执行

首先看下**两个异步任务**并行化执行的场景：

```java
    public static void testTwoTaskExe()  throws Exception{

        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName()+"cf1...");
            return 1;
        });
        CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName()+"cf2...");
            return 2;
        });
        CompletableFuture<Void> voidCompletableFuture = cf1.thenAcceptBoth(cf2, (resultA, resultB) -> {
            System.out.println();
        });
        CompletableFuture<Integer> stringCompletableFuture = cf1.thenCombine(cf2, (resultA, resultB) -> {
            System.out.println(resultA+resultB);
            return resultA+resultB;
        });
    }     
```

​	`thenAcceptBoth` 和 `thenCombine` 都是等待两个`CompletionStage` 任务执行完成后，拿到两个返回结果后统一处理；唯一的区别是thenAcceptBoth 本身无有返回值，thenCombine 有返回值；

多个异步任务 并行化执行: anyof, allof

```java
    public static void testParallelExe()  throws Exception{
        //准备两个异步任务 CompletableFuture
        CompletableFuture<Integer> completableFuture0 = CompletableFuture.supplyAsync(() -> {
            doWork();
            System.out.println("exe completableFuture0....");
            return 0;
        });
        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            doWork();
            System.out.println("exe completableFuture1....");
            return 1;
        });
        //anyOf：任意一个任务执行成功就返回，并且可以拿到返回结果
        CompletableFuture.anyOf(completableFuture0, completableFuture1)
            .thenAccept(integer -> {
                System.out.println("其中一个任务执行成功，执行结果为："+integer);
            });
        //allOf：所有任务都执行成功才能继续执行 (返回值没有直接提供所有异步结果)
        CompletableFuture.allOf(completableFuture0,completableFuture1)
                .thenAccept(r->{
                    System.out.println("所有异步任务执行成功");
                });
    }
```

​	`anyOf()`表示只要任意一个异步任务执行成功，就进行下一步，`allOf()` 表示必须全部的异步任务执行成功，才能进行下一步，这些组合操作可以实现非常复杂的异步流程控制。

​	上面 allOf 的示例，在thenAccept 的方法入参中并不能拿到所有异步任务的执行结果，CompletableFuture 的设计中并没有直接提供，要想获取所有异步任务的返回结果，还需要编写一些额外代码：

```java
public static void testParallelExe()  throws Exception{
	//获取所有异步任务的返回结果
    List<CompletableFuture<Integer>> completableFutures = Arrays.asList(completableFuture0, completableFuture1);
    CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]))
       .thenApply(v->{//所有任务执行完毕,获取返回结果
            Stream<Integer> integerStream = completableFutures.stream().map(CompletableFuture::join);
            return integerStream.collect(Collectors.toList());
        }).thenAccept(v->{ //拿到所有返回结果
            v.forEach(System.out::println);
        });
}
```

注：Java9中的 CompletableFuture还添加了`completeOnTimeout`、`orTimeout` 等关于超时的方法，方便对超时任务的处理。

## 其他类库增强



### ConcurrentHashMap

取消分段锁的实现，改为红黑树的数据结构



### Base64

在Java8 中首次添加Base64 工具类，位于 `java.util` 包中。

## README

