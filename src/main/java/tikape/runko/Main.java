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
import tikape.runko.domain.Aines;
import tikape.runko.domain.Smoothie;

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

        get("/smoothiet", (req, res) -> {       //smoothielista
            HashMap map = new HashMap<>();
            map.put("smoothiet", smoothieDao.findAll()); // Virhe jossain täällä

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        get("/uusi", (req, res) -> {        //luo uusi smoothie -sivu
            HashMap map = new HashMap<>();
            map.put("smoothiet", smoothieDao.findAll());

            return new ModelAndView(map, "uusi");
        }, new ThymeleafTemplateEngine());
        
        Spark.post("/uusi", (req, res) -> {
            smoothieDao.saveOrUpdate(new Smoothie(null, req.queryParams("nimi")));

            res.redirect("/uusi");
            return "";
        });  //Lisätyt smoothieiden nimet ei tuu smoothielistaan, ainoastaan ohjelinkki tulee

        get("/ainekset", (req, res) -> {        //raaka-ainesivu
            HashMap map = new HashMap<>();
            map.put("ainekset", ainesDao.findAll());

            return new ModelAndView(map, "ainekset");
        }, new ThymeleafTemplateEngine());
        
        Spark.post("/ainekset", (req, res) -> {
            ainesDao.saveOrUpdate(new Aines(null, req.queryParams("nimi")));

            res.redirect("/ainekset");
            return "";
        }); //EI TOIMI - poistolinkeissä ei ole toiminnallisuutta eikä raaka-aineiden lisääminen lisää nimeä raaka-ainelistaan (lisää pelkän poistolinkin)

        get("/smoothiet/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("smoothie", smoothieDao.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "smoothie");
        }, new ThymeleafTemplateEngine());

        get("/palaaetusivulle", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");

            return new ModelAndView(map, "index");
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
