package com.example.restapi.controller;

import com.example.restapi.model.User;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
@Validated
public class ApiController {

    private static final String URL = "jdbc:postgresql://192.168.56.1:5432/mydatabase";
    private static final String USER = "myuser";
    private static final String PASSWORD = "mypassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @GetMapping("/static")
    public ResponseEntity<User> getStaticJson(@RequestParam String login) {
        User user = DBConnectionController.selectByLogin(login);
        requestLatency();
        if (user == null) {
            throw new UserNotFoundException("User with login '" + login + "' not found");
        } else {
            FileController.writeUserToFile(user,"insertFromDB.txt");
            return ResponseEntity.ok(user);}
    }

    @GetMapping("/RandomStringFromFile")
    public ResponseEntity<String> getRandomStringFromFile() {
        String str = null;
        str=FileController.readRandomStringFromFile("File.txt");
        requestLatency();
        return ResponseEntity.ok(str);
    }




    /* @GetMapping("/static")
    public ResponseEntity<User> getStaticJson(@RequestParam String login) {
        User user = DBConnectionController.selectByLogin(login);
        requestLatency();
        if (user == null) {
            throw new UserNotFoundException("User with login '" + login + "' not found");
        } else {
        return ResponseEntity.ok(user);}
    }

    // POST-запрос с использованием Map и ручной проверки
    @PostMapping("/login/map")
    public ResponseEntity<?> loginWithMap(@RequestBody Map<String, String> request) {
        if (request.size() > 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request contains unexpected fields");
        }
        int rowsUpdated = 0;
        String login = request.get("login");
        String password = request.get("password");
        String email = request.get("email");
        Timestamp date = Timestamp.valueOf(request.get("date"));

        if (login == null || login.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login cannot be blank");
        }

        if (!login.matches(".*\\D.*")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login cannot consist only of digits");
        }

        if (password == null || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password cannot be blank");
        }

        User user = new User(login, password, date, email);
        rowsUpdated = DBConnectionController.insertUser(user);
        requestLatency();
        return ResponseEntity.ok(rowsUpdated);
    }

    // POST-запрос с использованием валидации
    @PostMapping("/login/valid")
    public ResponseEntity<User> loginWithValidation(@Valid @RequestBody User user) {
        requestLatency();
        return ResponseEntity.ok(user);
    }
    */


    // Обработчик ошибок валидации
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationExceptions(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error: " + ex.getMessage());
    }

    public class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public void requestLatency(){
        try {
            TimeUnit.SECONDS.sleep(1 + (long) (Math.random() * 2)); // задержка от 1 до 2 секунд
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // восстанавливаем статус прерывания
            e.printStackTrace();
        }
    }
}