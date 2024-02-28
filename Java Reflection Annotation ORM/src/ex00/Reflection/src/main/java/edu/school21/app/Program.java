package edu.school21.app;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class Program {
    static Object obj = null;
    static Scanner scanner = new Scanner(System.in);
    static final String SEPARATOR = "---------------------";

    public static void main(String[] args) {
        printClasses();
        System.out.println(SEPARATOR);
        Class<?> clas = null;
        Object obj;
        try {
            System.out.println("Enter class name:");
            System.out.print("->");
            clas = Class.forName("edu.school21.classes." + scanner.next());
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid input");
            System.exit(1);
        }
        printFields(clas);
        printMethods(clas);
        System.out.println(SEPARATOR);
        createObject(clas);
        System.out.println(SEPARATOR);
        changeField(clas);
        System.out.println(SEPARATOR);
        CallMethod callMethod = new CallMethod(Program.obj);
        scanner.close();

    }

    private static void printClasses() {
        Reflections reflections = new Reflections("edu.school21.classes",
                new SubTypesScanner(false));
        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
        System.out.println("Classes:");
        for (Class<?> clas : classes) {
            System.out.print("  - ");
            System.out.println(clas.getSimpleName());
        }
    }

    private static void printFields(Class<?> clas) {
        Field[] fields = clas.getDeclaredFields();
        System.out.println("fields:");
        for (Field field : fields) {
            System.out.println("      " + field.getType().getSimpleName() + " " + field.getName());
        }
    }

    private static void printMethods(Class<?> clas) {
        Method[] methods = clas.getDeclaredMethods();
        System.out.println("methods:");
        for (Method method : methods) {
            StringBuilder sb = methodReturnType(method);
            System.out.println("      " + method.getReturnType().getSimpleName() + " " +
                                method.getName() + " " + "(" + sb + ")");
        }
    }

    static StringBuilder methodReturnType(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        StringBuilder parameters = new StringBuilder();
        for (Class<?> pr : parameterTypes) {
            parameters.append(pr.getSimpleName());
        }
        return parameters;
    }
    private static void createObject(Class<?> clas) {
        //scanner.reset();
        System.out.println("Let’s create an object.");
        Constructor<?>[] constructor = clas.getConstructors();
        for (Constructor<?> cs : constructor) {
            Class<?>[] parameterTypes = cs.getParameterTypes();
            Object[] arguments = new Object[parameterTypes.length];
            Field[] fields = clas.getDeclaredFields();

            if (parameterTypes.length > 0) {
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (parameterTypes[i] == String.class) {
                        System.out.println(fields[i].getName()+":");
                        System.out.print("->");
                        scanner.nextLine();
                        arguments[i] = scanner.next();
                    } else if (parameterTypes[i].equals(Integer.class)) {
                        System.out.println(fields[i].getName()+":");
                        System.out.print("->");
                        arguments[i] = scanner.nextInt();
                        scanner.nextLine();
                    } else if (parameterTypes[i].equals(Double.class)) {
                        System.out.println(fields[i].getName()+":");
                        arguments[i] = scanner.nextDouble();
                        scanner.nextLine();
                    } else if (parameterTypes[i].equals(Long.class)) {
                        System.out.println(fields[i].getName()+":");
                        arguments[i] = scanner.nextLong();
                        scanner.nextLine();
                    } else if (parameterTypes[i].equals(Boolean.class)) {
                        System.out.println(fields[i].getName()+":");
                        arguments[i] = scanner.nextBoolean();
                        scanner.next();
                    }
                }
            }
            try {
                if (parameterTypes.length > 0) {
                    obj = cs.newInstance(arguments);
                    System.out.println("Object created: " + obj);
                }
            } catch (Exception e) {
                System.err.println("Input incorrect");
                System.exit(1);
            }
        }
    }
    private static void changeField(Class<?> clas) {
        System.out.println("Enter name of the field for changing:");
        String fieldName = null;
        String newFieldName;
        Field[] fields = clas.getDeclaredFields();
        if (!scanner.hasNextLine()) {
            System.err.println("No input provided");
            System.exit(1);
        } else {
            fieldName = scanner.nextLine();
        }
        String finalFieldName = fieldName;
        if (Arrays.stream(fields).anyMatch(field -> finalFieldName.equals(field.getName()))) {

            System.out.println("Enter " + finalFieldName.toString() + " value:");
            if (!scanner.hasNextLine()) {
                System.err.println("Incorrect Input");
                System.exit(1);
            } else {
                newFieldName = scanner.nextLine();
                try {
                    Field field = clas.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    if (field.getType() == Integer.class) {
                        field.set(obj, Integer.parseInt(newFieldName));
                    } else if (field.getType() == Double.class) {
                        field.set(obj, Double.parseDouble(newFieldName));
                    } else if (field.getType() == Long.class) {
                        field.set(obj, Long.parseLong(newFieldName));
                    } else if (field.getType() == Boolean.class) {
                        field.set(obj, Boolean.parseBoolean(newFieldName));
                    } else {
                        field.set(obj, newFieldName);
                    }
                    System.out.println("Object updated: " + obj);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            System.err.println("Incorrect Input");
            System.exit(1);
        }
    }

}
