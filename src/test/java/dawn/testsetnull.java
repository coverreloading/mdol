package dawn;

import cn.mdol.mapper.NoteMapper;
import cn.mdol.mapper.SharenoteMapper;
import cn.mdol.mapper.UserMapper;
import cn.mdol.po.*;
import cn.mdol.po.UserExample.Criteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-dao.xml"})
public class testsetnull {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private SharenoteMapper sharenoteMapper;

    @Test
    public void insert() {
        User user = new User();
        user.setName("test");
        userMapper.insert(user);
        ResponResult responsult = ResponResult.ok(user);
        System.out.println(responsult.getData().toString());
    }

    @Test
    public void update() {
        UserExample userExample = new UserExample();
        Criteria criteria = userExample.createCriteria();
        criteria.andNameEqualTo("test");
        List<User> list = userMapper.selectByExample(userExample);
        User usertemp = list.get(0);
        usertemp.setName(null);
        userMapper.updateByPrimaryKey(usertemp);
    }

    @Test
    public void selectusertest() {
        String email = "123";
        String password1 = "2,";

        System.out.println(DigestUtils.md5DigestAsHex(password1.getBytes()));
        //
        //UserExample userExample = new UserExample();
        //UserExample.Criteria criteria = userExample.createCriteria();
        //criteria.andEmailEqualTo(email);
        //criteria.andPasswordEqualTo(DigestUtils.md5DigestAsHex(password.getBytes()));
        //List<User> list = userMapper.selectByExample(userExample);
        //
        //if (list == null || list.size() == 0) {
        //    return ResponResult.build(500, "用户名或密码错了");
    }
    @Test
    public void insertReturnID(){
        Note note = new Note();
        note.setName("aaa");
        noteMapper.insertAndGetId(note);
        System.out.println(note.getId());
    }
    @Test
    public void shareNoteTest(){
        Sharenote sharenote = new Sharenote();
        sharenote.setNoteid(Long.valueOf(1));
        sharenote.setSharedate(new Date());
        sharenoteMapper.insert(sharenote);
    }
}

