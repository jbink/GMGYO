package kr.co.gmgpayment.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreUtil {

	//SharedPreference 이름
	static public String SHARED_PREF_NAME = "gmgyo";

	/*************************************************************************************************/
	//토큰
	static public String PREF_TOKEN= "token";
	/**
	 * 토큰
	 *
	 *  ctx
	 *  value = token
	 */
	static public void setToken(Context ctx, String value) {
		SharedPreferences.Editor editor = ctx.getSharedPreferences(SHARED_PREF_NAME, 0).edit();
		editor.putString(PREF_TOKEN, value);
		editor.commit();
	}
    /**
     * 토큰
     *
     *  default -> ""
     */
    static public String getToken(Context ctx) {
        return ctx.getSharedPreferences(SHARED_PREF_NAME, 0).getString(PREF_TOKEN, "");
    }

    static public String PREF_COOKIE= "cookie";
    /**
     * cookie
     *  ctx
     *  value = token
     */
    static public void setCookie(Context ctx, String value) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(SHARED_PREF_NAME, 0).edit();
        editor.putString(PREF_COOKIE, value);
        editor.commit();
    }

    /**
     * cookie
     *  default -> ""
     */
    static public String getCookie(Context ctx) {
        return ctx.getSharedPreferences(SHARED_PREF_NAME, 0).getString(PREF_COOKIE, "");
    }

    //ID
    static public String PREF_ID= "gmgyo_id";
    /**
     * id
     *  ctx
     *  value = id
     */
    static public void setID(Context ctx, String value) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(SHARED_PREF_NAME, 0).edit();
        editor.putString(PREF_ID, value);
        editor.commit();
    }

    /**
     * id
     *  default -> ""
     */
    static public String getID(Context ctx) {
        return ctx.getSharedPreferences(SHARED_PREF_NAME, 0).getString(PREF_ID, "");
    }

    //PW
    static public String PREF_PW= "gmgyo_pw";
    /**
     * pw
     *  ctx
     *  value = id
     */
    static public void setPW(Context ctx, String value) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(SHARED_PREF_NAME, 0).edit();
        editor.putString(PREF_PW, value);
        editor.commit();
    }

    /**
     * pw
     *  default -> ""
     */
    static public String getPW(Context ctx) {
        return ctx.getSharedPreferences(SHARED_PREF_NAME, 0).getString(PREF_PW, "");
    }

    //자동로그인
    static public String PREF_AUTOLOGIN_CHECK= "gmgyo_auto_login";
    /**
     * bool
     *  ctx
     *  value = 자동로그인 체크 유무
     */
    static public void setAutoLogin(Context ctx, boolean value) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(SHARED_PREF_NAME, 0).edit();
        editor.putBoolean(PREF_AUTOLOGIN_CHECK, value);
        editor.commit();
    }

    /**
     * pw
     *  default -> true
     */
    static public boolean getAutoLogin(Context ctx) {
        return ctx.getSharedPreferences(SHARED_PREF_NAME, 0).getBoolean(PREF_AUTOLOGIN_CHECK, true);
    }


}
