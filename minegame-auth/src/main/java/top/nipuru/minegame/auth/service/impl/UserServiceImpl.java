package top.nipuru.minegame.auth.service.impl;

import top.nipuru.minegame.auth.domain.entity.User;
import top.nipuru.minegame.auth.mapper.UserMapper;
import top.nipuru.minegame.auth.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户 服务层实现 (写得很狗屎)
 *
 * @author Nipuru
 * @since 2023/09/25 17:21
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;


    @Override
    public User getUserByUniqueId(String uniqueId) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(User::getUniqueId, uniqueId);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User initUser(String uniqueId, String lastIp) {
        User user = getUserByUniqueId(uniqueId);
        if (user == null) {
            user = new User();
            user.setUniqueId(uniqueId);
            user.setDbId(1);
            user.setLastIp(lastIp);
            userMapper.insert(user);
            return user;
        }
        user.setLastIp(lastIp);
        userMapper.updateById(user);
        return user;
    }
}
