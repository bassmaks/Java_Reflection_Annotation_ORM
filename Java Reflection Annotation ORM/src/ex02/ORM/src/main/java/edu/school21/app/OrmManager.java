package edu.school21.app;

import edu.school21.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrmManager {
    private final Connection connection;

    public OrmManager(Connection connection) {
        this.connection = connection;
    }

    public void initialize(List<Class<?>> entityClasses) throws SQLException {
        for (Class<?> entityClass : entityClasses) {
            if (entityClass.isAnnotationPresent(OrmEntity.class)) {
                createTable(entityClass);
            }
        }
    }

    private void createTable(Class<?> entityClass) throws SQLException {
        String tableName = getTableName(entityClass);
        List<String> columns = new ArrayList<>();
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(OrmColumn.class)) {
                OrmColumn columnAnnotation = field.getAnnotation(OrmColumn.class);
                String columnName = columnAnnotation.name();
                String columnType = getColumnType(field.getType(), columnAnnotation.length());
                boolean isIdColumn = field.isAnnotationPresent(OrmColumnId.class);
                columns.add(columnName + " " + columnType + (isIdColumn ? "id SERIAL PRIMARY KEY" : ""));
            }
        }
        String sqlDropTable = "DROP TABLE IF EXISTS " + tableName;
        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + tableName +
                "(id SERIAL PRIMARY KEY NOT NULL, " + String.join(", ", columns) + ")";
        System.out.println(sqlDropTable);
        System.out.println(sqlCreateTable);
        try (Statement statement = connection.createStatement()) {
            statement.execute(sqlDropTable);
            statement.execute(sqlCreateTable);
        }
    }

    public void save(Object entityObj) {
        List<Object> fieldList = new ArrayList<>();
        fieldList = getAnnotatedFieldValues(entityObj);
        String tableName = getTableName(entityObj.getClass());
        Field[] fields = entityObj.getClass().getDeclaredFields();
        List<String> columns = new ArrayList<>();

        for (Field field : fields) {
            OrmColumn annotation = field.getAnnotation(OrmColumn.class);
            if (annotation != null) {
                String columnName = annotation.name();
                columns.add(columnName);

            }
        }

        String sqlInsert = "INSERT INTO " + tableName + " (" + String.join(", ", columns) + ") " +
                "VALUES (?, ?, ?)";
        setSql(sqlInsert, fieldList);
        try {
            setIdEntityObject(entityObj);
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T findById(Long id, Class<T> aClass) {

        Map<String, Object> values = mapIsSelectById(id, aClass);

        T entity;
        try {
            entity = aClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        for (Field field : aClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(OrmColumn.class)) {
                String columnName = field.getAnnotation(OrmColumn.class).name();
                field.setAccessible(true);
                try {
                    field.set(entity, values.get(columnName));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else if(field.isAnnotationPresent(OrmColumnId.class)){
                String columnName = field.getAnnotation(OrmColumnId.class).name();
                field.setAccessible(true);
                try {
                    field.set(entity, id);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return entity;
    }

    public void update(Object entityObj) {
        List<Object> fieldList = new ArrayList<>();
        fieldList = getAnnotatedFieldValues(entityObj);
        String tableName = getTableName(entityObj.getClass());
        Field[] fields = entityObj.getClass().getDeclaredFields();
        List<String> columns = new ArrayList<>();
        for (int i = 1; i < fields.length; i++) {
            Field field = fields[i];
            OrmColumn annotation = field.getAnnotation(OrmColumn.class);
            if (annotation != null) {
                String columnName = annotation.name();
                columns.add(columnName);
            }
        }
        Long id = getId(entityObj);

        String sqlUpdate = "UPDATE " + getTableName(entityObj.getClass()) +
                " SET " + String.join(" = ?, ", columns) + "= ? " + "WHERE id = " + id + ";";
        setSql(sqlUpdate, fieldList);
        try {
            setIdEntityObject(entityObj);
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private  <T> Map<String, Object> mapIsSelectById(Long id, Class<T> aClass) {
        String sqlSelect = "SELECT * FROM " + getTableName(aClass) + " WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sqlSelect)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Map<String, Object> fieldValues = new HashMap<>();

                    for (Field field : aClass.getDeclaredFields()) {
                        if (field.isAnnotationPresent(OrmColumn.class)) {
                            String columnName = field.getAnnotation(OrmColumn.class).name();
                            field.setAccessible(true);
                            Object value = resultSet.getObject(columnName);
                            fieldValues.put(field.getName(), value);
                        } else if(field.isAnnotationPresent(OrmColumnId.class)){
                            String columnName = field.getAnnotation(OrmColumnId.class).name();
                            field.setAccessible(true);
                            Object value = resultSet.getObject(columnName);
                            fieldValues.put(field.getName(), value);
                        }
                    }
                    return fieldValues;
                } else {
                    System.err.println("Invalid argument");
                    System.exit(1);
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL query", e);
        }
    }

    void setSql(String sql, List<Object> fieldList) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int i = 1;
            for (int j = 1; j < fieldList.size(); j++) {
                Object value = fieldList.get(i);
                if (value instanceof Integer) {
                    statement.setInt(i++, (Integer) value);
                } else if (value instanceof String) {
                    statement.setString(i++, (String) value);
                } else if (value instanceof Double) {
                    statement.setDouble(i++, (Double) value);
                } else if (value instanceof Long) {
                    statement.setLong(i++, (Long) value);
                } else if (value == null) {
                    statement.setNull(i++, Types.NULL);
                } else {
                    throw new IllegalArgumentException("Неподдерживаемый тип значения: " + value.getClass());
                }
            }
            String message = statement.toString();
            printSql(message);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Long getId(Object entityObj) {
        Field idField = null;
        Long id = 0L;
        try {
            idField = entityObj.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            id = (Long) idField.get(entityObj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    void printSql(String message) {
        int index = message.indexOf("wrapping");
        if (index != -1) {
            String result = message.substring(index + "wrapping".length()).trim();
            System.out.println(result);
        }
    }

    private ArrayList<Object> getAnnotatedFieldValues(Object entity) {
        ArrayList<Object> data = new ArrayList<>();
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                data.add(field.get(entity));
            }
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
        return data;
    }

    private String getColumnType(Class<?> fieldType, int length) {
        if (fieldType == String.class) {
            return "VARCHAR(" + length + ")";
        } else if (fieldType == Integer.class) {
            return "INT";
        } else if (fieldType == Double.class) {
            return "DOUBLE PRECISION";
        } else if (fieldType == Boolean.class) {
            return "BOOLEAN";
        } else if (fieldType == Long.class) {
            return "BIGINT";
        } else {
            throw new IllegalArgumentException("Неподдерживаемый тип поля: " + fieldType.getSimpleName());
        }
    }

    private void setIdEntityObject(Object entityObj) throws SQLException, NoSuchFieldException, IllegalAccessException {
        String sqlGetLastId = "SELECT MAX(id) FROM " + getTableName(entityObj.getClass());
        try (PreparedStatement statement = connection.prepareStatement(sqlGetLastId)) {
            ResultSet rs = statement.executeQuery();
            long lastId = 0;
            while (rs.next()) {
                lastId = rs.getLong(1);
            }
            Field idField = entityObj.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entityObj, lastId);
        }
    }

    private String getTableName(Class<?> entityClass) {
        OrmEntity entityAnnotation = entityClass.getAnnotation(OrmEntity.class);
        return entityAnnotation.table();
    }
}
