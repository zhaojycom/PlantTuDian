package com.zhaojy.planttudian.bean;

/**
 * @author: zhaojy
 * @data:On 2018/9/28.
 */

public class User {
    private String phone;
    private String avatar;
    private String banner;

    public static User user;
    /**
     * 账户是否发生改变
     */
    private boolean userChange = false;

    public static User getInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    private User() {

    }

    /**
     * 复制
     *
     * @param res
     */
    public static void copy(User res) {
        getInstance();
        user.setPhone(res.getPhone());
        user.setAvatar(res.getAvatar());
        user.setBanner(res.getBanner());
        user.setUserChange(res.isUserChange());
    }

    /**
     * 重置
     */
    public static void reset() {
        getInstance();
        user.setPhone(null);
        user.setAvatar(null);
        user.setBanner(null);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public boolean isUserChange() {
        return userChange;
    }

    public void setUserChange(boolean userChange) {
        this.userChange = userChange;
    }
}
