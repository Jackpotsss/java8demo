package org.example.stream;

import java.util.Objects;

public class Student {

    private String name;
    private int age;
    private double englishScore;

    public Student(String name, int age, double englishScore) {
        this.name = name;
        this.age = age;
        this.englishScore = englishScore;
    }
    public Student() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getEnglishScore() {
        return englishScore;
    }

    public void setEnglishScore(double englishScore) {
        this.englishScore = englishScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return age == student.age && Double.compare(student.englishScore, englishScore) == 0 && Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, englishScore);
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", englishScore=" + englishScore +
                '}';
    }
}
