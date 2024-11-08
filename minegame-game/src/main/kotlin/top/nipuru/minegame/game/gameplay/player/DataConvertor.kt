package top.nipuru.minegame.game.gameplay.player

import top.nipuru.minegame.common.message.database.FieldMessage
import top.nipuru.minegame.common.message.database.QueryPlayerRequest
import top.nipuru.minegame.common.message.database.TableInfo
import java.io.IOException
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import kotlin.reflect.KProperty1

internal object DataConvertor {

    private val cache = mutableMapOf<Class<*>, DataClassCache>()

    fun preload(request: QueryPlayerRequest,dataClass: Class<*>) {
        val dataClassCache = getOrCache(dataClass)
        val fields = mutableMapOf<String, Class<*>>()
        dataClassCache.tableFields.forEach{ fields[it.key] = it.value.type }
        val tableInfo = TableInfo(dataClassCache.tableName, dataClassCache.autoCreate, fields, dataClassCache.uniqueFields)
        request.tables.add(tableInfo)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> unpack(tables: Map<String, List<List<FieldMessage>>>, dataClass: Class<T>): T? {
        val dataClassCache = getOrCache(dataClass)
        val instance = dataClassCache.constructor.newInstance() as T
        val fieldMessagesList = tables[dataClassCache.tableName]
        if (fieldMessagesList.isNullOrEmpty()) {
            return null
        }
        if (fieldMessagesList.size > 1) {
            throw IOException("Too many results for " + dataClass.name)
        }
        for (fieldMessage in fieldMessagesList[0]) {
            val field = dataClassCache.fields[fieldMessage.name] ?: continue
            field[instance] = fieldMessage.value
        }
        return instance
    }

    fun <T : Any> getProperty(properties: Array<out KProperty1<T, *>>): Array<String> {
        return properties.map { it.name }.toTypedArray()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> unpackList(tables: Map<String, List<List<FieldMessage>>>, dataClass: Class<T>): List<T> {
        val dataClassCache = getOrCache(dataClass)
        val fieldMessagesList = tables[dataClassCache.tableName]
        if (fieldMessagesList.isNullOrEmpty()) {
            return emptyList()
        }
        val result: MutableList<T> = ArrayList(fieldMessagesList.size)
        for (fieldMessages in fieldMessagesList) {
            val instance = dataClassCache.constructor.newInstance() as T
            for (fieldMessage in fieldMessages) {
                val field = dataClassCache.fields[fieldMessage.name] ?: continue
                field[instance] = fieldMessage.value
            }
            result.add(instance)
        }
        return result
    }

    fun pack(tables: MutableMap<String, MutableList<List<FieldMessage>>>, data: Any) {
        val dataClassCache = getOrCache(data.javaClass)
        val fieldMessagesList = tables.getOrPut(dataClassCache.tableName) { mutableListOf() }
        val fieldMessages = mutableListOf<FieldMessage>()
        for ((key, value) in dataClassCache.fields) {
            val fieldMessage = FieldMessage(key, value[data])
            fieldMessages.add(fieldMessage)
        }
        fieldMessagesList.add(fieldMessages)
    }

    fun getOrCache(dataClass: Class<*>): DataClassCache {
        var dataClassCache = cache[dataClass]
        if (dataClassCache == null) {
            dataClassCache = createCache(dataClass)
            cache[dataClass] = dataClassCache
        }
        return dataClassCache
    }

    private fun createCache(dataClass: Class<*>): DataClassCache {
        if (!dataClass.isAnnotationPresent(Table::class.java)) {
            throw Exception("dataClass must be annotated with @Table, provided: " + dataClass.name)
        }
        val constructor: Constructor<*>
        try {
            constructor = dataClass.getDeclaredConstructor()
            constructor.setAccessible(true)
        } catch (e: NoSuchMethodException) {
            throw Exception("dataClass must have default constructor, provided: " + dataClass.name)
        }
        val table = dataClass.getAnnotation(
            Table::class.java
        )
        val tableName = table.name
        val autoCreate = table.autoCreate
        val uniqueFields: MutableList<String> = ArrayList() // 被 @Unique 注释的字段
        val tempFields: MutableSet<String> = HashSet() // 被 @Temp 注释的字段
        val fields: MutableMap<String, Field> = LinkedHashMap() // 所有字段
        val tableFields: MutableMap<String, Field> = LinkedHashMap() // 除 @Temp 之外的字段
        val updateFields: MutableMap<String, Field> = LinkedHashMap() // 包含在 tableFields 中的非 Unique 字段
        val fieldNames: MutableMap<String, String> = LinkedHashMap()
        for (field in dataClass.declaredFields) {
            field.isAccessible = true
            var name = field.name
            if (field.isAnnotationPresent(Alias::class.java)) {
                name = field.getAnnotation(Alias::class.java).name
            }
            if (field.isAnnotationPresent(Unique::class.java)) {
                uniqueFields.add(name)
            } else {
                updateFields[name] = field
            }
            if (field.isAnnotationPresent(Temp::class.java)) {
                tempFields.add(name)
            } else {
                tableFields[name] = field
            }
            fields[name] = field
            fieldNames[field.name] = name
        }
        return DataClassCache(
            tableName,
            autoCreate,
            uniqueFields,
            tempFields,
            fields,
            tableFields,
            updateFields,
            fieldNames,
            constructor
        )
    }

    data class DataClassCache(
        val tableName: String,
        val autoCreate: Boolean,
        val uniqueFields: List<String>,
        val tempFields: Set<String>,
        val fields: Map<String, Field>,
        val tableFields: Map<String, Field>,
        val updateFields: Map<String, Field>,
        val fieldNames: Map<String, String>,
        val constructor: Constructor<*>
    )
}
