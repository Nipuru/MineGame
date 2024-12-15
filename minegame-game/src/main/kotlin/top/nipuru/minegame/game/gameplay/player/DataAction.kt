package top.nipuru.minegame.game.gameplay.player

class DataAction(val type: DataActionType, val data: Any, val fields: Array<String>?)

enum class DataActionType {
    INSERT, UPDATE, DELETE
}
