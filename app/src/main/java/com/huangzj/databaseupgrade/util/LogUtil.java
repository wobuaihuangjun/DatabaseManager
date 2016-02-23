package com.huangzj.databaseupgrade.util;

import android.content.Context;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * <b>此类可以灵活的控制log输出,解决了因不同的人开发的时候打印不同的log造成程序log打印非常混乱,只需做一些简单的配置
 * 就可屏蔽log输出和打印自己想要的log输出</b>
 * <p><br>1.在配置文件中配置"类全名"=true/false;"包名"=true/false
 * <br>2.如果类配置和包配置有冲突就以类配置为准
 * <br>3.父包和子包配置有冲突就以子包为准</p>
 * <p>和assets文件夹中的log.properties文件配合控制log的打印输出
 * 在文件中配置类的全名和log打印模式(true表为debug模式,打印log;false表示非debug模式,即不打印log)
 * 在文件中配置类的log打印模式是永久生效的,也可直接调用debugAll()或者debug()方法来动态改变文件的配置
 * 不过调用这两个方法改变的打印模式只在内存中生效,不会写入配置文件中
 * addDebug()是动态添加新的类的打印模式,只在内存中生效,不写入配置文件</p>
 *
 * @author lhd
 */
public class LogUtil {

    private static String pckName;

    private static String className;

    private static String simpleClassName;

    private static String methodName;

    private static int lineNumber;

    private static boolean DEBUG = true;

    /**
     * 日志保存模式,false不保存文件,true则自动保存文件
     */
    private static boolean saveFile = true;

    private static boolean i = true;

    private static boolean d = true;

    private static boolean w = true;

    private static boolean v = true;

    private static boolean e = true;

    public static String saveUrl = Environment.getExternalStorageDirectory() + "/xtcdata/logs/watch/";

    public static String testUrl = Environment.getExternalStorageDirectory() + "/xtcdata/logs/test/";

    private static Properties properties = new Properties();

    private static String propertiesName = "log.properties";

    public static void init(Context context) {
        initProperties(context);
        initVar();
        pckName = context.getPackageName();
    }

    private static void initProperties(Context context) {
        try {
            InputStream is = context.getAssets().open(propertiesName);
            properties.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initVar() {
        if (properties != null) {
            String saveMode = properties.getProperty("saveFile");
            if (saveMode != null) {
                saveFile = Boolean.parseBoolean(saveMode);
            }
            String iMode = properties.getProperty("i");
            if (iMode != null) {
                i = Boolean.parseBoolean(iMode);
            }
            String dMode = properties.getProperty("d");
            if (dMode != null) {
                d = Boolean.parseBoolean(dMode);
            }
            String wMode = properties.getProperty("w");
            if (wMode != null) {
                w = Boolean.parseBoolean(wMode);
            }
            String vMode = properties.getProperty("v");
            if (vMode != null) {
                v = Boolean.parseBoolean(vMode);
            }
            String eMode = properties.getProperty("e");
            if (eMode != null) {
                e = Boolean.parseBoolean(eMode);
            }
        }
    }

    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    /**
     * 日志保存模式,false不保存文件,true则自动保存文件
     */
    public static void setSaveMode(boolean saveFile) {
        LogUtil.saveFile = saveFile;
    }

    public static void setSaveLevel(boolean v, boolean d, boolean i, boolean w, boolean e) {
        LogUtil.v = v;
        LogUtil.d = d;
        LogUtil.i = i;
        LogUtil.w = w;
        LogUtil.e = e;
    }

    public static void debug(String name, boolean debug) {
        if (properties == null) {
            return;
        }
        for (Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getKey().toString().equals(name)) {
                entry.setValue(String.valueOf(debug));
                return;
            }
        }
        properties.setProperty(name, String.valueOf(debug));
    }

    private enum LEVEL {
        verbose, debug, info, warn, error
    }

    private static void initLogMember(StackTraceElement[] sElements) {
        className = sElements[1].getClassName();
        int i = className.lastIndexOf(".");
        if (i + 1 < className.length() - 1)
            simpleClassName = className.substring(i + 1, className.length());
        else
            simpleClassName = className;
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    private synchronized static void log(LEVEL level, StackTraceElement[] sElements, String... msg) {
        if (!DEBUG) {
            //Log.i("LogUtil", "DEBUG = false");
            return;
        }
        initLogMember(sElements);
        if (check()) {
            print(level, msg);
        } else {
//            if (properties == null) {
//                print(level, msg);
//            }
        }
    }

    private static void print(LEVEL level, String... msg) {
        String tag = "";
        String text = "";
        if (msg != null && msg.length == 1) {
            tag = formatTag();
            text = msg[0];
        } else if (msg != null && msg.length == 2) {
            tag = msg[0];
            text = msg[1];
        } else {
            Log.w("LogUtil", "String... msg is illegal(null or length>2)");
            return;
        }

        switch (level) {
            case verbose:
                Log.v(tag, text);
                break;
            case debug:
                Log.d(tag, text);
                break;
            case info:
                Log.i(tag, text);
                break;
            case warn:
                Log.w(tag, text);
                break;
            case error:
                Log.e(tag, text);
                break;
            default:
                break;
        }
    }

    private static final class LogStruct {
        String level;
        String time;
        int pid;
        int tid;
        String pckName;
        String tag;
        String text;

        LogStruct(String level, String tag, String text) {
            String formatDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
            this.time = formatDate;
            this.pid = Process.myPid();
            this.tid = Process.myTid();
            this.pckName = LogUtil.pckName;
            this.level = level;
            this.tag = tag;
            this.text = text;
        }

        @Override
        public String toString() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("level", level);
                jsonObject.put("time", time);
                jsonObject.put("pid", pid);
                jsonObject.put("tid", tid);
                jsonObject.put("pckName", pckName);
                jsonObject.put("tag", tag);
                jsonObject.put("text", text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }
    }

    private static void saveLogToFile(LEVEL level, String... msg) {
        saveLogToFile(level, saveUrl, DateFormatUtil.format(DateFormatUtil.FORMAT_2) + ".txt", false, msg);
    }

    /**
     * 把日志保存到sdcard文件夹中
     *
     * @param level
     * @param dir
     * @param fileName
     * @param msg
     */
    private static void saveLogToFile(LEVEL level, String dir, String fileName, boolean clear, String... msg) {
        String tag = "";
        String text = "";
        if (msg != null && msg.length == 1) {
            tag = formatTag();
            text = msg[0];
        } else if (msg != null && msg.length == 2) {
            tag = msg[0];
            text = msg[1];
        } else {
            log(LEVEL.warn, new Throwable().getStackTrace(), "String... msg is illegal(null or length>2)");
            return;
        }
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }

        if (clear) {
            File[] fs = file.listFiles();
            for (File f : fs) {
                if (f.getName().equals(fileName)) {
                    continue;
                }
                f.delete();
            }
        }

        File logFile = new File(dir, fileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                log(LEVEL.error, new Throwable().getStackTrace(), "create log file failed:" + e.toString());
            }
        }
        LogStruct logStruct = new LogStruct(level.name(), tag, text);
        writeToFile(logFile, logStruct, true);
    }

    private static void writeToFile(File logFile, LogStruct logStruct, boolean append) {
        String line = logStruct.toString() + "\n";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(logFile, append); // 内容追加方式append
            fos.write(line.getBytes());
        } catch (IOException e) {
            log(LEVEL.error, new Throwable().getStackTrace(), "save log failed:" + e.toString());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log(LEVEL.error, new Throwable().getStackTrace(), "close FileOutputStream failed after save log:" + e.toString());
                }
            }
        }
    }

    private static boolean check() {

        if (properties == null) {
            Log.e("LogUtil", "properties is null");
            return false;
        }

        String key = new String(className);
        int i = -1;
        if ((i = key.indexOf("$")) != -1) {
            key = key.substring(0, i);
        }

        //完整类名匹配
        String result = properties.getProperty(key);
        if (result != null) {
            return Boolean.parseBoolean(result);
        }

        //完整包名匹配
        try {
            key = Class.forName(key).getPackage().getName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String pckResult = properties.getProperty(key);
        if (pckResult != null) {
            return Boolean.parseBoolean(pckResult);
        }

        //最小父包名匹配
        Map<String, String> map = new HashMap<String, String>();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            String k = entry.getKey().toString();
            if (key.startsWith(k)) {
                if (map.get(key) != null) {
                    if (map.get(key).length() < k.length())
                        map.put(key, k);
                } else {
                    map.put(key, k);
                }
            }
        }
        if (map.size() > 0) {
            boolean b = Boolean.parseBoolean(properties.getProperty(map.get(key)));
            map.clear();
            return b;
        }
        return true;
    }

    private static String formatTag() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(simpleClassName);
        strBuf.append(".");
        strBuf.append(methodName);
        strBuf.append(":");
        strBuf.append(lineNumber);
        return strBuf.toString();
    }

    public static void test(String... msg) {
        log(LEVEL.error, new Throwable().getStackTrace(), msg);
        saveLogToFile(LEVEL.debug, testUrl, DateFormatUtil.format(DateFormatUtil.FORMAT_2) + ".txt", true, msg);
    }

    public static void v(String... msg) {
        log(LEVEL.verbose, new Throwable().getStackTrace(), msg);
        if (saveFile && v) {
            saveLogToFile(LEVEL.verbose, msg);
        }
    }

    public static void d(String... msg) {
        log(LEVEL.debug, new Throwable().getStackTrace(), msg);
        if (saveFile && d) {
            saveLogToFile(LEVEL.debug, msg);
        }
    }

    public static void i(String... msg) {
        log(LEVEL.info, new Throwable().getStackTrace(), msg);
        if (saveFile && i) {
            saveLogToFile(LEVEL.info, msg);
        }
    }

    public static void w(String... msg) {
        log(LEVEL.warn, new Throwable().getStackTrace(), msg);
        if (saveFile && w) {
            saveLogToFile(LEVEL.warn, msg);
        }
    }

    public static void e(String... msg) {
        log(LEVEL.error, new Throwable().getStackTrace(), msg);
        if (saveFile && e) {
            saveLogToFile(LEVEL.error, msg);
        }
    }

    public static void e(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        log(LEVEL.error, new Throwable().getStackTrace(), stringWriter.toString());
        if (saveFile && e) {
            saveLogToFile(LEVEL.error, stringWriter.toString());
        }
    }

    public static void e(String tag, Exception throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        log(LEVEL.error, new Throwable().getStackTrace(), tag, stringWriter.toString());
        if (saveFile && e) {
            saveLogToFile(LEVEL.error, stringWriter.toString());
        }
    }
}
