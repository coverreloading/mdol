package cn.mdol.controller;

import bsh.StringUtil;
import cn.mdol.po.Note;
import cn.mdol.po.ResponResult;
import cn.mdol.service.NoteService;
import cn.mdol.utils.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Vincent on 16/10/17.
 */
@Controller
@RequestMapping("note")
public class NoteCtrl {

    @Autowired
    private NoteService noteService;

    @RequestMapping("/addNote")
    public ResponResult addNote(String token, Note note) {
        try {
            if (StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(note.getName()) && StringUtils.isNotEmpty(note.getData())){
                ResponResult responResult = noteService.addNote(token);
                return responResult;
            }
            else {
                return ResponResult.build(400,"session过期,重新登录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponResult.build(500, ExceptionUtil.getStackTrace(e));
        }
    }

    @RequestMapping("/updateNOte")
    public ResponResult updateNote() {
        return null;
        //TODO
    }

    @RequestMapping("/delNOte")
    public ResponResult delNote() {
        return null;
        //TODO
    }

}
