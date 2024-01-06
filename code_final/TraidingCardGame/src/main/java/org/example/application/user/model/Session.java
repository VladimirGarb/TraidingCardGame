package org.example.application.user.model;

public class Session {

    private String username;
    private String token;

    public Session() {
    }

    public Session(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public void setToken(String username){
        token = "Basic " + username + "-mtcgToken";
    }

    public void setUsername(String username){
        this.username = username;
    }
    public String getToken(){
        return token;
    }

    public String getUsername(){
        return username;
    }
}
