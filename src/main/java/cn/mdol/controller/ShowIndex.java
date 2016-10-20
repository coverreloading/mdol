package cn.mdol.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by loadi on 2016/9/15.
 */
@Controller
public class ShowIndex {
    @RequestMapping("login")
    public String login(){
        return "login";
    }
    @RequestMapping({"/index","/"})
    public String showIndex(){
        return "main";
    }
    @RequestMapping("/main")
    public String showMain(){
        return "main";
    }
    //@RequestMapping("/main2")
    //public String showMain2(){
    //    return "main2";
    //}
    //@RequestMapping("/mdpage.html")
    //public String showmdpage(){
    //    return "mdpage";
    //}
    @RequestMapping("/register")
    public String showRegister(){
        return "register";
    }
}