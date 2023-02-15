package org.example.future;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * thenAccept() 有输入无输出： 接收上一个任务的处理结果，并消费处理。本身无返回结果。
 * thenRun()    无输入无输出： 上一个任务执行完成就开始执行 thenRun，不接收处理结果，本身无返回结果；
 *
 * thenApply()   有输入有输出：     拿到上一个任务的返回结果，继续执行新的异步任务，用于串行执行多个异步任务；
 * thenCompose()
 *
 * thenAcceptBoth()  两个任务都执行完成，可以得到两个返回结果；本身无返回值
 * thenCombine()    同上，区别在于它本身有返回值；
 *
 * whenComplete((result,exception)->{})  所有任务执行完成后或发生异常时调用
 * exceptionally(exception->{})           当发生异常时调用
 *
 * Async后缀：表示用于异步线程，没有Async后缀的方法使用调用线程运行下一个任务
 */
public class CompletableFutureDemo {

    private static ExecutorService executorService;

    public static void main(String[] args) throws Exception {
        executorService = Executors.newFixedThreadPool(2);
//        testSerialExe1();
//        testSerialExe2();
        testTwoTaskExe();
//        testParallelExe();
    }

    public static void testSerialExe1() throws Exception{

        CompletableFuture.supplyAsync(() -> {
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
    //多个异步任务 串行化执行
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
        Thread.sleep(1000); // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭:
    }
    //两个异步任务 并行化执行
    public static void testTwoTaskExe()  throws Exception{
        /**
         * thenAcceptBoth 和 thenCombine 都是等待两个CompletionStage 任务执行完成后，拿到两个返回结果后统一处理；
         * 唯一的区别是thenAcceptBoth 本身无有返回值，thenCombine 有返回值
         */
        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName()+" cf1...");
            return 1;
        });
        CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName()+" cf2...");
            return 2;
        });
        CompletableFuture<Void> voidCompletableFuture = cf1.thenAcceptBoth(cf2, (resultA, resultB) -> {
            System.out.println(resultA+resultB);
        });
        CompletableFuture<Integer> stringCompletableFuture = cf1.thenCombine(cf2, (resultA, resultB) -> {
            System.out.println(resultA+resultB);
            return resultA+resultB;
        });
    }
    //多个异步任务 并行化执行: anyof, allof
    public static void testParallelExe()  throws Exception{
        //准备两个异步任务 CompletableFuture
        CompletableFuture<Integer> completableFuture0 = CompletableFuture.supplyAsync(() -> {
            doWork();
            System.out.println(Thread.currentThread().getName()+" exe completableFuture0....");
            return 0;
        });
        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            doWork();
            System.out.println(Thread.currentThread().getName()+" exe completableFuture1....");
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
        //获取所有异步任务的返回结果
        List<CompletableFuture<Integer>> completableFutures = Arrays.asList(completableFuture0, completableFuture1);
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]))
            .thenApply(v->{//所有任务执行完毕,获取返回结果
                Stream<Integer> integerStream = completableFutures.stream().map(CompletableFuture::join);
                return integerStream.collect(Collectors.toList());
            }).thenAccept(v->{ //拿到所有返回结果
                v.forEach(System.out::println);
            });
        Thread.sleep(2000);
    }

    public static void doWork(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
    }
}
