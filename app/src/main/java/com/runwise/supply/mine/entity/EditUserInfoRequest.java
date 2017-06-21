package com.runwise.supply.mine.entity;

/**
 * Created by mychao on 2016/11/1.
 */

public class EditUserInfoRequest {
    private String uid;//	int	Y		用户类型
    private String nickname;//	varchar	N		姓名
    private String headsmall;//	varchar	N		头像URL地址
    private String age;//	int	N		年龄
    private String sex;//	int	N		性别 0 未填写 1男 2女
    private String province_id;//	int	N		省份ID
    private String jigou_type;//	int	N		机构类型 机构类型 0 未填写1 鉴定评估机构 2 检测机构 3 修复机构 4 咨询机构 （默认0）
    private String lingyu_id;//	int	N		专业领域ID
    private String sign;//
    private String phone;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadsmall() {
        return headsmall;
    }

    public void setHeadsmall(String headsmall) {
        this.headsmall = headsmall;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProvince_id() {
        return province_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public String getJigou_type() {
        return jigou_type;
    }

    public void setJigou_type(String jigou_type) {
        this.jigou_type = jigou_type;
    }

    public String getLingyu_id() {
        return lingyu_id;
    }

    public void setLingyu_id(String lingyu_id) {
        this.lingyu_id = lingyu_id;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
