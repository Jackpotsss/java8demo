package org.example.function;

/**
 * 函数式接口只能有一个接口方法
 */
@FunctionalInterface
public interface Calculator {
    int calculator(int a,int b);
}
