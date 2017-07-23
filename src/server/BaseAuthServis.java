package server;


import java.util.ArrayList;

public class BaseAuthServis implements AuthServic {

    class Entry {
        private String login;
        private String pass;
        private String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }

    private ArrayList<Entry> entries;

    public BaseAuthServis() {
        entries = new ArrayList<>();
        entries.add(new Entry("login1", "pass1", "nick1"));
        entries.add(new Entry("login2", "pass2", "nick2"));
        entries.add(new Entry("login3", "pass3", "nick3"));
    }

    @Override
    public String getNickLoginPass(String login, String pass) {
        for (Entry as : entries) {
            if (login.endsWith(as.login) && pass.equals(as.pass)) return as.nick;
        }
        return null;
    }
}
