package com.example.restapi;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/static")
    public LoginResponse getStaticJson() {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try {
            TimeUnit.SECONDS.sleep(1 + (long) (Math.random() * 2)); // задержка от 1 до 2 секунд
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // восстанавливаем статус прерывания
            e.printStackTrace();
        }
            return new LoginResponse("admin", "admin", currentDateTime);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try {
            TimeUnit.SECONDS.sleep(1 + (long) (Math.random() * 2)); // задержка от 1 до 2 секунд
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // восстанавливаем статус прерывания
            e.printStackTrace();
        }
        return new LoginResponse(request.getLogin(), request.getPassword(), currentDateTime);
    }

    public static class LoginRequest {
        private String login;
        private String password;

        // Getters and setters
        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String login;
        private String password;
        private String date;

        public LoginResponse(String login, String password, String date) {
            this.login = login;
            this.password = password;
            this.date = date;
        }

        // Getters
        public String getLogin() { return login; }
        public String getPassword() { return password; }
        public String getDate() { return date; }
    }
}