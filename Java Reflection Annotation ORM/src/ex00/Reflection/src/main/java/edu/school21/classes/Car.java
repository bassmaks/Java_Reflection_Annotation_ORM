package edu.school21.classes;

public class Car {
    private String name;
    private Double engine;

    public Car(String name, Double engine) {
        this.name = name;
        this.engine = engine;
    }

    @Override
    public String toString() {
        return "Car{" +
                "name='" + name + '\'' +
                ", engine=" + engine +
                '}';
    }
}
