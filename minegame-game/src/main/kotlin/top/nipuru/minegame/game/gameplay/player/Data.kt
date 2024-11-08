package top.nipuru.minegame.game.gameplay.player

import top.nipuru.minegame.common.message.database.QueryPlayerRequest


interface Data

fun QueryPlayerRequest.preload(dataClass: Class<out Data>) {
    DataConvertor.preload(this, dataClass)
}

/**
 * 表示数据字段的别名
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Alias(val name: String)

/**
 * 表示一个数据类
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Table(
    /** 表名  */
    val name: String,
    /** 自动建表  */
    val autoCreate: Boolean = true
)

/**
 * 表示某个字段是临时数据，不存入数据库，但是会在服务器间传输
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Temp

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Unique

