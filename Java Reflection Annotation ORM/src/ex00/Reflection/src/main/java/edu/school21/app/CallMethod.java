package edu.school21.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static edu.school21.app.Program.*;

public class CallMethod {
    Object obj;

    public CallMethod(Object obj) {
        this.obj = obj;
        try {
            callMethod();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTypeArgumentMethod(Method methodForCall) {
        Class<?>[] parameterTypes = methodForCall.getParameterTypes();
        return parameterTypes[0].getSimpleName();
    }

    private void callMethod() throws InvocationTargetException, IllegalAccessException {
        System.out.println("Enter name of the method for call:");
        Class<?> classObj = obj.getClass();
        Method[] methods = classObj.getDeclaredMethods();
        boolean containsName = false;
        Method methodForCall = null;
        if (scanner.hasNextLine()) {
            String nameMethod = scanner.nextLine();
            for (Method mt : methods) {
                if (nameMethod.equals(mt.getName() + " (" + methodReturnType(mt) + ")")) {
                    methodForCall = mt;
                    containsName = true;
                    break;
                }
            }
        } else {
            System.err.println("Invalid input");
            System.exit(1);
        }

        if (!containsName) {
            System.err.println("Invalid input");
            System.exit(1);
        } else {
            System.out.println("Enter " + getTypeArgumentMethod(methodForCall) + " value:");
            methodForCall.setAccessible(true);
            for (int i = 0; i < methodForCall.getParameterCount(); ++i) {
                String type = String.valueOf(methodReturnType(methodForCall));
                switch (type) {
                    case "String":
                        if (scanner.hasNextLine()) {
                            System.out.println("Method returned:");
                            System.out.println(methodForCall.invoke(obj, scanner.nextLine()));
                        } else {
                            System.err.println("Invalid input");
                            System.exit(1);
                        }
                        break;
                    case "Integer":
                        if (scanner.hasNextInt()) {
                            System.out.println("Method returned:");
                            System.out.println(methodForCall.invoke(obj, scanner.nextInt()));
                        } else {
                            System.err.println("Invalid input");
                            System.exit(1);
                        }
                        break;
                    case "Double":
                        if (scanner.hasNextDouble()) {
                            System.out.println("Method returned:");
                            System.out.println(methodForCall.invoke(obj, scanner.nextDouble()));
                        } else {
                            System.err.println("Invalid input");
                            System.exit(1);
                        }
                        break;
                    case "Long":
                        if (scanner.hasNextLong()) {
                            System.out.println("Method returned:");
                            System.out.println(methodForCall.invoke(obj, scanner.nextLong()));
                        } else {
                            System.err.println("Invalid input");
                            System.exit(1);
                        }
                        break;
                    case "boolean":
                        if (scanner.hasNextBoolean()) {
                            System.out.println("Method returned:");
                            System.out.println(methodForCall.invoke(obj, scanner.nextBoolean()));
                        } else {
                            System.err.println("Invalid input");
                            System.exit(1);
                        }
                        break;
                }
            }
        }
    }
}
