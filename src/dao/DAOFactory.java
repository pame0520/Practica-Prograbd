/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import util.AppConfig;

/**
 *
 * @author pame
 */

public class DAOFactory {
    public static TaskDAO createTaskDAO() {
        String mode = AppConfig.get("mode", "SIMULATED");
        if ("JDBC".equalsIgnoreCase(mode)) {
            return new JDBCTaskDAO();
        } else {
            return new SimulatedTaskDAO();
        }
    }
}
