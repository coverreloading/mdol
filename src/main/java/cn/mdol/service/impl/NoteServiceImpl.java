package cn.mdol.service.impl;

import cn.mdol.mapper.NoteMapper;
import cn.mdol.mapper.UserMapper;
import cn.mdol.po.*;
import cn.mdol.service.NoteService;
import cn.mdol.service.UserService;
import cn.mdol.utils.ExceptionUtil;
import cn.mdol.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
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
        if (userId==-1){
            return ResponResult.build(401,"session已过期,请重新登录");
        }
        Note note = new Note();
        note.setUserid(userId);
        note.setName(new Date().toString());
        note.setCreate(new Date().toString());// TODO 日期格式不对
        note.setUpdate(new Date());
        note.setData(DEFAULT_NOTE_CONTENT);

        noteMapper.insertAndGetId(note);

        // 存入redis
        jedisClient.set(REDIS_USER_SESSION_KEY+":"+token+":"+note.getId(),JsonUtils.objectToJson(note));
        // 更新redis时间
        jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token,SSO_SESSION_EXPIRE);
        jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token+":"+note.getId(),SSO_SESSION_EXPIRE);
        return ResponResult.ok(note);
    }


    @Override
    public ResponResult getNote(String token, Long noteId) {
        // token过期判定
        Long userId = userService.getUserIdByToken(token);
        if (userId==-1){
            return ResponResult.build(401,"session已过期,请重新登录");
        }
        // redis查询token获取userid
        String json = jedisClient.get(REDIS_USER_SESSION_KEY+":"+token+":"+noteId);
        // 非空判定
        if(StringUtils.isBlank(json)){
            return ResponResult.build(400, "查无笔记");
        }
        Note note = JsonUtils.jsonToPojo(json,Note.class);
        // token对应用户id与笔记的userid不一致
        if(note.getUserid()!=userId) {
            return ResponResult.build(400, "又一个搞事的");
        }
        // 更新session时效
        jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token,SSO_SESSION_EXPIRE);
        jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token+":"+noteId,SSO_SESSION_EXPIRE);
        return ResponResult.ok(note);
    }


    @Override
    public ResponResult updateNote(String token, Note note) {
        // 接受token查询userId结果
        Long userId= userService.getUserIdByToken(token);
        if(userId==-1){
            return ResponResult.build(401,"session已过期,请重新登录");
        }
        // 用户token与笔记userid对应不上
        if(note.getUserid()!=userId){
            return ResponResult.build(401,"session已过期,请重新登录");
        }
        note.setUpdate(new Date());
        noteMapper.updateByPrimaryKey(note);
        // 存入redis
        jedisClient.set(REDIS_USER_SESSION_KEY+":"+token+":"+note.getId(),JsonUtils.objectToJson(note));
        // 更新redis时间
        jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token,SSO_SESSION_EXPIRE);
        jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token+":"+note.getId(),SSO_SESSION_EXPIRE);
        return ResponResult.ok();
    }

    @Override
    public ResponResult getAllNote(String token) {
        Long userId = userService.getUserIdByToken(token);
        if (userId==-1){
            return ResponResult.build(401,"session已过期,请重新登录");
        }
        // NoteExample 查询所有userid==userid的笔记
        NoteExample example = new NoteExample();
        NoteExample.Criteria criteria = example.createCriteria();
        criteria.andUseridEqualTo(userId);
        List<Note> list = noteMapper.selectByExample(example);
        if (list == null || list.size() == 0) {
            return ResponResult.build(500, "没有笔记");
        }
        for (Note note :list){
            // 存入redis
            jedisClient.set(REDIS_USER_SESSION_KEY+":"+token+":"+note.getId(),JsonUtils.objectToJson(note));
            // 更新redis时间
            jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token+":"+note.getId(),SSO_SESSION_EXPIRE);
        }
        jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token,SSO_SESSION_EXPIRE);
        return ResponResult.ok(list);
    }
}
