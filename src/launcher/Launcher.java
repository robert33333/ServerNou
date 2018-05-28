package launcher;

import date.Bilet;
import date.Meci;
import date.Pariu;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;

class Launcher {
    private static Connection con;

    public static void validareBilet(int id) {
        String sql_update = "update bilete set validat = 'true' where id = ?";
        try(PreparedStatement ps = con.prepareStatement(sql_update)) {
            ps.setInt(1, id);
            ps.executeQuery();
            Launcher.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void commit() {
        try(Statement stmt = con.createStatement()){
            stmt.executeQuery("commit");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Meci> selectMeciuri() throws SQLException {
        try (Statement stmt=con.createStatement();
             ResultSet rs=stmt.executeQuery("select * from meciuri order by 1")) {

            ArrayList<Meci> meciuri = new ArrayList<>();
            while (rs.next()) {
                Meci m = new Meci(rs.getInt("id"), rs.getString("echipa1"), rs.getString("echipa2"), rs.getDouble("cota1"), rs.getDouble("cotax"),
                        rs.getDouble("cota2"));
                m.setRezultat(rs.getString("rezultat"));
                meciuri.add(m);
            }
            return meciuri;
        }
    }

    public static void insertMeci(Meci meci) throws SQLException {
        try(
        Statement stmt = con.createStatement();
        ResultSet rs=stmt.executeQuery("select max(id) from meciuri")
        ) {
            rs.next();
            int id = rs.getInt(1) + 1;
            String sql_isnert = "insert into meciuri" + "(id, echipa1, echipa2, cota1, cotax, cota2) values" + "(?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(sql_isnert)) {
                ps.setInt(1, id);
                ps.setString(2, meci.getEchipa1());
                ps.setString(3, meci.getEchipa2());
                ps.setDouble(4, meci.getCota1());
                ps.setDouble(5, meci.getCotax());
                ps.setDouble(6, meci.getCota2());
                ps.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Launcher.commit();
        }
    }

    public static ArrayList<Bilet> selectBilete() throws SQLException {
        try (Statement stmt=con.createStatement();
             ResultSet rs=stmt.executeQuery("select * from bilete order by 1")) {
            ArrayList<Bilet> bilete = new ArrayList<>();
            while (rs.next()) {
                ArrayList<Pariu> pariuriBilet = Launcher.selectPariuri(rs.getInt("id"));
                Bilet b = new Bilet(rs.getInt("id"), pariuriBilet, rs.getDouble("miza"));
                if (rs.getString("validat").equals("true"))
                    b.setDejaValidat();
                bilete.add(b);
            }
            return bilete;
        }
    }

    public static void insertBilet(Bilet obj) throws SQLException {
        int id;
        try(
                Statement stmt = con.createStatement();
                ResultSet rs=stmt.executeQuery("select max(id) from bilete")
        ) {
            rs.next();
            id = rs.getInt(1) + 1;
            String sql_insert = "insert into bilete" + "(id, miza, validat) values" + "(?, ?, 'false')";

            try (PreparedStatement ps = con.prepareStatement(sql_insert)) {
                ps.setInt(1, id);
                ps.setDouble(2, obj.getBani());
                ps.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Launcher.commit();
        }

        ArrayList<Pariu> pariuri = obj.getPariuri();
        for(Pariu p : pariuri) {
            Launcher.insertPariu(id, p);
        }
    }

    private static void insertPariu(int idBilet, Pariu p) throws SQLException {
        try(
                Statement stmt = con.createStatement();
                ResultSet rs=stmt.executeQuery("select max(id) from pariuri")
        ) {
            rs.next();
            int id = rs.getInt(1) + 1;
            String sql_isnert = "insert into pariuri" + "(id, id_meci, id_bilet, optiune) values" + "(?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql_isnert)) {
                ps.setInt(1, id);
                ps.setInt(2, p.getMeci().getId());
                ps.setInt(3, idBilet);
                ps.setString(4, p.getOptiune());
                ps.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Launcher.commit();
        }
    }

    private static ArrayList<Pariu> selectPariuri(int idBilet) throws SQLException {
        String sql= "select * from pariuri where id_bilet = ?";
        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBilet);
            try(ResultSet rs = ps.executeQuery()) {
                ArrayList<Pariu> pariuri = new ArrayList<>();
                while(rs.next()){
                    Meci m = Launcher.selectMeci(rs.getInt("id_meci"));
                    Pariu p = new Pariu(m, rs.getString("optiune"));
                    pariuri.add(p);
                }
                return pariuri;
            }
        }
    }

    private static Meci selectMeci(int id) throws SQLException {
        String sql= "select * from meciuri where id = ?";
        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                rs.next();
                Meci m = new Meci(rs.getInt("id"), rs.getString("echipa1"), rs.getString("echipa2"), rs.getDouble("cota1"), rs.getDouble("cotax"),
                        rs.getDouble("cota2"));
                m.setRezultat(rs.getString("rezultat"));
                return m;
            }
        }
    }

    public static void updateMeci(Meci meci) {
        String sql_update = "update meciuri set rezultat = ? where id = ?";
        try(PreparedStatement ps = con.prepareStatement(sql_update)) {
            ps.setString(1, meci.getRezultat());
            ps.setInt(2, meci.getId());
            ps.executeQuery();
            Launcher.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // Conectare si interogare baza de date
        Class.forName("oracle.jdbc.driver.OracleDriver");
        final String DB_URL = "jdbc:oracle:thin:@193.226.51.37:1521:o11g";
        final String DB_USER = "grupa42";
        final String DB_PASS = "grupa42";
        con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);


        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(9090);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                assert serverSocket != null;
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            UserThread T = new UserThread(socket);
            T.start();
        }
    }
}
