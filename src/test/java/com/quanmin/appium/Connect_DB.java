package com.quanmin.appium;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect_DB {
    public Connection con = null;  
    public Statement stmt = null;
    public ResultSet rs = null; 
    
	public static void main(String[] args) {
		Connect_DB db = new Connect_DB();
		db.deleteDb();
		db.selectDb();
		
		db.closeDb();
	}
	
	public  Connect_DB(){
		String url = "com.mysql.jdbc.Driver";
		String connectSql = "jdbc:mysql://rm-uf69e2i0360c1ozrvo.mysql.rds.aliyuncs.com:3307/db_rich";
		String sqlUser = "checkdb"; //数据库账号  
        String sqlPasswd = "tLr^&PL(36GH)_+UF"; //你的数据库密码  
              
        
        try {  
            //加载驱动包  
            Class.forName(url);  
            //连接MYSQL  
            con = DriverManager.getConnection(connectSql,sqlUser,sqlPasswd);  
            stmt = con.createStatement();
              
        } catch (Exception e) {  
            System.out.println("显示所有数据报错，原因："+e.getMessage());  
        }                    
    } 
	
	
	public void closeDb(){
        //关闭数据库连接  
		try {
			rs.close();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
			

	}
	
	public void selectDb() {
        try {
			String sql = "SELECT * FROM nick_charge_record WHERE uid=4299778";
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				System.out.println(rs.getInt(1));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}     		
	}
	
	public void  deleteDb() {
		try {
			String sql = "DELETE FROM nick_charge_record WHERE id=18279";
			stmt.executeUpdate(sql);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void insertDb(){
		
	}


}
