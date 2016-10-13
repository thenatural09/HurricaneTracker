package com.company;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import sun.jvm.hotspot.asm.sparc.SPARCArgument;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        HashMap<String,User> users = new HashMap<>();
        createTable(conn);
        Spark.get(
                "/",
                (request,response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = users.get(name);
                    HashMap m = new HashMap();
                    if (user != null) {
                        m.put("name",user.name);
                    }
                    m.put("hurricanes",selectHurricane(conn));
                    return new ModelAndView(m,"home.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request,response) -> {
                    String name = request.queryParams("loginName");
                    String password = request.queryParams("password");
                    User user = users.get(name);
                    if (user == null) {
                        user = new User(name,password);
                        users.put(name,user);
                    }
                    else if (!password.equals(user.password)) {
                        response.redirect("/");
                        return null;
                    }

                    Session session = request.session();
                    session.attribute("loginName",name);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/logout",
                (request,response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/hurricane",
                (request,response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = users.get(name);
                    if (user == null) {
                        return null;
                    }
                    String hname = request.queryParams("hname");
                    String hlocation = request.queryParams("hlocation");
                    int hcategory = Integer.valueOf(request.queryParams("hcategory"));
                    String himage = request.queryParams("himage");
                    insertHurricane(conn,hname,hlocation,hcategory,himage);
                    response.redirect("/");
                    return null;
                }
        );
    }

    public static void createTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS hurricanes (id IDENTITY, name VARCHAR, location VARCHAR, category INT, image VARCHAR)");
    }

    public static void insertHurricane(Connection conn,String name,String location,int category,String image) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO hurricanes VALUES(null,?,?,?,?)");
        stmt.setString(1,name);
        stmt.setString(2,location);
        stmt.setInt(3,category);
        stmt.setString(4,image);
        stmt.execute();
    }

    public static ArrayList<Hurricane> selectHurricane(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM hurricanes");
        ResultSet results = stmt.executeQuery();
        ArrayList<Hurricane> hurricanes = new ArrayList<>();
        while (results.next()) {
            int id = results.getInt("id");
            String name = results.getString("name");
            String location = results.getString("location");
            int category = results.getInt("category");
            String image = results.getString("image");
            Hurricane hurricane = new Hurricane(id,name,location,category,image);
            hurricanes.add(hurricane);
        }
        return hurricanes;
    }
}
