package top.nipuru.minegame.database.config

import org.yaml.snakeyaml.Yaml
import top.nipuru.minegame.common.util.ResourceUtil
import java.io.InputStreamReader


/**
 * @author Nipuru
 * @since 2024/11/08 12:38
 */

data class Config (
    var broker: Broker,
    var datasource: DataSource,
)

data class Broker (
    var host: String,
    var port: Int
)

data class DataSource (
    var host: String,
    var port: Int,
    var database: String,
    var username: String,
    var password: String,
)

fun loadConfig(): Config {
    ResourceUtil.getResourceOrExtract("config.yml").use { inputStream ->
        InputStreamReader(inputStream).use { reader ->
            val yaml = Yaml()
            return yaml.loadAs(reader, Config::class.java)
        }
    }
}