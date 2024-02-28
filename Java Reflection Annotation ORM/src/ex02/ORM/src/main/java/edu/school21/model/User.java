package edu.school21.model;

import edu.school21.annotation.*;

import javax.persistence.GenerationType;

@OrmEntity(table = "simple_user")
public class User {
    @OrmColumnId(id = GenerationType.AUTO, name = "id")
    private Long id;
    @OrmColumn(name = "first_name", length = 10)
    private String firstName;
    @OrmColumn(name = "last_name", length = 10)
    private String lastName;
    @OrmColumn(name = "age")
    private Integer age;

    public User(){
        this.id = 0L;
        this.firstName = null;
        this.lastName = null;
        this.age = 0;
    }

    public User(String firstName, String lastName, Integer age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                '}';
    }
}
