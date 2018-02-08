package kr.huah.maleum;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by banggrae on 2018. 1. 25..
 */

public class Preference {

    public static final String KEY_AGREE_SMS = "agreeSms";
    public static final String KEY_AGREE_NOTIFY = "agreeNotify";
    public static final String KEY_WEATHER_ALARM = "weatherAlarm";
    public static final String KEY_DEVICE_ID = "deviceId";
    public static final String KEY_CM_TOKEN = "messageToken";

    static final String NAME = Preference.class.getPackage().getName();

    public static boolean agreedSms(Context context) {
        SharedPreferences pf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return pf.getBoolean("agreeSms", false);
    }

}
