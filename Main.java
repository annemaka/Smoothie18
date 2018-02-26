package tikape.runko;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import spark.ModelAndView;
import spark.Spark;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.SmoothieDao;
import tikape.runko.database.AinesDao;

public class Main {

    public static void main(String[] args) throws Exception {
        if (System.getenv("PORT") != null) {
            Spark.port(Integer.valueOf(System.getenv("PORT")));
        }

        Database database = new Database("jdbc:sqlite:smoothie.db");
        database.init();

        SmoothieDao smoothieDao = new SmoothieDao(database);
        AinesDao ainesDao = new AinesDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/smoothiet", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("smoothiet", smoothieDao.findAll());

            return new ModelAndView(map, "smoothiet");
        }, new ThymeleafTemplateEngine());

        get("/uusi", (req, res) -> {
            HashMap map = new HashMap<>();
            //map.put("smoothiet", smoothieDao.findAll());

            return new ModelAndView(map, "uusi");
        }, new ThymeleafTemplateEngine());

        get("/ainekset", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("ainekset", ainesDao.findAll());

            return new ModelAndView(map, "ainekset");
        }, new ThymeleafTemplateEngine());

        get("/smoothiet/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("smoothie", smoothieDao.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "smoothie");
        }, new ThymeleafTemplateEngine());

    }

    public static Connection getConnection() throws Exception {
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        if (dbUrl != null && dbUrl.length() > 0) {
            return DriverManager.getConnection(dbUrl);
        }

        return DriverManager.getConnection("jdbc:sqlite:smoothie.db");
    }
}
