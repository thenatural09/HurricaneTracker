package com.company;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Created by Troy on 10/13/16.
 */
public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }

    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Troy", "pass");
        User user = Main.selectUser(conn,"Troy");
        conn.close();
        assertTrue(user != null);
    }

    @Test
    public void testHurricane() throws SQLException {
        Connection conn = startConnection();
        Main.insertHurricane(conn,"Matthew","Charleston",2,"image",1);
        ArrayList<Hurricane> hurricanes = Main.selectHurricane(conn);
        conn.close();
        assertTrue(hurricanes.size() == 1);
    }
}
