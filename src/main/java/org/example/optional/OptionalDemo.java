package org.example.optional;

import java.util.Date;
import java.util.Optional;

public class OptionalDemo {

    public static void main(String[] args) {

    }

    public static void test(){
        /**
         * 为非null的值创建一个Optional
         * of方法通过工厂方法创建Optional类。需要注意传入的参数不能为null。如果传入参数为null，则抛出NullPointerException
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
}
