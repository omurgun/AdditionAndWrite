package com.omurgun.additionandwrite.Model;

public class User implements Comparable<User>{
    private String username;
    private String email;
    private String maxScore;

    public User(String username, String email, String maxScore) {
        this.username = username;
        this.email = email;
        this.maxScore = maxScore;
    }

    public User(String username, String maxScore) {
        this.username = username;
        this.maxScore = maxScore;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore;
    }

    @Override
    public int compareTo(User o) {
        if(Integer.parseInt(maxScore)==Integer.parseInt(o.getMaxScore()))
            return 0;
        else if(Integer.parseInt(maxScore)<Integer.parseInt(o.getMaxScore()))
            return 1;
        else
            return -1;
    }
}

