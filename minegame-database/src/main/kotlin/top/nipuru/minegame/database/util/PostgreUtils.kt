package top.nipuru.minegame.database.util

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.*

@Suppress("UNCHECKED_CAST")
fun ResultSet.getObject(clazz: Class<*>, index: Int): Any {
    when (clazz) {
        Int::class.javaPrimitiveType -> {
            return this.getInt(index)
        }
        Long::class.javaPrimitiveType -> {
            return this.getLong(index)
        }
        Float::class.javaPrimitiveType -> {
            return this.getFloat(index)
        }
        Double::class.javaPrimitiveType -> {
            return this.getDouble(index)
        }
        Boolean::class.javaPrimitiveType -> {
            return this.getBoolean(index)
        }
        String::class.java -> {
            return this.getString(index)
        }
        ByteArray::class.java -> {
            return this.getBytes(index)
        }
        Array<String>::class.java -> {
            return this.getArray(index).array
        }
        IntArray::class.java -> {
            val array = this.getArray(index).array as Array<Int>
            val ints = IntArray(array.size)
            for (i in array.indices) {
                ints[i] = array[i]
            }
            return ints
        }
        LongArray::class.java -> {
            val array = this.getArray(index).array as Array<Long>
            val longs = LongArray(array.size)
            for (i in array.indices) {
                longs[i] = array[i]
            }
            return longs
        }
        FloatArray::class.java -> {
            val array = this.getArray(index).array as Array<Float>
            val floats = FloatArray(array.size)
            for (i in array.indices) {
                floats[i] = array[i]
            }
            return floats
        }
        DoubleArray::class.java -> {
            val array = this.getArray(index).array as Array<Double>
            val doubles = DoubleArray(array.size)
            for (i in array.indices) {
                doubles[i] = array[i]
            }
            return doubles
        }
        BooleanArray::class.java -> {
            val array = this.getArray(index).array as Array<Boolean>
            val booleans = BooleanArray(array.size)
            for (i in array.indices) {
                booleans[i] = array[i]
            }
            return booleans
        }
        else -> throw IllegalArgumentException("Unsupported type: $clazz")
    }
}


fun PreparedStatement.setObject(con: Connection, index: Int, obj: Any) {
    when (val clazz = obj.javaClass) {
        Int::class.java -> {
            this.setInt(index, obj as Int)
        }
        Long::class.java -> {
            this.setLong(index, obj as Long)
        }
        Float::class.java -> {
            this.setFloat(index, obj as Float)
        }
        Double::class.java -> {
            this.setDouble(index, obj as Double)
        }
        Boolean::class.java -> {
            this.setBoolean(index, obj as Boolean)
        }
        String::class.java -> {
            this.setString(index, obj as String)
        }
        ByteArray::class.java -> {
            this.setBytes(index, obj as ByteArray)
        }
        Array<String>::class.java -> {
            this.setObject(index, obj)
        }
        IntArray::class.java -> {
            val ints = obj as IntArray
            val data = arrayOfNulls<Int>(ints.size)
            for (i in ints.indices) {
                data[i] = ints[i]
            }
            this.setArray(index, con.createArrayOf("INTEGER", data))
        }
        LongArray::class.java -> {
            val longs = obj as LongArray
            val data = arrayOfNulls<Long>(longs.size)
            for (i in longs.indices) {
                data[i] = longs[i]
            }
            this.setArray(index, con.createArrayOf("BIGINT", data))
        }
        FloatArray::class.java -> {
            val floats = obj as FloatArray
            val data = arrayOfNulls<Float>(floats.size)
            for (i in floats.indices) {
                data[i] = floats[i]
            }
            this.setArray(index, con.createArrayOf("REAL", data))
        }
        DoubleArray::class.java -> {
            val doubles = obj as DoubleArray
            val data = arrayOfNulls<Double>(doubles.size)
            for (i in doubles.indices) {
                data[i] = doubles[i]
            }
            this.setArray(index, con.createArrayOf("DOUBLE PRECISION", data))
        }
        BooleanArray::class.java -> {
            val booleans = obj as BooleanArray
            val data = arrayOfNulls<Boolean>(booleans.size)
            for (i in booleans.indices) {
                data[i] = booleans[i]
            }
            this.setArray(index, con.createArrayOf("BOOLEAN", data))
        }
        else -> {
            throw IllegalArgumentException("Unsupported type: $clazz")
        }
    }
}

fun Class<*>.getSqlType(): String {
    return if (this == String::class.java) {
        "TEXT"
    } else if (this == Int::class.java || this == Int::class.javaPrimitiveType) {
        "INTEGER"
    } else if (this == Long::class.java || this == Long::class.javaPrimitiveType) {
        "BIGINT"
    } else if (this == Boolean::class.java || this == Boolean::class.javaPrimitiveType) {
        "BOOLEAN"
    } else if (this == Double::class.java || this == Double::class.javaPrimitiveType) {
        "DOUBLE PRECISION"
    } else if (this == Float::class.java || this == Float::class.javaPrimitiveType) {
        "REAL"
    } else if (this == ByteArray::class.java) {
        "BYTEA"
    } else if (this.isArray) {
        this.componentType.getSqlType() + "[]"
    } else {
        throw IllegalArgumentException("Unsupported type: " + this.simpleName)
    }
}

fun String.getSqlName(): String {
    return "\"" + this
        .replace("([a-z])([A-Z])".toRegex(), "$1_$2") // 在小写字母和大写字母之间添加下划线
        .lowercase(Locale.getDefault()) + "\""
}
