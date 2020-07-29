/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shedule;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

/**
 *
 * @author koben
 */
public class DailyStockTask implements Runnable {
    private DataSource dataSource;
    @Override
    public void run() {
        try (Connection con = dataSource.getConnection()) {
            boolean canContinue = false;
            try (Statement s = con.createStatement(); ResultSet rs = s.executeQuery("SELECT o.* FROM t_parameters o WHERE str_KEY='KEY_VALORISATION_JOURNALIERE'")) {
                while (rs.next()) {
                    canContinue = Integer.valueOf(rs.getString("str_VALUE").trim()) == 1;
                    break;
                }
            }
            if (canContinue) {
                try (CallableStatement stmt = con.prepareCall("{CALL proc_update_stock_snaps()}")) {
                    stmt.executeUpdate();
                }
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }
    

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
