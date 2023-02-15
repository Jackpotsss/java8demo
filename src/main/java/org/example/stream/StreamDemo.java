package org.example.stream;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamDemo {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
                new Student("jack", 15, 98),
                new Student("array", 17, 54),
                new Student("tom", 16, 54),
                new Student("ber", 19, 66),
                new Student("ber", 19, 66)
        );
        test(students);
    }

    public static void createStream(List<Student> students){
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
    public static void test(List<Student> students){
        students.stream()
                .filter(student -> student.getAge()<28)         //过滤操作 filter(Predicate)
                .sorted(Comparator.comparingInt(Student::getAge))    //排序 sorted(Comparator)
                .limit(2)       //限制返回个数
                .distinct()             //去重
                .skip(1)            //跳过N个元素
                .map(Student::getName)  //取出所有名称，映射出一个新流
                .forEach(System.out::println);
        //映射
        Stream<String> stringStream = students.stream()
                .map(student -> student.getName());
        //扁平化映射
        Stream<List<String>> s = Stream.of(
                Arrays.asList("A", "B", "C"),
                Arrays.asList("D", "E"),
                Arrays.asList("F"));
        Stream<String> str = s.flatMap(List::stream);

        //匹配
        boolean allMatch = students.stream().allMatch(student -> student.getAge() == 19);
        boolean anyMatch = students.stream().anyMatch(student -> student.getAge() == 19);
        boolean noneMatch = students.stream().noneMatch(student -> student.getAge() == 22);

        //查找
        Optional<Student> first = students.stream().findFirst();
        Optional<Student> any = students.stream().findAny();
        long count = students.stream().count();
        Optional<Student> max = students.stream().max((Comparator.comparingInt(Student::getAge)));
        Optional<Student> min = students.stream().min((Comparator.comparingInt(Student::getAge)));
        System.out.println();

        //聚合操作  reduce
        Stream<Double> scores = students.stream().map(Student::getEnglishScore);
        Double scoreSum = scores.reduce(0D, (score1, score2) -> score1 + score2);

        //收集到指定集合中 collect(Collector)
        Set<String> set = students.stream()
                .map(Student::getName)
                .collect(Collectors.toSet());   //将姓名收集到Set 集合中去
//                .collect(Collectors.toList());  //收集到List中去
//                .collect(Collectors.toCollection(HashSet::new)); //收集到HashSet中去;


    }
}
