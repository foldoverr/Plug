package com.example.restapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

public class User {
    @NotBlank(message = "Login cannot be blank")
    @Pattern(regexp = ".*\\D.*", message = "Login cannot consist only of digits")
    private String login = null;

    @NotBlank(message = "Password cannot be blank")
    private String password = null;

    private Timestamp date = null;

    @NotBlank(message = "Login cannot be blank")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,6}$", message = "Email wrong")
    private String email = null;

    // Конструктор для GET-запроса
    public User() {
        this.login = "admin";
        this.password = "admin";
        //this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.email = "admin@example.com";
    }

    // Конструктор для POST-запроса
    public User(String login, String password) {
        this.login = login;
        this.password = password;
        //this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.email = "admin@example.com";
    }

    public User(String login, String password, Timestamp date, String email) {
        this.login = login;
        this.password = password;
        this.date = date;
        this.email = email;
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public Timestamp getDate() { return date; }
    public String getEmail() { return email; }
}