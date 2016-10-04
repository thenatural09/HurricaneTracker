package com.company;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import sun.jvm.hotspot.asm.sparc.SPARCArgument;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        HashMap<String,User> users = new HashMap<>();
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
    }
}
