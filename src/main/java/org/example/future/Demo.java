package org.example.future;

import java.util.concurrent.CompletableFuture;

public class Demo {
    public static void main(String[] args) {

        CompletableFuture.supplyAsync(()->{
            System.out.println("11111");
            return 1;
        }).thenApplyAsync(r->{
            return r+1;
        }).whenComplete((r,e)->{
            System.out.println(r);
        });
    }
}
