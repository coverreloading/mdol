package cn.mdol.service.impl;

import cn.mdol.mapper.NoteMapper;
import cn.mdol.mapper.UserMapper;
import cn.mdol.po.JedisClient;
import cn.mdol.po.Note;
import cn.mdol.po.ResponResult;
import cn.mdol.po.User;
import cn.mdol.service.NoteService;
import cn.mdol.service.UserService;
import cn.mdol.utils.ExceptionUtil;
import cn.mdol.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.Date;

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
    // 用户session在redis中保存的key
    @Value("${REDIS_USER_SESSION_KEY}")
    private String REDIS_USER_SESSION_KEY;
    //  Session的过期时间
    @Value("${SSO_SESSION_EXPIRE}")
    private Integer SSO_SESSION_EXPIRE;
    @Value("${DEFAULT_NOTE_CONTENT}")
    private String DEFAULT_NOTE_CONTENT;

    // TODO 还没测试
    @Override
    public ResponResult addNote(String token) {
        ResponResult result = null;
        try {
            result=userService.getUserByToken(token);
            if(result.getStatus()==400){
                // session已过期
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("添加笔记失败");
            return ResponResult.build(500, ExceptionUtil.getStackTrace(e));
        }

        User user = JsonUtils.jsonToPojo(String.valueOf(result.getData()),User.class);
        Note note = new Note();
        note.setUserid(user.getId());
        note.setName(new Date().toString());
        note.setCreate(new Date().toString());// TODO 日期格式不对
        note.setUpdate(new Date());
        note.setData(DEFAULT_NOTE_CONTENT);
        try {
            noteMapper.insert(note);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponResult.build(400,"新建笔记失败,请重试");
        }
        return ResponResult.ok(note);
    }

    @Override
    public ResponResult getNote(String token, Integer noteId) {
        return null;
        //TODO
    }

    //TODO 错
    @Override
    public ResponResult updateNote(String token, Integer noteId) {
        User user = JsonUtils.jsonToPojo(String.valueOf(userService.getUserByToken(token).getData()),User.class);
        Note note = new Note();
        note.setUserid(user.getId());
        // TODO
        System.out.println(note.toString());
        try {
            noteMapper.updateByPrimaryKey(note);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponResult.build(400,"保存笔记失败,请重试");
        }
        return ResponResult.ok();
    }


}
