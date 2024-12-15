package top.nipuru.minegame.auth.datasource

import javax.sql.DataSource

interface DataSourceProvider {
    fun init(host: String, port: Int, database: String, username: String, password: String)

    fun shutdown()

    val dataSource: DataSource
}
