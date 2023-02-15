package org.example.method.ref;

import java.util.function.Supplier;

public class Car {

    public String name = "none";

    public Car() {
    }

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
        System.out.println("Following the ");
    }

    public void follow(Car another) {
        System.out.println("Following the " + another.toString());
    }

    public void follow(Car another, String str) {
        System.out.println("Following the " + another.toString());
    }

}
