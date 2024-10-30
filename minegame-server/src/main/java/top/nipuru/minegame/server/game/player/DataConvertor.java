package top.nipuru.minegame.server.game.player;

import top.nipuru.minegame.common.message.database.FieldMessage;
import top.nipuru.minegame.common.message.database.QueryPlayerRequest;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

public class DataConvertor {

    private static final Map<Class<?>, DataClassCache> cache = new HashMap<>();

    static void preload(QueryPlayerRequest request, Class<?> dataClass) throws Exception {
        DataClassCache dataClassCache = getOrCache(dataClass);
        Map<String, Class<?>> fields = new HashMap<>();
        for (String field : dataClassCache.tableFields.keySet()) {
            fields.put(field, dataClassCache.tableFields.get(field).getType());
        }
        QueryPlayerRequest.TableInfo tableInfo = new QueryPlayerRequest.TableInfo(dataClassCache.tableName, dataClassCache.autoCreate, fields, dataClassCache.uniqueFields);
        request.getTables().add(tableInfo);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    static <T> T unpack(Map<String, List<List<FieldMessage>>> tables, Class<T> dataClass) throws Exception {
        DataClassCache dataClassCache = getOrCache(dataClass);
        T instance = (T) dataClassCache.constructor.newInstance();
        List<List<FieldMessage>> fieldMessagesList = tables.get(dataClassCache.tableName);
        if (fieldMessagesList == null || fieldMessagesList.isEmpty()) {
            return null;
        }
        if (fieldMessagesList.size() > 1) {
            throw new IOException("Too many results for " + dataClass.getName());
        }
        for (FieldMessage fieldMessage : fieldMessagesList.get(0)) {
            Field field = dataClassCache.fields.get(fieldMessage.getName());
            if (field == null) {
                continue;
            }
            field.set(instance, fieldMessage.getValue());
        }
        return instance;
    }

    static String[] getFields(Object data, Function<?, ?>[] functions) {
        try {
            DataClassCache dataClassCache = getOrCache(data.getClass());
            if (functions.length == 0) {
                return dataClassCache.fields.keySet().toArray(new String[0]);
            }
            String[] fields = new String[functions.length];
            for (int i = 0; i < functions.length; i++) {
                // 根本没多少损耗
                Method writeReplace = functions[i].getClass().getDeclaredMethod("writeReplace");
                writeReplace.setAccessible(true);
                SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(functions[i]);
                String methodName = lambda.getImplMethodName();
                String fieldName = dataClassCache.fieldNames.get(methodName);
                if (fieldName == null) {
                    throw new Exception("Field: " + methodName + " not found");
                }
                fields[i] = fieldName;
            }
            return fields;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> List<T> unpackList(Map<String, List<List<FieldMessage>>> tables, Class<T> dataClass) throws Exception {
        DataClassCache dataClassCache = getOrCache(dataClass);
        List<List<FieldMessage>> fieldMessagesList = tables.get(dataClassCache.tableName);
        if (fieldMessagesList == null || fieldMessagesList.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(fieldMessagesList.size());
        for (List<FieldMessage> fieldMessages : fieldMessagesList) {
            T instance = (T) dataClassCache.constructor.newInstance();
            for (FieldMessage fieldMessage : fieldMessages) {
                Field field = dataClassCache.fields.get(fieldMessage.getName());
                if (field == null) {
                    continue;
                }
                field.set(instance, fieldMessage.getValue());
            }
            result.add(instance);
        }
        return result;
    }

    static void pack(Map<String, List<List<FieldMessage>>> tables, Object data) throws Exception {
        DataClassCache dataClassCache = getOrCache(data.getClass());
        List<List<FieldMessage>> fieldMessagesList = tables.computeIfAbsent(dataClassCache.tableName, k -> new ArrayList<>());
        List<FieldMessage> fieldMessages = new ArrayList<>(dataClassCache.fields.size());
        for (Map.Entry<String, Field> entry : dataClassCache.fields.entrySet()) {
            FieldMessage fieldMessage = new FieldMessage().setName(entry.getKey())
                    .setValue(entry.getValue().get(data));
            fieldMessages.add(fieldMessage);
        }
        fieldMessagesList.add(fieldMessages);
    }

    static DataClassCache getOrCache(Class<?> dataClass) throws Exception {
        DataClassCache dataClassCache = cache.get(dataClass);
        if (dataClassCache == null) {
            dataClassCache = createCache(dataClass);
            cache.put(dataClass, dataClassCache);
        }
        return dataClassCache;
    }

    static DataClassCache createCache(Class<?> dataClass) throws Exception {
        if (!dataClass.isAnnotationPresent(Table.class)) {
            throw new Exception("dataClass must be annotated with @Table, provided: " + dataClass.getName());
        }
        Constructor<?> constructor;
        try {
            constructor = dataClass.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new Exception("dataClass must have default constructor, provided: " + dataClass.getName());
        }
        Table table = dataClass.getAnnotation(Table.class);
        String tableName = table.name();
        boolean autoCreate = table.autoCreate();
        List<String> uniqueFields = new ArrayList<>();          // 被 @Unique 注释的字段
        Set<String> tempFields = new HashSet<>();               // 被 @Temp 注释的字段
        Map<String, Field> fields = new LinkedHashMap<>();      // 所有字段
        Map<String, Field> tableFields = new LinkedHashMap<>(); // 除 @Temp 之外的字段
        Map<String, Field> updateFields = new LinkedHashMap<>();// 包含在 tableFields 中的非 Unique 字段
        Map<String, String> fieldNames = new LinkedHashMap<>();
        for (Field field : dataClass.getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            if (field.isAnnotationPresent(Alias.class)) {
                name = field.getAnnotation(Alias.class).name();
            }
            if (field.isAnnotationPresent(Unique.class)) {
                uniqueFields.add(name);
            } else {
                updateFields.put(name, field);
            }
            if (field.isAnnotationPresent(Temp.class)) {
                tempFields.add(name);
            } else {
                tableFields.put(name, field);
            }
            fields.put(name, field);
            fieldNames.put(field.getName(), name);
        }
        return new DataClassCache(tableName, autoCreate, uniqueFields, tempFields, fields, tableFields, updateFields, fieldNames, constructor);
    }

    record DataClassCache(
            String tableName,
            boolean autoCreate,
            List<String> uniqueFields,
            Set<String> tempFields,
            Map<String, Field> fields,
            Map<String, Field> tableFields,
            Map<String, Field> updateFields,
            Map<String, String> fieldNames,
            Constructor<?> constructor
    ) { }
}
