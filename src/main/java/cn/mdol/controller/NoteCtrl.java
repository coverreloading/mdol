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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Vincent on 16/10/17.
 */
@Controller
@RequestMapping("note")
public class NoteCtrl {

    @Autowired
    private NoteService noteService;

    @RequestMapping(value = "/defaultShow")
    @ResponseBody
    public ResponResult defaultShow(){
        return ResponResult.build(200,"# 能看就行");
    }

    @RequestMapping(value = "/addNote" ,method = RequestMethod.POST)
    @ResponseBody
    public ResponResult addNote(String token) {
        try {
            if (StringUtils.isNotEmpty(token)){
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

    @RequestMapping(value = "/updateNote",method = RequestMethod.POST)
    @ResponseBody
    public ResponResult updateNote(String token,Note note) {
        try {
            if (StringUtils.isNotEmpty(token)&&note.getId()!=null&&note.getUserid()!=null){
                ResponResult responResult = noteService.updateNote(token,note);
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

    @RequestMapping(value = "/getNote",method = RequestMethod.POST)
    @ResponseBody
    public ResponResult getNote(String token, Long noteId) {
        try {
            if (StringUtils.isNotEmpty(token)&&noteId!=null){
                ResponResult responResult = noteService.getNote(token,noteId);
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
    @RequestMapping(value = "/getAllNote",method = RequestMethod.POST)
    @ResponseBody
    public ResponResult getAllNote(String token) {
        try {
            if (StringUtils.isNotEmpty(token)){
                ResponResult responResult = noteService.getAllNote(token);
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
    @RequestMapping(value = "/delNote",method = RequestMethod.POST)
    @ResponseBody
    public ResponResult delNote() {
        return null;
        //TODO
    }

}
