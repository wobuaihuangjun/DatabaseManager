package com.hzj.database;

/**
 * 数据库配置
 * <p/>
 * Created by hzj on 2016/10/9.
 */
public interface DbConfig {

    interface Launcher {

        String DATABASE_NAME = "i3launcher.db";

        int DATABASE_VERSION = 1;
    }

    interface Setting {

        String DATABASE_NAME = "setting.db";

        int DATABASE_VERSION = 1;
    }

    interface Contact {
        String DATABASE_NAME = "contact.db";

        int DATABASE_VERSION = 1;
    }

    interface SinaWeib {
        String DATABASE_NAME = "sinaweibo.db";

        int DATABASE_VERSION = 1;
    }
}
