package cn.mdol.service.impl;

import cn.mdol.mapper.UserMapper;
import cn.mdol.po.JedisClient;
import cn.mdol.po.ResponResult;
import cn.mdol.po.User;
import cn.mdol.po.UserExample;
import cn.mdol.service.UserService;
import cn.mdol.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Vincent on 16/10/13.
 */
@Service("UserService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JedisClient jedisClient;
    // 用户session在redis中保存的key
    @Value("${REDIS_USER_SESSION_KEY}")
    private String REDIS_USER_SESSION_KEY;
    //  Session的过期时间
    @Value("${SSO_SESSION_EXPIRE}")
    private Integer SSO_SESSION_EXPIRE;

    public ResponResult checkData(String param, Integer type) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (type == 1) {
            criteria.andNameEqualTo(param);
        }
        if (type == 2) {
            criteria.andEmailEqualTo(param);
        }
        List<User> list = userMapper.selectByExample(userExample);
        if (list == null || list.size() == 0) {
            return ResponResult.ok(true);
        }
        return ResponResult.ok(false);
    }

    @Override
    public ResponResult addUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setJoindate(new Date());
        userMapper.insert(user);
        return ResponResult.ok();
    }

    @Override
    public ResponResult login(String email, String password) {

        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andEmailEqualTo(email);
        List<User> list = userMapper.selectByExample(userExample);

        if (list == null || list.size() == 0) {
            return ResponResult.build(500, "用户名或密码错了");
        }
        User user = list.get(0);
        String mdpsw = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!mdpsw.equals(user.getPassword())){
            return ResponResult.build(500, "用户名或密码错了");
        }
        user.setPassword(null);
        String token = UUID.randomUUID().toString();
        // 保存token对应用户数据到redis
        jedisClient.set(REDIS_USER_SESSION_KEY+":"+token, JsonUtils.objectToJson(user));
        // 设置过期时间
        jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token,SSO_SESSION_EXPIRE);

        return ResponResult.ok(token);
    }
    // 根据token从redis获取用户信息
    @Override
    public ResponResult getUserByToken(String token) {
        String json = jedisClient.get(REDIS_USER_SESSION_KEY+":"+token);
        if(StringUtils.isBlank(json)){
            return ResponResult.build(400,"session已过期,请重新登录");
        }
        jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token,SSO_SESSION_EXPIRE);
        return ResponResult.ok(json);
    }
}
