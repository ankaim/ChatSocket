package server;


import java.sql.*;

public class DBAuthService implements AuthServic{
    private Connection con;
    private PreparedStatement ps;

    public void start(){
        try {
            con = DriverManager.getConnection("jdbc:sqlite:chat.db");
            ps = con.prepareStatement("SELECT nick FROM entries WHERE login = ? AND pass = ?;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void stop(){
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNickLoginPass(String login, String pass) {//метод получения ника
        try {
            ps.setString(1, login);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
