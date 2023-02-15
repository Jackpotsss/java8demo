package org.example.lambda;

import org.example.function.Anmial;
import org.example.function.Calculator;

/**
 * Lambda 表达式只适用于函数式接口
 */
public class LambdaDemo {

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

    //变量作用域
    String str1 ="";
    public void testVarScope() {
        String str2 ="";  //被final修饰与否，在都具有final语义，不能在lambda 表达式中被修改；
        Calculator addCalculator1 = (a,b)->{
            String s = str2;	//	可以读取局部变量
            //str2 = "test";  //报错：不可以修改局部变量
            str1 = "test";  //可以修改全部变量
            return a+b;
        };
    }
}
