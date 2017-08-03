package server;

import java.sql.*;

public class DBAuthService implements AuthService {
    private Connection connection;
    private PreparedStatement ps;
    private PreparedStatement psChangeNick;
    private PreparedStatement psCheckNick;



    public void start() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
            ps = connection.prepareStatement("SELECT Nick FROM entries WHERE Login = ? AND Pass = ?;");
            psChangeNick = connection.prepareStatement("UPDATE entries SET Nick = ? WHERE Nick = ?;");
            psCheckNick = connection.prepareStatement("SELECT COUNT(*) FROM entries WHERE Nick = ?");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean changeNick(ClientHandler c, String newNick) {
        try {
            psCheckNick.setString(1, newNick);
            ResultSet rs = psCheckNick.executeQuery();
            rs.next();
            if(rs.getInt(1) == 0) {
                psChangeNick.setString(2, c.getName());
                psChangeNick.setString(1, newNick);
                psChangeNick.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        try {
            ps.setString(1, login);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stop() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
