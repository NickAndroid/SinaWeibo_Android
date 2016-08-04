/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sina.weibo.sdk.openapi.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户信息结构体。
 * 
 * @author SINA
 * @since 2013-11-24
 */
public class User implements Parcelable{

    /** 用户UID（int64） */
    public String id;
    /** 字符串型的用户 UID */
    public String idstr;
    /** 用户昵称 */
    public String screen_name;
    /** 友好显示名称 */
    public String name;
    /** 用户所在省级ID */
    public int province;
    /** 用户所在城市ID */
    public int city;
    /** 用户所在地 */
    public String location;
    /** 用户个人描述 */
    public String description;
    /** 用户博客地址 */
    public String url;
    /** 用户头像地址，50×50像素 */
    public String profile_image_url;
    /** 用户的微博统一URL地址 */
    public String profile_url;
    /** 用户的个性化域名 */
    public String domain;
    /** 用户的微号 */
    public String weihao;
    /** 性别，m：男、f：女、n：未知 */
    public String gender;
    /** 粉丝数 */
    public int followers_count;
    /** 关注数 */
    public int friends_count;
    /** 微博数 */
    public int statuses_count;
    /** 收藏数 */
    public int favourites_count;
    /** 用户创建（注册）时间 */
    public String created_at;
    /** 暂未支持 */
    public boolean following;
    /** 是否允许所有人给我发私信，true：是，false：否 */
    public boolean allow_all_act_msg;
    /** 是否允许标识用户的地理位置，true：是，false：否 */
    public boolean geo_enabled;
    /** 是否是微博认证用户，即加V用户，true：是，false：否 */
    public boolean verified;
    /** 暂未支持 */
    public int verified_type;
    /** 用户备注信息，只有在查询用户关系时才返回此字段 */
    public String remark;
    /** 用户的最近一条微博信息字段 */
    public Status status;
    /** 是否允许所有人对我的微博进行评论，true：是，false：否 */
    public boolean allow_all_comment;
    /** 用户大头像地址 */
    public String avatar_large;
    /** 用户高清大头像地址 */
    public String avatar_hd;
    /** 认证原因 */
    public String verified_reason;
    /** 该用户是否关注当前登录用户，true：是，false：否 */
    public boolean follow_me;
    /** 用户的在线状态，0：不在线、1：在线 */
    public int online_status;
    /** 用户的互粉数 */
    public int bi_followers_count;
    /** 用户当前的语言版本，zh-cn：简体中文，zh-tw：繁体中文，en：英语 */
    public String lang;
    
    /** 注意：以下字段暂时不清楚具体含义，OpenAPI 说明文档暂时没有同步更新对应字段 */
    public String star;
    public String mbtype;
    public String mbrank;
    public String block_word;

    public User() {
    }

    protected User(Parcel in) {
        id = in.readString();
        idstr = in.readString();
        screen_name = in.readString();
        name = in.readString();
        province = in.readInt();
        city = in.readInt();
        location = in.readString();
        description = in.readString();
        url = in.readString();
        profile_image_url = in.readString();
        profile_url = in.readString();
        domain = in.readString();
        weihao = in.readString();
        gender = in.readString();
        followers_count = in.readInt();
        friends_count = in.readInt();
        statuses_count = in.readInt();
        favourites_count = in.readInt();
        created_at = in.readString();
        following = in.readByte() != 0;
        allow_all_act_msg = in.readByte() != 0;
        geo_enabled = in.readByte() != 0;
        verified = in.readByte() != 0;
        verified_type = in.readInt();
        remark = in.readString();
        status = in.readParcelable(Status.class.getClassLoader());
        allow_all_comment = in.readByte() != 0;
        avatar_large = in.readString();
        avatar_hd = in.readString();
        verified_reason = in.readString();
        follow_me = in.readByte() != 0;
        online_status = in.readInt();
        bi_followers_count = in.readInt();
        lang = in.readString();
        star = in.readString();
        mbtype = in.readString();
        mbrank = in.readString();
        block_word = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static User parse(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return User.parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static User parse(JSONObject jsonObject) {
        if (null == jsonObject) {
            return null;
        }
        
        User user = new User();
        user.id                 = jsonObject.optString("id", "");
        user.idstr              = jsonObject.optString("idstr", "");
        user.screen_name        = jsonObject.optString("screen_name", "");
        user.name               = jsonObject.optString("name", "");
        user.province           = jsonObject.optInt("province", -1);
        user.city               = jsonObject.optInt("city", -1);
        user.location           = jsonObject.optString("location", "");
        user.description        = jsonObject.optString("description", "");
        user.url                = jsonObject.optString("url", "");
        user.profile_image_url  = jsonObject.optString("profile_image_url", "");
        user.profile_url        = jsonObject.optString("profile_url", "");
        user.domain             = jsonObject.optString("domain", "");
        user.weihao             = jsonObject.optString("weihao", "");
        user.gender             = jsonObject.optString("gender", "");
        user.followers_count    = jsonObject.optInt("followers_count", 0);
        user.friends_count      = jsonObject.optInt("friends_count", 0);
        user.statuses_count     = jsonObject.optInt("statuses_count", 0);
        user.favourites_count   = jsonObject.optInt("favourites_count", 0);
        user.created_at         = jsonObject.optString("created_at", "");
        user.following          = jsonObject.optBoolean("following", false);
        user.allow_all_act_msg  = jsonObject.optBoolean("allow_all_act_msg", false);
        user.geo_enabled        = jsonObject.optBoolean("geo_enabled", false);
        user.verified           = jsonObject.optBoolean("verified", false);
        user.verified_type      = jsonObject.optInt("verified_type", -1);
        user.remark             = jsonObject.optString("remark", "");
        //user.status             = jsonObject.optString("status", ""); // XXX: NO Need ?
        user.allow_all_comment  = jsonObject.optBoolean("allow_all_comment", true);
        user.avatar_large       = jsonObject.optString("avatar_large", "");
        user.avatar_hd          = jsonObject.optString("avatar_hd", "");
        user.verified_reason    = jsonObject.optString("verified_reason", "");
        user.follow_me          = jsonObject.optBoolean("follow_me", false);
        user.online_status      = jsonObject.optInt("online_status", 0);
        user.bi_followers_count = jsonObject.optInt("bi_followers_count", 0);
        user.lang               = jsonObject.optString("lang", "");
        
        // 注意：以下字段暂时不清楚具体含义，OpenAPI 说明文档暂时没有同步更新对应字段含义
        user.star               = jsonObject.optString("star", "");
        user.mbtype             = jsonObject.optString("mbtype", "");
        user.mbrank             = jsonObject.optString("mbrank", "");
        user.block_word         = jsonObject.optString("block_word", "");
        
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(idstr);
        parcel.writeString(screen_name);
        parcel.writeString(name);
        parcel.writeInt(province);
        parcel.writeInt(city);
        parcel.writeString(location);
        parcel.writeString(description);
        parcel.writeString(url);
        parcel.writeString(profile_image_url);
        parcel.writeString(profile_url);
        parcel.writeString(domain);
        parcel.writeString(weihao);
        parcel.writeString(gender);
        parcel.writeInt(followers_count);
        parcel.writeInt(friends_count);
        parcel.writeInt(statuses_count);
        parcel.writeInt(favourites_count);
        parcel.writeString(created_at);
        parcel.writeByte((byte) (following ? 1 : 0));
        parcel.writeByte((byte) (allow_all_act_msg ? 1 : 0));
        parcel.writeByte((byte) (geo_enabled ? 1 : 0));
        parcel.writeByte((byte) (verified ? 1 : 0));
        parcel.writeInt(verified_type);
        parcel.writeString(remark);
        parcel.writeParcelable(status, i);
        parcel.writeByte((byte) (allow_all_comment ? 1 : 0));
        parcel.writeString(avatar_large);
        parcel.writeString(avatar_hd);
        parcel.writeString(verified_reason);
        parcel.writeByte((byte) (follow_me ? 1 : 0));
        parcel.writeInt(online_status);
        parcel.writeInt(bi_followers_count);
        parcel.writeString(lang);
        parcel.writeString(star);
        parcel.writeString(mbtype);
        parcel.writeString(mbrank);
        parcel.writeString(block_word);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", idstr='" + idstr + '\'' +
                ", screen_name='" + screen_name + '\'' +
                ", name='" + name + '\'' +
                ", province=" + province +
                ", city=" + city +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", profile_image_url='" + profile_image_url + '\'' +
                ", profile_url='" + profile_url + '\'' +
                ", domain='" + domain + '\'' +
                ", weihao='" + weihao + '\'' +
                ", gender='" + gender + '\'' +
                ", followers_count=" + followers_count +
                ", friends_count=" + friends_count +
                ", statuses_count=" + statuses_count +
                ", favourites_count=" + favourites_count +
                ", created_at='" + created_at + '\'' +
                ", following=" + following +
                ", allow_all_act_msg=" + allow_all_act_msg +
                ", geo_enabled=" + geo_enabled +
                ", verified=" + verified +
                ", verified_type=" + verified_type +
                ", remark='" + remark + '\'' +
                ", status=" + status +
                ", allow_all_comment=" + allow_all_comment +
                ", avatar_large='" + avatar_large + '\'' +
                ", avatar_hd='" + avatar_hd + '\'' +
                ", verified_reason='" + verified_reason + '\'' +
                ", follow_me=" + follow_me +
                ", online_status=" + online_status +
                ", bi_followers_count=" + bi_followers_count +
                ", lang='" + lang + '\'' +
                ", star='" + star + '\'' +
                ", mbtype='" + mbtype + '\'' +
                ", mbrank='" + mbrank + '\'' +
                ", block_word='" + block_word + '\'' +
                '}';
    }
}
