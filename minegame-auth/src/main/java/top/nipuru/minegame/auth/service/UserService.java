package top.nipuru.minegame.auth.service;


import top.nipuru.minegame.auth.domain.entity.User;

/**
 * 用户 服务层接口
 *
 * @author Nipuru
 * @since 2023/09/25 17:21
 */
public interface UserService {

    /**
     * 通过uuid获取用户
     *
     * @param uniqueId 用户uuid
     * @return 用户
     */
    User getUserByUniqueId(String uniqueId);

    /**
     * 初始化一个用户 每次用户进服都应该调用一次 包含新建账户 更新登录ip等操作
     *
     * @param uniqueId 用户uuid
     * @param lastIp 登录ip
     */
    User initUser(String uniqueId, String lastIp);

}
