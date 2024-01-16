package com.daqin.medicinegod.Utils;

import java.sql.*;

/*
 * JDBC工具类
 * */
public class JdbcUtil {
	//	public static final String url = "";
//    public static final String uRL = "jdbc:mysql://139.224.48.87:3306/mg?characterEncoding=utf-8&useSSL=false";
//    public static final String uNname = "mg";
//    public static final String uPwd = "Qhx010394Mg";
	public static final String uRL = "jdbc:mysql://139.224.48.87:3306/mg?characterEncoding=utf-8&useSSL=false";
	public static final String uNname = "mg";
	public static final String uPwd = "Qhx010394Mg";


	static {
		try {
			//注释
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 获得连接
	 * */
	public static Connection getConnection() {
		Connection con = null;
		try {
			con = DriverManager.getConnection(uRL, uNname, uPwd);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return con;
	}

	/*
	 * 释放资源
	 * ResultSet
	 * Statement或PrepareStatement
	 * Connection
	 * 释放资源有顺序 ResultSet->Statment或PrepareStatement->Connection
	 * */
	public static void close(Connection con,
	                         Statement stat, PreparedStatement pre, ResultSet ret) {
		if (ret != null) {
			try {
				ret.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		if (stat != null) {
			try {
				stat.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		if (pre != null) {
			try {
				pre.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

	}
}
