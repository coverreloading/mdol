package cn.mdol.controller;

import cn.mdol.po.Note;
import cn.mdol.po.ResponResult;
import cn.mdol.service.FileService;
import cn.mdol.service.NoteService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Created by Vincent on 16/10/21.
 */

@Controller
@RequestMapping(value = "/file")
public class FileCtrl {

    @Autowired
    private FileService fileService;

    // restful  type:html,md
    @RequestMapping(value = "download/{token}/{type}", method = RequestMethod.POST)
    public ResponseEntity<byte[]> download(HttpServletRequest request,
                                           @PathVariable(value = "token") String token,
                                           @PathVariable(value = "type") String type,
                                           String content)throws IOException {
        //TODO 未测试
        if(token!=null&&type!=null&&content!=null){
            String realPath = request.getSession().getServletContext().getRealPath("/WEB-INF/upload/temp");
            return fileService.downloadFile(token, type, content, realPath);
        }else {
            return null;
        }
    }
}

