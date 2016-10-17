package cn.mdol.po;

import java.util.Date;

public class Note {
    private Long id;

    private String name;

    private String data;

    private String create;

    private Date update;

    private Integer isshared;

    private Integer isdel;

    private Long userid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data == null ? null : data.trim();
    }

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create == null ? null : create.trim();
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }

    public Integer getIsshared() {
        return isshared;
    }

    public void setIsshared(Integer isshared) {
        this.isshared = isshared;
    }

    public Integer getIsdel() {
        return isdel;
    }

    public void setIsdel(Integer isdel) {
        this.isdel = isdel;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }
}