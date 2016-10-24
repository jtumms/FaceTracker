package com.company;

import jdk.packager.services.userjvmoptions.PreferencesUserJvmOptions;
import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;


import javax.servlet.MultipartConfigElement;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


import java.sql.*;
import java.util.ArrayList;

import java.util.HashMap;
import sun.misc.BASE64Decoder;

import static spark.Spark.post;




/**
 * Created by john.tumminelli on 10/15/16.
 */
public class Main {

    public static void main(String[] args) throws SQLException {

        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);


        Spark.externalStaticFileLocation("public");
        Server.createWebServer().start();


        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");

                    if (name != null) {
                        response.redirect("/home");
                    }
                    return new ModelAndView(null, "index.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String name = request.queryParams("loginName");
                    String password = request.queryParams("password");
                    User user = checkForValidUser(conn, name);
                    if (user == null) {
                        createUser(conn, name, password);
                    } else if (!password.equals(user.password)) {
                        response.redirect("/");
                        return null;
                    }

                    Session session = request.session();
                    session.attribute("loginName", name);
                    response.redirect("/home");
                    return null;
                }
        );
        Spark.get(
                "/home",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = checkForValidUser(conn, name);
                    HashMap m = new HashMap();
                    if (user != null) {
                        m.put("name", name);
                    }
                    if (session != null) {
                        populateData(conn, m, user);
                    }


                    return new ModelAndView(m, "home.html");
                },
                new MustacheTemplateEngine()
        );
        Spark.get(
                "/photo",
                (request, response) -> {

                    return new ModelAndView(null, "takePhoto.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/photo",
                (request, response) -> {

                    request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

                    try (InputStream input = request.raw().getPart("mydata").getInputStream()) { // getPart needs to use same "name" as input field in form
                        File uploadDir = new File("public/images");
                        uploadDir.mkdirs(); // create the images directory if it doesn't exist
                        File imageFile = File.createTempFile("photo", ".jpg", uploadDir);
                        BASE64Decoder decoder = new BASE64Decoder();
                        byte[] decodedBytes = decoder.decodeBuffer(input);
                        try (FileOutputStream stream = new FileOutputStream(imageFile)) {
                            stream.write(decodedBytes);
                        }

                        Session session = request.session();
                        String name = session.attribute("loginName");
                        User user = checkForValidUser(conn, name);
                        String first = request.queryParams("firstName");
                        String last = request.queryParams("lastName");
                        int height = Integer.valueOf(request.queryParams("height"));
                        int weight = Integer.valueOf(request.queryParams("weight"));
                        String lastAddress= request.queryParams("lastAddress");
                        String skills  = request.queryParams("specialSkills");
                        int userId = user.id;
                        String eyeColor = request.queryParams("eye color");
                        String imageUrl = imageFile.getName();

                        insertAsset(conn, first, last, height, weight, lastAddress, skills, userId, eyeColor, imageUrl);
                    }


                    response.redirect("/home");
                    return null;
                }
        );
        Spark.post(
                "/delete-asset",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = checkForValidUser(conn, name);

                    if (user == null) {
                        return null;
                    }
                    int deleteId = Integer.valueOf(request.queryParams("id"));
                    deleteAsset(conn, deleteId);




                    response.redirect("/home");
                    return null;
                }
        );
        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return null;
                }
        );
        Spark.get(
                "/edit-asset",
                (request, response) -> {
                    int selectedId = Integer.valueOf(request.queryParams("id"));
                    RootObject rootObj = selectAsset(conn, selectedId);

                    return new ModelAndView(rootObj, "editAsset.html");
                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/edit-asset",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = checkForValidUser(conn, name);
                    if (name == null){
                        return null;
                    }
                    int id = Integer.valueOf(request.queryParams("id"));
                    String firstName = request.queryParams("firstName");
                    String lastName = request.queryParams("lastName");
                    int height = Integer.valueOf(request.queryParams("height"));
                    int weight = Integer.valueOf(request.queryParams("weight"));
                    String lastAddress = request.queryParams("lastAddress");
                    String skills = request.queryParams("specialSkills");
                    String eyeColor = request.queryParams("eyeColor");
                    editAsset(conn, id, firstName, lastName, height, weight, lastAddress, skills, eyeColor);
                    response.redirect("/home");

                    return "";
                }

        );



    }

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS root_object (id IDENTITY, first_name VARCHAR, last_name VARCHAR, height INT, weight INT, last_address VARCHAR, special_skills VARCHAR, user_entered INT, eye_color VARCHAR, image_url VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS face (id IDENTITY, x INT, y INT, width INT, height INT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS mouth (id IDENTITY,x INT, y INT, width INT, height INT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS nose (id IDENTITY,x INT, y INT, width INT, height INT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS image (id IDENTITY, width INT, height INT)");
    }
    public static User checkForValidUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id, name, password);
        }
        return null;

    }
    public static void createUser (Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }
    public static void insertAsset (Connection conn, String first, String last, int height, int weight, String lastAddress, String skills, int userId, String eyeColor, String imageUrl ) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO root_object VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        stmt.setString(1, first);
        stmt.setString(2, last);
        stmt.setInt(3, height);
        stmt.setInt(4, weight);
        stmt.setString(5, lastAddress);
        stmt.setString(6, skills);
        stmt.setInt(7, userId);
        stmt.setString(8, eyeColor);
        stmt.setString(9, imageUrl);
        stmt.execute();


    }
    public static ArrayList populateData(Connection conn, HashMap m, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM root_object INNER JOIN users ON root_object.user_entered = users.id");
        ResultSet result = stmt.executeQuery();
        ArrayList<RootObject> rootObjArray = new ArrayList<>();
        while (result.next()){

            int id = result.getInt("id");
            String firstName = result.getString("first_name");
            String lastName = result.getString("last_name");
            int height = result.getInt("height");
            int weight = result.getInt("weight");
            String lastAddress = result.getString("last_address");
            String skills = result.getString("special_skills");
            int userEntered = result.getInt("user_entered");
            String author = result.getString("users.name");
            String eyeColor = result.getString("eye_color");
            String imageUrl = result.getString("image_url");
            RootObject rootObj = new RootObject(id, firstName, lastName, height, weight, lastAddress, skills, author, eyeColor, imageUrl, false);
            rootObjArray.add(rootObj);
            if (userEntered == user.id) {
                rootObj.isMe = true;
            }
            m.put("rootObjArray", rootObjArray);


        }

        return rootObjArray;
    }
    public static void deleteAsset(Connection conn, int deleteId) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM root_object WHERE id = ?");
        stmt.setInt(1, deleteId);
        stmt.execute();
    }
    public static RootObject editAsset(Connection conn, int id, String firstName, String lastName, int height, int weight, String lastAddress, String skills, String eyeColor) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("UPDATE root_object SET first_name = ?," +
                "last_name = ?," +
                "height = ?," +
                "weight = ?," +
                "last_address = ?," +
                "special_skills = ?," +
                "eye_color = ? " +
                "WHERE id = ?");
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setInt(3, height);
        stmt.setInt(4, weight);
        stmt.setString(5, lastAddress);
        stmt.setString(6, skills);
        stmt.setString(7,eyeColor);
        stmt.setInt(8, id);
        stmt.execute();

        return new RootObject(id, firstName, lastName, height, weight, lastAddress, skills, eyeColor );


        }

    public static RootObject selectAsset(Connection conn, int selectedId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM root_object INNER JOIN users ON root_object.user_entered = users.id WHERE root_object.id = ?");
        stmt.setInt(1, selectedId);
        ResultSet result = stmt.executeQuery();
        ArrayList<RootObject> rootObjArray = new ArrayList<>();
        while (result.next()) {
            int id = result.getInt("id");
            String firstName = result.getString("first_name");
            String lastName = result.getString("last_name");
            int height = result.getInt("height");
            int weight = result.getInt("weight");
            String lastAddress = result.getString("last_address");
            String skills = result.getString("special_skills");
            int userEntered = result.getInt("user_entered");
            String author = result.getString("users.name");
            String eyeColor = result.getString("eye_color");
            String imageUrl = result.getString("image_url");
            RootObject rootObj = new RootObject(id, firstName, lastName, height, weight, lastAddress, skills, author, eyeColor, imageUrl, false);
            return rootObj;

        }
        return null;


    }
}
