package com.daqin.medicinegod;


import java.util.regex.Pattern;

public class Constant {
    public static final String DATABASE_NAME = "MedicineGod.db";
    public static final int DATABASE_VERSION = 7;
    public static final double VERSION = 1.9;
    public static final String VNAME = "Beta";
    public static final String VERSIONTIPS = "VERSIONTIPS";

    public static final String TABLE_NAME_MEDICINE = "Medicines";
    public static final String TABLE_NAME_HISTORY = "History";
    public static final String TABLE_NAME_USER = "Users";
    public static final String TABLE_NAME_HISTORY_EXCEL = "ExcelsHis";
    public static final String TABLE_NAME_NOTICE = "Notice";

    public static final String PREFERENCES_NAME = "MgData";

    /**
     * mimeType
     */
    public static final String FILE_TYPE_XLS = "application/vnd.ms-excel";
    public static final String FILE_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String FILE_TYPE_CSV = "text/comma-separated-values";

    /**
     * 药品
     */
    public static final String COLUMN_M_KEYID = "KEYID";
    public static final String COLUMN_M_UID = "LNAME";
    public static final String COLUMN_M_NAME = "NAME";
    public static final String COLUMN_M_IMAGE = "IMAGE";
    public static final String COLUMN_M_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_M_OUTDATE = "OUTDATE";
    public static final String COLUMN_M_OTC = "OTC";
    public static final String COLUMN_M_BARCODE = "BARCODE";
    public static final String COLUMN_M_YU = "YU";
    public static final String COLUMN_M_ELABEL = "ELABEL";
    public static final String COLUMN_M_LOVE = "LOVE";
    public static final String COLUMN_M_SHARE = "SHARE";
    public static final String COLUMN_M_MUSE = "MUSE";
    public static final String COLUMN_M_COMPANY = "COMPANY";
    public static final String COLUMN_M_DELFLAG = "DELFLAG";
    public static final String COLUMN_M_SHOWFLAG = "SHOWFLAG";
    public static final String COLUMN_M_FROMWEB = "FROMWEB";
    public static final String COLUMN_M_GROUP = "GROUPF";

    /**
     * 中文标识
     */
    public static final String COLUMN_M_KEYID_CN = "ID";
    public static final String COLUMN_M_UID_CN = "所属人";
    public static final String COLUMN_M_NAME_CN = "药品名";
    public static final String COLUMN_M_IMAGE_CN = "药品图像";
    public static final String COLUMN_M_DESCRIPTION_CN = "药品描述";
    public static final String COLUMN_M_OUTDATE_CN = "过期时间";
    public static final String COLUMN_M_OTC_CN = "标识";
    public static final String COLUMN_M_BARCODE_CN = "条码";
    public static final String COLUMN_M_YU_CN = "余量";
    public static final String COLUMN_M_ELABEL_CN = "药效标签";
    public static final String COLUMN_M_LOVE_CN = "收藏";
    public static final String COLUMN_M_SHARE_CN = "分享目录";
    public static final String COLUMN_M_MUSE_CN = "用法用量";
    public static final String COLUMN_M_COMPANY_CN = "出产公司";
    public static final String COLUMN_M_DELFLAG_CN = "删除标记";
    public static final String COLUMN_M_SHOWFLAG_CN = "展示标记";
    public static final String COLUMN_M_FROMWEB_CN = "网络标记";
    public static final String COLUMN_M_GROUP_CN = "组别";

    /**
     * 用户
     */
    public static final String COLUMN_U_LNAME = "LNAME";
    public static final String COLUMN_U_SNAME = "SNAME";
    public static final String COLUMN_U_PWD = "PWD";
    public static final String COLUMN_U_HEAD = "HEAD";
    public static final String COLUMN_U_FRIEND = "FRIEND";
    public static final String COLUMN_U_PHONE = "PHONE";
    public static final String COLUMN_U_MAIL = "MAIL";
    public static final String COLUMN_U_RGTIME = "RGTIME";
    public static final String COLUMN_U_ONLINE = "ONLINE";
    public static final String COLUMN_U_HAS = "HAS";
    public static final String COLUMN_U_VIP = "VIP";
    public static final String COLUMN_U_VIPYU = "VIPYU";
    public static final String COLUMN_U_POINT = "POINT";
    public static final String COLUMN_U_POINTHISTORY = "POINTHISTORY";
    public static final String COLUMN_U_CLOUDYU = "CLOUDYU";
    public static final String COLUMN_U_SIGNIN = "SIGNIN";
    public static final String COLUMN_U_MYSTYLE = "MYSTYLE";

    /**
     * 消息通知
     */
    public static final String COLUMN_N_ID= "notice_id";
    public static final String COLUMN_N_TITLE= "notice_title";
    public static final String COLUMN_N_CONTEXT= "notice_context";
    public static final String COLUMN_N_FROM= "notice_from";
    public static final String COLUMN_N_TO= "notice_to";
    public static final String COLUMN_N_TIME= "notice_time";
    public static final String COLUMN_N_CHECKD= "notice_check";
    public static final String COLUMN_N_LNAME= "notice_lname";



    public static final String COLUMN_G_DEFAULT = "默认";
    public static final String COLUMN_G_All = "全部";
    public static final String COLUMN_G_ADD = "+ 添加";
    public static final String COLUMN_G_WEB = "网络";


    public static final String COLUMN_H_ID = "BTNID";
    public static final String COLUMN_H_STR = "BTNSTR";

    public static final String COLUMN_H_HOTKEY = "HOTKEY";
    public static final String COLUMN_H_TIMES = "TIMES";


    public static final String COLUMN_HE_FLAG = "FLAG";
    public static final String COLUMN_HE_NAME = "NAME";
    public static final String COLUMN_HE_FILEPATH = "FILEPATH";
    public static final String COLUMN_HE_TIME = "TIME";

    public static final String COLUMN_HE_FLAG_TEMP = "模板";
    public static final String COLUMN_HE_FLAG_INPORT = "导入";
    public static final String COLUMN_HE_FLAG_EXPORT = "导出";
    public static final String COLUMN_HE_FLAG_EXPIRE = "失效";


    public static final String COLUMN_S_RESULT_NAME = "RESULT_NAME";
    public static final String COLUMN_S_RESULT_IMG = "RESULT_IMG";
    public static final String COLUMN_S_RESULT_COMPANY = "RESULT_COMPANY";
    public static final String COLUMN_S_RESULT_DESP = "RESULT_DESP";
    public static final String COLUMN_S_RESULT_LINK = "RESULT_LINK";


    public static final String COLUMN_C_B_BID = "BID";
    public static final String COLUMN_C_B_LNAME = "LNAME";
    public static final String COLUMN_C_B_TITLE = "TITLE";
    public static final String COLUMN_C_B_CONTEXT = "CONTEXT";
    public static final String COLUMN_C_B_TORNAME = "TORNAME";
    public static final String COLUMN_C_B_TOPHONE = "TOPHONE";
    public static final String COLUMN_C_B_TOADDRESS = "TOADDRESS";
    public static final String COLUMN_C_B_IMAGE = "IMAGE";
    public static final String COLUMN_C_B_TIMESHOW = "TIME_SHOW";
    public static final String COLUMN_C_B_METHOD = "METHOD";

    public static final String COLUMN_C_B_ZAN = "ZAN";

    public static final String COLUMN_C_B_PID = "PID";
    public static final String COLUMN_C_B_PING_LNAME = "PLNAME";
    public static final String COLUMN_C_B_PING_HEAD = "PHEAD";
    public static final String COLUMN_C_B_PING_SNAME = "PSNAME";
    public static final String COLUMN_C_B_PING_CONTEXT = "PCONTEXT";
    public static final String COLUMN_C_B_PING_SHOWTIME = "PTIME_SHOW";

    public static final String COLUMN_C_B_METHOD_XIANZHI = "XIANZHI";
    public static final String COLUMN_C_B_METHOD_XUQIU = "XUQIU";
    public static final String COLUMN_C_B_METHOD_JIAOHUAN = "JIAOHUAN";

    public static final String COLUMN_C_S_SID = "SID";
    public static final String COLUMN_C_S_MONEY = "MONEY";


    public static final String TABLE_COLLAGE_BORROW = "taishankejixueyuan_school_borrow";
    public static final String TABLE_COLLAGE_SALE = "taishankejixueyuan_school_sale";
    public static final String TABLE_COLLAGE_BORROW_ZAN = "taishankejixueyuan_school_zan";
    public static final String TABLE_COLLAGE_BORROW_PING = "taishankejixueyuan_school_ping";


    public static final String SEARCHKEY = "searchkey";
    public static final String SEARCHCHECKKEY = "searchcheckkey";
    public static final String EDITKEY = "editkey";
    public static final String ADDKEY = "addkey";
    public static final String SHOWMETHODKEY = "showmethodkey";
    public static final String SHOWSHOW = "showkey";
    public static final String CBBIDKEY = "cbbidkey";
    public static final String GROUPKEY = "groupkey";
    public static final String GROUPLISTKEY = "grouplkey";

    public static final String MG_LOGIN = "login";
    public static final String MG_LOGIN_LNAME = "login_lanme";

    public static final String MG_DATE_OUT = "mg_date_out";
    public static final String MG_DATE_NEAR = "mg_date_near";
    public static final String MG_DATE_OK = "mg_date_ok";
    public static final String MG_M_COUNT_WEB = "mg_count_web";
    public static final String MG_M_COUNT_LOCAL = "mg_count_local";
    public static final String MG_M_COUNT_TOTAL = "mg_count_total";
    public static final int MAX_YU = 2000000000;
    public static final int MAX_USE = 2000000000;


    public static final String FromMail = "wfgmqhx@163.com";
    public static final String FromSecret = "PTYVSZLVHAUJDPYG";
    public static final Pattern FromMailPattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");

    public static final String SQL_CREATE_M_TABLE = "create table " + Constant.TABLE_NAME_MEDICINE + "(" +
            Constant.COLUMN_M_KEYID + " VARCHAR(20)  NOT NULL PRIMARY KEY," +
            Constant.COLUMN_M_UID + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_M_NAME + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_M_IMAGE + " LONGBLOB NOT NULL, " +
            Constant.COLUMN_M_DESCRIPTION + " longtext NOT NULL," +
            Constant.COLUMN_M_OUTDATE + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_M_OTC + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_M_BARCODE + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_M_YU + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_M_ELABEL + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_M_LOVE + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_M_SHARE + " LONGTEXT NOT NULL," +
            Constant.COLUMN_M_MUSE + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_M_COMPANY + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_M_DELFLAG + " INT(2) NOT NULL," +
            Constant.COLUMN_M_SHOWFLAG + " INT(2) NOT NULL," +
            Constant.COLUMN_M_FROMWEB + " INT(2) NOT NULL," +
            Constant.COLUMN_M_GROUP + " VARCHAR(20) NOT NULL" +
//			Constant.COLUMN_M_MD5KEY + " VARCHAR(20) NOT NULL" +
            ")";

    public static final String SQL_CREATE_U_TABLE = "create table " + Constant.TABLE_NAME_USER + "(" +
            Constant.COLUMN_U_LNAME + " VARCHAR(20)  NOT NULL PRIMARY KEY," +
            Constant.COLUMN_U_SNAME + " VARCHAR(50) NOT NULL," +
            Constant.COLUMN_U_PWD + " VARCHAR(20) NOT NULL, " +
            Constant.COLUMN_U_HEAD + " LONGBLOB NOT NULL," +
            Constant.COLUMN_U_FRIEND + " LONGTEXT NOT NULL," +
            Constant.COLUMN_U_PHONE + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_U_MAIL + " VARCHAR(50) NOT NULL," +
            Constant.COLUMN_U_RGTIME + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_U_ONLINE + " INT(2) NOT NULL," +
            Constant.COLUMN_U_POINT + " INT(10) NOT NULL," +
            Constant.COLUMN_U_POINTHISTORY + " LONGTEXT NOT NULL," +
//                Constant.COLUMN_U_HAS+" VARCHAR(20) NOT NULL,"+
            Constant.COLUMN_U_VIP + " LONGTEXT NOT NULL," +
            Constant.COLUMN_U_VIPYU + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_U_CLOUDYU + " INT(10) NOT NULL," +
            Constant.COLUMN_U_SIGNIN + " VARCHAR(20) NOT NULL," +
            Constant.COLUMN_U_MYSTYLE + " LONGTEXT NOT NULL" +
            ")";


}