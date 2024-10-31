package com.example.restapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {
    @NotBlank(message = "Login cannot be blank")
    @Pattern(regexp = ".*\\D.*", message = "Login cannot consist only of digits")
    private String login = null;

    @NotBlank(message = "Password cannot be blank")
    private String password = null;

    private String date = null;

    // Конструктор для GET-запроса
    public User() {
        this.login = "admin";
        this.password = "admin";
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Конструктор для POST-запроса
    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public String getDate() { return date; }
}