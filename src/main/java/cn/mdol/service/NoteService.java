package cn.mdol.service;

import cn.mdol.po.Note;
import cn.mdol.po.ResponResult;

/**
 * Created by Vincent on 16/10/13.
 */

public interface NoteService {
    ResponResult addNote(String token);
    ResponResult getNote(String token,Long noteId);
    ResponResult updateNote(String token,Note note);
    ResponResult getAllNote(String token);

}
