package com.pixurvival.core.util;

import com.esotericsoftware.minlog.Log;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LogUtils {

    public static void debug(String s1, String s2) {
        if (Log.DEBUG) {
            Log.debug(s1 + s2);
        }
    }

    public static void debug(String s1, String s2, String s3) {
        if (Log.DEBUG) {
            StringBuilder sb = new StringBuilder(s1.length() + s2.length() + s3.length());
            sb.append(s1).append(s2).append(s3);
            Log.debug(sb.toString());
        }
    }

    public static void debug(String... strings) {
        if (Log.DEBUG) {
            StringBuilder sb = new StringBuilder();
            for (String string : strings) {
                sb.append(string);
            }
            Log.debug(sb.toString());
        }
    }

    public static void debug(Object o) {
        if (Log.DEBUG) {
            Log.debug(o.toString());
        }
    }

    public static void debug(Object o1, Object o2) {
        if (Log.DEBUG) {
            debug(o1.toString(), o2.toString());
        }
    }

    public static void debug(Object o1, Object o2, Object o3) {
        if (Log.DEBUG) {
            debug(o1.toString(), o2.toString(), o3.toString());
        }
    }

    public static void debug(Object... objects) {
        if (Log.DEBUG) {
            StringBuilder sb = new StringBuilder();
            for (Object object : objects) {
                sb.append(object.toString());
            }
            Log.debug(sb.toString());
        }
    }

}
