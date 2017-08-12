package com.stephen.bangbang.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FriendsResponse extends BaseResponse {
    @JsonProperty(value = "friends_info")
    List<SingleFriendInfo> friendsInfo;

    public FriendsResponse(List<SingleFriendInfo> friendsInfo) {
        this.friendsInfo = friendsInfo;
    }

    public List<SingleFriendInfo> getFriendsInfo() {
        return friendsInfo;
    }

    public void setFriendsInfo(List<SingleFriendInfo> friendsInfo) {
        this.friendsInfo = friendsInfo;
    }
}
