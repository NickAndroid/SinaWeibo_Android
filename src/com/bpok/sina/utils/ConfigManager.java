package com.bpok.sina.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author howard data store and fetch
 * 
 */
public class ConfigManager {
	private Context mContext;
	private SharedPreferences spf;

	/**
	 * @param mContext
	 */
	public ConfigManager(Context mContext) {
		super();
		this.mContext = mContext;
		spf = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	/**
	 * @param name
	 *            存储用户名
	 */
	public void setStoreUserNameConfig(String name) {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		spf.edit().putString("USER_NAME", name).commit();
	}

	/**
	 * @return 获取用户名
	 */
	public String getUserName() {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		return spf.getString("USER_NAME", "同步中...");

	}

	/**
	 * @return 获取过滤类型
	 */
	public int getFeatureType() {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		return spf.getInt("featureType", 0);
	}

	/**
	 * @param type
	 *            设置过滤类型
	 */
	public void setFeatureType(int type) {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		spf.edit().putInt("featureType", type).commit();
	}

	/**
	 * 设置主题 0：白天 1：夜间
	 */
	public void setThemeMod(int mod) {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		spf.edit().putInt("ThemeMod", mod).commit();
	}

	/**
	 * 获取主题 0：白天 1：夜间
	 */
	public int getThemeMod() {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		return spf.getInt("ThemeMod", 0);
	}

	/**
	 * @param gender
	 *            设置性别，0：男 1：女
	 */
	public void setUserGender(int gender) {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		spf.edit().putInt("UserGender", gender).commit();
	}

	/**
	 * @return 返回用户性别，默认：男 0：男 1：女
	 */
	public int getUserGender() {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		return spf.getInt("UserGender", 0);
	}

	/**
	 * @param userMod
	 *            设置签名
	 */
	public void setUserMod(String userMod) {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		spf.edit().putString("UserMod", userMod).commit();
	}

	/**
	 * @return 返回签名
	 */
	public String getUserMod() {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		return spf.getString("UserMod", "");
	}

	/**
	 * @param count
	 *            设置用户粉丝数
	 */
	public void setUserFansCount(int count) {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		spf.edit().putInt("UserFansCount", count).commit();
	}

	/**
	 * @return 返回用户粉丝数
	 */
	public int getUserFansCount() {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		return spf.getInt("UserFansCount", 0);
	}

	/**
	 * @param count
	 *            设置用户关注数
	 */
	public void setUserFollowingCount(int count) {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		spf.edit().putInt("UserFollowingCount", count).commit();
	}

	/**
	 * @return 返回用户关注数
	 */
	public int getUserFollowingCount() {
		if (spf == null) {
			spf = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		return spf.getInt("UserFollowingCount", 0);
	}

}