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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            //Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
            //Matcher matcher = pattern.matcher("param");
            //System.out.println(matcher.matches());
        }
        List<User> list = userMapper.selectByExample(userExample);
        if (list == null || list.size() == 0) {
            return ResponResult.ok(true);
        }
        return ResponResult.build(400,"已被占用");
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
    public ResponResult getLogin(String email, String password) {

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
    // 根据token从redis获取用户信息,返回用户json
    @Override
    public ResponResult getUserByToken(String token) {
        String json = jedisClient.get(REDIS_USER_SESSION_KEY+":"+token);
        if(StringUtils.isBlank(json)){
            return ResponResult.build(401,"session已过期,请重新登录");
        }
        jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token,SSO_SESSION_EXPIRE);
        return ResponResult.ok(json);
    }

    // 根据token查询jedis中用户id,返回-1代表session过期
    @Override
    public Long getUserIdByToken(String token){
        String json = jedisClient.get(REDIS_USER_SESSION_KEY+":"+token);
        if(StringUtils.isBlank(json)){
            return Long.valueOf(-1);
        }
        jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token,SSO_SESSION_EXPIRE);
        User user = JsonUtils.jsonToPojo(json,User.class);
        //System.out.println(user.toString());
        return user.getId();
    }
}
