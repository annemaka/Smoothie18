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
import tikape.runko.domain.Smoothie;

public class AinesDao implements Dao<Aines, Integer> {

    private Database database;

    public AinesDao(Database database) {
        this.database = database;
    }

    @Override
    public Aines findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM RaakaAine WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        Aines a = new Aines(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return a;
    }

    @Override
    public List<Aines> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM RaakaAine");

        ResultSet rs = stmt.executeQuery();
        List<Aines> ainekset = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            ainekset.add(new Aines(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return ainekset;
    }

    public Aines saveOrUpdate(Aines object) throws SQLException {
        // simply support saving -- disallow saving if task with 
        // same name exists
        Aines byName = findByName(object.getNimi());

        if (byName != null) {
            return byName;
        }

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO RaakaAine (nimi) VALUES (?)");
            stmt.setString(1, object.getNimi());
            stmt.executeUpdate();
        }

        return findByName(object.getNimi());
    }

    private Aines findByName(String name) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, nimi FROM RaakaAine WHERE nimi = ?");
            stmt.setString(1, name);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }
            return new Aines(result.getInt("id"), result.getString("nimi"));
        }
    }
    
    public Integer monessakoEsiintyy(Aines object) throws SQLException {
        return 1;
    }
    
    // haetaan tilastosivun tilastot
    public List<Aines.AinesTilasto> haeTilastot() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT nimi, AVG(maara) as keskiarvo, "
                + "SUM(maara) as kokonaismaara, COUNT(*) as lukumaara "
                + "FROM AnnosRaakaAine LEFT JOIN RaakaAine "
                + "ON raaka_aine_id = id GROUP BY raaka_aine_id");

        ResultSet rs = stmt.executeQuery();
        List<Aines.AinesTilasto> tilastot = new ArrayList<>();
        while (rs.next()) {
            String nimi = rs.getString("nimi");
            Integer lukumaara = rs.getInt("lukumaara");
            Double keskiarvo = rs.getDouble("keskiarvo");
            Double kokonaismaara = rs.getDouble("kokonaismaara");
            
            tilastot.add(new Aines.AinesTilasto(nimi, lukumaara, keskiarvo, kokonaismaara));
   
        }

        rs.close();
        stmt.close();
        connection.close();

        return tilastot;
    
    }

    public void delete(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM AnnosRaakaAine WHERE raaka_aine_id = ?"); //aines poistuu samalla myös kaikista ohjeista
        PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM raakaaine WHERE id = ?");

        stmt.setInt(1, key);
        stmt1.setInt(1, key);
        stmt.executeUpdate();
        stmt1.executeUpdate();

        stmt.close();
        stmt1.close();
        conn.close();
    }

}
