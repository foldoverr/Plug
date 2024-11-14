package com.example.restapi.controller;

import com.example.restapi.model.User;

import java.sql.*;

public class DBConnectionController {
    private static final String URL = "jdbc:postgresql://192.168.56.1:5432/mydatabase";
    private static final String USER = "myuser";
    private static final String PASSWORD = "mypassword";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static User selectByLogin(String login){
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
                return user;
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
        return user;
    }

    public static int insertUser(User user){
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
        return rowsUpdated;
    }
}
