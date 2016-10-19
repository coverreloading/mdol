package cn.mdol.service.impl;

import cn.mdol.mapper.NoteMapper;
import cn.mdol.po.*;
import cn.mdol.service.NoteService;
import cn.mdol.service.UserService;
import cn.mdol.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 16/10/17.
 */
@Service("NoteService")
public class NoteServiceImpl implements NoteService {

    @Autowired
    private UserService userService;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private JedisClient jedisClient;
    // NOTEsession在redis中保存的key
    @Value("${REDIS_USER_SESSION_KEY}")
    private String REDIS_USER_SESSION_KEY;
    // NOTEsession在redis中保存的key
    @Value("${REDIS_NOTE_SESSION_KEY}")
    private String REDIS_NOTE_SESSION_KEY;
    //  Session的过期时间
    @Value("${SSO_SESSION_EXPIRE}")
    private Integer SSO_SESSION_EXPIRE;
    @Value("${DEFAULT_NOTE_CONTENT}")
    private String DEFAULT_NOTE_CONTENT;


    @Override
    public ResponResult addNote(String token) {
        Long userId = userService.getUserIdByToken(token);
        if (userId == -1) {
            return ResponResult.build(401, "session已过期,请重新登录");
        }
        Note note = new Note();
        note.setUserid(userId);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
        note.setName(format.format(new Date()));
        note.setCreate(new Date().toString());// TODO 日期格式不对
        note.setUpdate(new Date());
        note.setData(DEFAULT_NOTE_CONTENT);

        noteMapper.insertAndGetId(note);

        // 存入redis
        jedisClient.set(REDIS_USER_SESSION_KEY + ":" + token + ":" + note.getId(), JsonUtils.objectToJson(note));
        // 更新redis时间
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token + ":" + note.getId(), SSO_SESSION_EXPIRE);
        return ResponResult.ok(note);
    }


    @Override
    public ResponResult getNote(String token, Long noteId) {
        // token过期判定
        Long userId = userService.getUserIdByToken(token);
        if (userId == -1) {
            return ResponResult.build(401, "session已过期,请重新登录");
        }
        Note note = new Note();
        // 1. redis查询token获取userid
        String json = jedisClient.get(REDIS_USER_SESSION_KEY + ":" + token + ":" + noteId);
        // 非空判定
        if (org.apache.commons.lang3.StringUtils.isNotBlank(json)) {
            note = JsonUtils.jsonToPojo(json, Note.class);
        }else{
            // 2. 直接查数据库
            note = noteMapper.selectByPrimaryKey(noteId);
        }
        // token对应用户id与笔记的userid不一致
        if (note.getUserid() != userId) {
            return ResponResult.build(400, "又一个搞事的");
        }
        // 直接查数据库需要更新redis
        jedisClient.set(REDIS_USER_SESSION_KEY + ":" + token + ":" + noteId, JsonUtils.objectToJson(note));

        // 更新session时效
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token + ":" + noteId, SSO_SESSION_EXPIRE);
        return ResponResult.ok(note);
    }

    @Override
    public ResponResult updateNote(String token, Note note) {
        // 接受token查询userId结果
        Long userId = userService.getUserIdByToken(token);
        if (userId == -1) {
            return ResponResult.build(401, "session已过期,请重新登录");
        }
        // 用户token与笔记userid对应不上
        if (note.getUserid() != userId) {
            return ResponResult.build(401, "session已过期,请重新登录");
        }

        Note noteFromDB = noteMapper.selectByPrimaryKey(note.getId());

        String[] text = note.getData().split("\n");
        noteFromDB.setName(text[0]);
        noteFromDB.setUpdate(new Date());
        noteFromDB.setData(note.getData());

        noteMapper.updateByPrimaryKey(noteFromDB);
        // 存入redis
        jedisClient.set(REDIS_USER_SESSION_KEY + ":" + token + ":" + note.getId(), JsonUtils.objectToJson(noteFromDB));
        // 更新redis时间
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token + ":" + note.getId(), SSO_SESSION_EXPIRE);
        return ResponResult.ok();
    }

    @Override
    public ResponResult getAllNote(String token, int getFromRedis) {
        Long userId = userService.getUserIdByToken(token);
        if (userId == -1) {
            return ResponResult.build(401, "session已过期,请重新登录");
        }
        List<Note> list = new ArrayList<>();
        // redis 中获取
        if (getFromRedis == 1) {
            List<Long> ids = noteMapper.selectIdListByUserId(userId);
            for (Long id : ids) {
                list.add(JsonUtils.jsonToPojo(jedisClient.get(REDIS_USER_SESSION_KEY + ":" + token + ":" + id), Note.class));
            }
        } else if (getFromRedis == 0) {
            // NoteExample 查询所有userid==userid的笔记
            NoteExample example = new NoteExample();
            NoteExample.Criteria criteria = example.createCriteria();
            criteria.andUseridEqualTo(userId);
            list = noteMapper.selectByExample(example);
            if (list == null || list.size() == 0) {
                return ResponResult.build(500, "没有笔记");
            }
            for (Note note : list) {
                // 存入redis
                jedisClient.set(REDIS_USER_SESSION_KEY + ":" + token + ":" + note.getId(), JsonUtils.objectToJson(note));
                // 更新redis时间
                jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token + ":" + note.getId(), SSO_SESSION_EXPIRE);
            }
        }
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
        return ResponResult.ok(list);
    }

    @Override
    public ResponResult del(String token, Long noteId) {
        // token过期判定
        Long userId = userService.getUserIdByToken(token);
        if (userId == -1) {
            return ResponResult.build(401, "session已过期,请重新登录");
        }
        Note note = new Note();
        // 1. redis查询token获取userid
        String json = jedisClient.get(REDIS_USER_SESSION_KEY + ":" + token + ":" + noteId);
        // 非空判定
        if (org.apache.commons.lang3.StringUtils.isNotBlank(json)) {
            note = JsonUtils.jsonToPojo(json, Note.class);
        }else{
            // 2. 直接查数据库
            note = noteMapper.selectByPrimaryKey(noteId);
        }
        // token对应用户id与笔记的userid不一致
        if (note.getUserid() != userId) {
            return ResponResult.build(400, "又一个搞事的");
        }
        noteMapper.deleteByPrimaryKey(noteId);
        // 直接查数据库需要更新redis
        jedisClient.del(REDIS_USER_SESSION_KEY + ":" + token + ":" + noteId);

        // 更新session时效
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token + ":" + noteId, SSO_SESSION_EXPIRE);
        return ResponResult.ok(note);
    }
}
