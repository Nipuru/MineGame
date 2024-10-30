package top.nipuru.minegame.common.message.shared;

import lombok.Getter;

@Getter
public class QueryPlayerInfoRequest {
    private final String name;

    public QueryPlayerInfoRequest(String name) {
        this.name = name;
    }
}
