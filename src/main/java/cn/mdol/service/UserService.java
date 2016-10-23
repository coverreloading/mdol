package cn.mdol.service;

import cn.mdol.po.ResponResult;
import cn.mdol.po.User;
import org.apache.http.HttpResponse;
import org.springframework.http.HttpRequest;

/**
 * Created by Vincent on 16/10/13.
 */

public interface UserService {
    ResponResult addUser(String email, String password);
    ResponResult checkData(String param, Integer type);
    ResponResult getLogin(String email, String password);
    ResponResult getUserByToken(String token);
    Long getUserIdByToken(String token);
}
