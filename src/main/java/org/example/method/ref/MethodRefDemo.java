package org.example.method.ref;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MethodRefDemo {

    public static void main(String[] args) {

    }

    //静态方法引用 语法 Class::static_method
    public static void testStaticMethod(){
        //1. java8之前的写法：
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

    //构造方法引用 语法 Class::new
    public static void testConstructor(){

        Supplier<Car> car0  = () -> new Car();   //lambda写法，无参构造
        Supplier<Car> car1 = Car::new;            // 使用方法引用重构

        Function<String,Car> car3 = a -> new Car(a);    //同上  有参构造
        Function<String,Car> car4 = Car::new;

        System.out.println(car1.get().name);
        System.out.println(car4.apply("Ferrari").name);

        Car car = Car.create(Car::new);    //应用
    }
    //构造数组的方法引用 语法  类型::new
    public static void testArray(){
        Function<Integer,String[]> StrArray0 = len -> new String[len];    //lambda写法 类数组
        Function<Integer,String[]> StrArray1 = String[]::new;           // 使用方法引用重构

        Function<Integer,int[]> StrArray3 = len -> new int[len];    //同上  基本数据类型的数组
        Function<Integer,int[]> StrArray4 = int[]::new;

        System.out.println(StrArray4.apply(10).length);
    }

    //特定对象的方法引用 语法  instance::method
    public static void testObjectMethod(){
        Car car = new Car();
        //示例1：
        Consumer<Car> c = car::follow;  //有入参，无返回值
        None none = car::follow;        //无入参，无返回值
        //示例2：
        Consumer<Car> c0 = System.out::println;
        c0.accept(new Car());
    }
    //类的任意对象的方法引用 语法 Class::method
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

    public interface Consumer2<T,K> {
        void accept(T t,K k);
    }
    public interface Consumer3<T,K,V> {
        void accept(T t,K k,V v);
    }
    public interface Supplier2<T> {
        T get2(int a);
    }
    public interface None {
        void none();
    }

}
