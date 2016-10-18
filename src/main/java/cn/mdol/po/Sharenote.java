package cn.mdol.po;

import java.util.Date;

public class Sharenote {
    private Long id;

    private Long noteid;

    private Date createdate;

    private Date sharedate;

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getNoteid() {
        return noteid;
    }

    public void setNoteid(Long noteid) {
        this.noteid = noteid;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public Date getSharedate() {
        return sharedate;
    }

    public void setSharedate(Date sharedate) {
        this.sharedate = sharedate;
    }

    @Override
    public String toString() {
        return "Sharenote{" +
                "id=" + id +
                ", noteid=" + noteid +
                ", createdate=" + createdate +
                ", sharedate=" + sharedate +
                '}';
    }
}