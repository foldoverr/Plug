package com.example.restapi.controller;

import com.example.restapi.model.User;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
@Validated
public class ApiController {

    @GetMapping("/static")
    public ResponseEntity<User> getStaticJson() {
        User user = new User(); // Создаем пользователя с login = "admin" и password = "admin"
        requestLatency();
        return ResponseEntity.ok(user);
    }

    // POST-запрос с использованием Map и ручной проверки
    @PostMapping("/login/map")
    public ResponseEntity<?> loginWithMap(@RequestBody Map<String, String> request) {
        if (request.size() > 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request contains unexpected fields");
        }

        String login = request.get("login");
        String password = request.get("password");

        if (login == null || login.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login cannot be blank");
        }

        if (!login.matches(".*\\D.*")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login cannot consist only of digits");
        }

        if (password == null || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password cannot be blank");
        }

        User user = new User(login, password);
        requestLatency();
        return ResponseEntity.ok(user);
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

    public void requestLatency(){
        try {
            TimeUnit.SECONDS.sleep(1 + (long) (Math.random() * 2)); // задержка от 1 до 2 секунд
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // восстанавливаем статус прерывания
            e.printStackTrace();
        }
    }
}