/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Aines;
import tikape.runko.domain.Ainesohje;
import tikape.runko.domain.Smoothie;

public class SmoothieDao implements Dao<Smoothie, Integer> {

    private Database database;

    public SmoothieDao(Database database) {
        this.database = database;
    }

    @Override
    public Smoothie findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Annos WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        Smoothie s = new Smoothie(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return s;
    }

    public void lisaaAinesohje(Integer smoothieId, Ainesohje ainesohje) throws SQLException {

        try (Connection conn = database.getConnection()) { 
            // katsotaan löytyykö aines jo smoothiesta
            PreparedStatement stmt1 = conn.prepareStatement("SELECT * FROM AnnosRaakaAine WHERE annos_id = ? AND raaka_aine_id = ?");
            stmt1.setInt(1, smoothieId);
            stmt1.setInt(2, ainesohje.getAines().getId());
            ResultSet rs = stmt1.executeQuery();
            Boolean exists = rs.next();

            if (exists) {
                // jos löytyy päivitetään
                PreparedStatement stmt = conn.prepareStatement("UPDATE AnnosRaakaAine SET jarjestys = ? , maara = ?, ohje = ? WHERE annos_id = ? AND raaka_aine_id = ?");
                stmt.setInt(1, ainesohje.getJarjestys());
                stmt.setString(2, ainesohje.getMaara());
                stmt.setString(3, ainesohje.getOhje());
                stmt.setInt(4, smoothieId);
                stmt.setInt(5, ainesohje.getAines().getId());
                stmt.executeUpdate();

            } else {
                // tai lisätään
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO AnnosRaakaAine (jarjestys, maara, ohje, annos_id, raaka_aine_id) VALUES (?,?,?,?,?)");
                stmt.setInt(1, ainesohje.getJarjestys());
                stmt.setString(2, ainesohje.getMaara());
                stmt.setString(3, ainesohje.getOhje());
                stmt.setInt(4, smoothieId);
                stmt.setInt(5, ainesohje.getAines().getId());
                stmt.executeUpdate();
            }

        }

    }

    public List<Ainesohje> haeAinesohjeet(Integer smoothieID) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM AnnosRaakaAine LEFT JOIN RaakaAine ON Raaka_Aine_id = id WHERE Annos_id = ? ORDER BY jarjestys");
        stmt.setInt(1, smoothieID);

        ResultSet rs = stmt.executeQuery();
        List<Ainesohje> ainesohjeet = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");
            Integer jarjestys = rs.getInt("jarjestys");
            String maara = rs.getString("maara");
            String ohje = rs.getString("ohje");
            Aines aines = new Aines(id, nimi);

            ainesohjeet.add(new Ainesohje(jarjestys, aines, maara, ohje));
        }

        rs.close();
        stmt.close();

        connection.close();

        return ainesohjeet;
    }

    @Override
    public List<Smoothie> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Annos");

        ResultSet rs = stmt.executeQuery();
        List<Smoothie> smoothiet = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            smoothiet.add(new Smoothie(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return smoothiet;
    }

    public Smoothie saveOrUpdate(Smoothie object) throws SQLException {
        Smoothie byName = findByName(object.getNimi());

        if (byName != null) {
            return byName;
        }

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Annos (nimi) VALUES (?)");
            stmt.setString(1, object.getNimi());
            stmt.executeUpdate();
        }

        return findByName(object.getNimi());
    }

    private Smoothie findByName(String name) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, nimi FROM Annos WHERE nimi = ?");
            stmt.setString(1, name);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Smoothie(result.getInt("id"), result.getString("nimi"));
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM AnnosRaakaAine WHERE Annos_id = ?;DELETE FROM Annos WHERE id = ?");

        stmt.setInt(1, key);
        stmt.setInt(2, key);
        stmt.executeUpdate();

        stmt.close();
        conn.close();

    }
}
