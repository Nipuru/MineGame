package top.nipuru.minegame.auth.processor;

import top.nipuru.minegame.auth.domain.entity.User;
import top.nipuru.minegame.auth.service.UserService;
import top.nipuru.minegame.common.message.auth.QueryUserRequest;
import top.nipuru.minegame.common.processor.RequestDispatcher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class QueryUserHandler implements RequestDispatcher.Handler<QueryUserRequest> {


    @Resource
    private UserService userService;

    @Override
    public void handle(RequestDispatcher.ResponseSender responseSender, QueryUserRequest request) {
        User user = userService.initUser(request.getUniqueId().toString(), request.getIp());
        QueryUserRequest.UserMessage userMessage = new QueryUserRequest.UserMessage(user.getPlayerId(), user.getDbId());
        responseSender.sendResponse(userMessage);
    }

    @Override
    public Class<QueryUserRequest> interest() {
        return QueryUserRequest.class;
    }
}
