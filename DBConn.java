package Filmdatabase;


import java.sql.*;
import java.util.Properties;
import java.sql.Connection;  
import java.sql.DriverManager;  

public abstract class DBConn {
    protected Connection conn;
    
    public DBConn () {
    }
    public void connect() {
    	try {
    		Class.forName("com.mysql.cj.jdbc.Driver");  
	    // Properties for user and password.
            Properties p = new Properties();
            p.put("user", "root");
            p.put("password", "Cool-man123");           
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/filmdb?autoReconnect=true&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",p);
        } catch (Exception e)
    	{
            throw new RuntimeException("Unable to connect LEMAO", e);
    	}
    }
}