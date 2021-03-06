package com.app.manager.model.payload.request;

import com.app.manager.entity.User;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class UserProfileRequest {
    @NotNull private String fullname = ""; //search
    @NotNull private String phone = "";
    @NotNull private String address = "";
    @NotNull private String civil_id = "";
    @NotNull @Min(0L) private Long birthday = 0L;
    @NotNull private User.GenderEnum gender
            = User.GenderEnum.UNKNOWN;

    public UserProfileRequest() {
    }

    public UserProfileRequest(@NotNull String fullname, @NotNull String phone,
                              @NotNull String address, @NotNull String civil_id,
                              @NotNull @Min(0L) Long birthday,
                              @NotNull User.GenderEnum gender) {
        this.fullname = fullname;
        this.phone = phone;
        this.address = address;
        this.civil_id = civil_id;
        this.birthday = birthday;
        this.gender = gender;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCivil_id() {
        return civil_id;
    }

    public void setCivil_id(String civil_id) {
        this.civil_id = civil_id;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public User.GenderEnum getGender() {
        return gender;
    }

    public void setGender(User.GenderEnum gender) {
        this.gender = gender;
    }
}
