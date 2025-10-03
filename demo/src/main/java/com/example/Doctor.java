package com.example;

import java.sql.*;

public class Doctor {
    private Connection connection;

    public Doctor(Connection connection) {
        this.connection = connection;
    }

    public void viewDoctors() {
        String query = "SELECT * FROM doctors";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("Doctor List:");
            System.out.println("+-----------+--------------------+----------------------+");
            System.out.println("| Doctor ID | Name               | Specialization       |");
            System.out.println("+-----------+--------------------+----------------------+");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");  // ✅ fixed
                String name = resultSet.getString("NAME");  // ✅ fixed
                String specialization = resultSet.getString("SPECIALIZATION");  // ✅ fixed

                System.out.printf("| %-9d | %-18s | %-20s |%n", id, name, specialization);
                System.out.println("+-----------+--------------------+----------------------+");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error while fetching doctors.");
            e.printStackTrace();
        }
    }

    public boolean getDoctorById(int doctorId) {
        String query = "SELECT * FROM doctors WHERE id = ?"; // ✅ fixed
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("❌ Error while searching doctor by ID.");
            e.printStackTrace();
        }
        return false;
    }
}
