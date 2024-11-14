package com.example.restapi.controller;

import com.example.restapi.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class FileController {
    private static final ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.INDENT_OUTPUT);
    public static void writeUserToFile(User user, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            String json = objectMapper.writeValueAsString(user);
            fileWriter.write(json + System.lineSeparator());
            System.out.println("Объект User успешно добавлен в файл в виде JSON.");
        } catch (IOException e) {
            System.err.println("Ошибка при записи объекта User в файл: " + e.getMessage());
        }
    }
    public static String readRandomStringFromFile(String filePath) {
        try{
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            Random random = new Random();
            int randomIndex = random.nextInt(lines.size());
            return lines.get(randomIndex);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении из файла: " + e.getMessage());
            return null;
        }
    }

}
