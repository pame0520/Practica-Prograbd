/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import com.sun.jdi.connect.spi.Connection;
import util.AppConfig;

/**
 *
 * @author pame
 */
public class DBConnection {
    private static DBConnection instance;
    private Connection conn;

    private DBConnection() throws SQLException, ClassNotFoundException {
        String driver = AppConfig.get("jdbc.driver");
        String url = AppConfig.get("jdbc.url");
        String user = AppConfig.get("jdbc.user");
        String pass = AppConfig.get("jdbc.password");
        if (driver != null && !driver.isEmpty()) {
            Class.forName(driver);
        }
        conn = DriverManager.getConnection(url, user, pass);
        conn.setAutoCommit(true);
    }

    public static synchronized Connection getConnection() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance.conn;
    }
}
