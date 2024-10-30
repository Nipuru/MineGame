package top.nipuru.minegame.database.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgreUtils {

    public static Object getObject(ResultSet rs, int index, Class<?> clazz) throws SQLException {
        if (clazz == int.class) {
            return rs.getInt(index);
        } else if (clazz == long.class) {
            return rs.getLong(index);
        } else if (clazz == float.class) {
            return rs.getFloat(index);
        } else if (clazz == double.class) {
            return rs.getDouble(index);
        } else if (clazz == boolean.class) {
            return rs.getBoolean(index);
        } else if (clazz == String.class) {
            return rs.getString(index);
        } else if (clazz == byte[].class) {
            return rs.getBytes(index);
        } else if (clazz == String[].class) {
            return rs.getArray(index).getArray();
        } else if (clazz == int[].class) {
            Integer[] array = (Integer[]) rs.getArray(index).getArray();
            int[] ints = new int[array.length];
            for (int i = 0; i < array.length; i++) {
                ints[i] = array[i];
            }
            return ints;
        } else if (clazz == long[].class) {
            Long[] array = (Long[]) rs.getArray(index).getArray();
            long[] longs = new long[array.length];
            for (int i = 0; i < array.length; i++) {
                longs[i] = array[i];
            }
            return longs;
        } else if (clazz == float[].class) {
            Float[] array = (Float[]) rs.getArray(index).getArray();
            float[] floats = new float[array.length];
            for (int i = 0; i < array.length; i++) {
                floats[i] = array[i];
            }
            return floats;
        } else if (clazz == double[].class) {
            Double[] array = (Double[]) rs.getArray(index).getArray();
            double[] doubles = new double[array.length];
            for (int i = 0; i < array.length; i++) {
                doubles[i] = array[i];
            }
            return doubles;
        } else if (clazz == boolean[].class) {
            Boolean[] array = (Boolean[]) rs.getArray(index).getArray();
            boolean[] booleans = new boolean[array.length];
            for (int i = 0; i < array.length; i++) {
                booleans[i] = array[i];
            }
            return booleans;
        }
        throw new IllegalArgumentException("Unsupported type: " + clazz);
    }

    public static void setObject(Connection con, PreparedStatement ps, int index, Object object) throws SQLException {
        Class<?> clazz = object.getClass();
        if (clazz == Integer.class) {
            ps.setInt(index, (int) object);
        } else if (clazz == Long.class) {
            ps.setLong(index, (long) object);
        } else if (clazz == Float.class) {
            ps.setFloat(index, (float) object);
        } else if (clazz == Double.class) {
            ps.setDouble(index, (double) object);
        } else if (clazz == Boolean.class) {
            ps.setBoolean(index, (boolean) object);
        } else if (clazz == String.class) {
            ps.setString(index, (String) object);
        } else if (clazz == byte[].class) {
            ps.setBytes(index, (byte[]) object);
        } else if (clazz == String[].class) {
            ps.setObject(index, object);
        } else if (clazz == int[].class) {
            int[] ints = (int[]) object;
            Integer[] data = new Integer[ints.length];
            for (int i = 0; i < ints.length; ++i) {
                data[i] = ints[i];
            }
            ps.setArray(index, con.createArrayOf("INTEGER", data));
        } else if (clazz == long[].class) {
            long[] longs = (long[]) object;
            Long[] data = new Long[longs.length];
            for (int i = 0; i < longs.length; ++i) {
                data[i] = longs[i];
            }
            ps.setArray(index, con.createArrayOf("BIGINT", data));
        } else if (clazz == float[].class) {
            float[] floats = (float[]) object;
            Float[] data = new Float[floats.length];
            for (int i = 0; i < floats.length; ++i) {
                data[i] = floats[i];
            }
            ps.setArray(index, con.createArrayOf("REAL", data));
        } else if (clazz == double[].class) {
            double[] doubles = (double[]) object;
            Double[] data = new Double[doubles.length];
            for (int i = 0; i < doubles.length; ++i) {
                data[i] = doubles[i];
            }
            ps.setArray(index, con.createArrayOf("DOUBLE PRECISION", data));
        } else if (clazz == boolean[].class) {
            boolean[] booleans = (boolean[]) object;
            Boolean[] data = new Boolean[booleans.length];
            for (int i = 0; i < booleans.length; ++i) {
                data[i] = booleans[i];
            }
            ps.setArray(index, con.createArrayOf("BOOLEAN", data));
        } else {
            throw new IllegalArgumentException("Unsupported type: " + clazz);
        }
    }

    public static String getSqlType(Class<?> fieldType) {
        if (fieldType == String.class) {
            return "TEXT";
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return "INTEGER";
        } else if (fieldType == Long.class || fieldType == long.class) {
            return "BIGINT";
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return "BOOLEAN";
        } else if (fieldType == Double.class || fieldType == double.class) {
            return "DOUBLE PRECISION";
        } else if (fieldType == Float.class || fieldType == float.class) {
            return "REAL";
        } else if (fieldType == byte[].class) {
            return "BYTEA";
        } else if (fieldType.isArray()) {
            return getSqlType(fieldType.getComponentType()) + "[]";
        } else {
            throw new IllegalArgumentException("Unsupported type: " + fieldType.getSimpleName());
        }
    }

    public static String mapFieldName(String fieldName) {
        return "\"" + fieldName
                .replaceAll("([a-z])([A-Z])", "$1_$2")  // 在小写字母和大写字母之间添加下划线
                .toLowerCase() + "\"";
    }
}
