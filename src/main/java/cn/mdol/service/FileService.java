package cn.mdol.service;

import cn.mdol.po.Note;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Vincent on 16/10/21.
 */
public interface FileService {
    ResponseEntity<byte[]> downloadFile(String token,String type,String content,String realPath);
}
