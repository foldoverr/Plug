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

    private static final String URL = "jdbc:postgresql://localhost:5432/mydatabase";
    private static final String USER = "myuser";
    private static final String PASSWORD = "mypassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @GetMapping("/static")
    public ResponseEntity<User> getStaticJson(@RequestParam String login) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        User user = null;
        try{
            connection = getConnection();
            statement = connection.createStatement();
            String query = "SELECT u.login, u.password, e.email, u.date " +
                    "FROM user_login u LEFT JOIN user_email e ON u.login = e.login " +
                    "WHERE u.login = '" + login + "'";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                Timestamp date = resultSet.getTimestamp("date");
                user = new User(login, password, date, email);
            }
            if (user == null) {
                throw new UserNotFoundException("User with login '" + login + "' not found");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        requestLatency();
        return ResponseEntity.ok(user);
    }

    // POST-запрос с использованием Map и ручной проверки
    @PostMapping("/login/map")
    public ResponseEntity<?> loginWithMap(@RequestBody Map<String, String> request) {
        if (request.size() != 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request contains unexpected fields");
        }

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
        String insertUserLogin = "INSERT INTO user_login (login, password, date) VALUES (?, ?, ?); \n INSERT INTO user_email (login, email) VALUES (?, ?);";
        int rowsUpdated = 0;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(insertUserLogin)) {
            connection.setAutoCommit(false);
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setTimestamp(3, user.getDate());
            statement.setString(4, user.getLogin());
            statement.setString(5, user.getEmail());
            rowsUpdated = statement.executeUpdate();
            connection.commit();  // Завершаем транзакцию
        } catch (SQLException e) {
            e.printStackTrace();
        }
        requestLatency();
        return ResponseEntity.ok(rowsUpdated);
    }

    // POST-запрос с использованием валидации
    @PostMapping("/login/valid")
    public ResponseEntity<User> loginWithValidation(@Valid @RequestBody User user) {
        requestLatency();
        return ResponseEntity.ok(user);
    }



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