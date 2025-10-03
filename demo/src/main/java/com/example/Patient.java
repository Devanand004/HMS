package com.example;

import java.sql.*;
import java.util.Scanner;

public class Patient {
    private final Connection connection;
    private final Scanner scanner;

    public Patient(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    // Add a new patient into the database
    public void addPatient() {
        try {
            System.out.print("Enter Patient Name: ");
            scanner.nextLine(); // clear buffer
            String name = scanner.nextLine();

            System.out.print("Enter Patient Age: ");
            int age = scanner.nextInt();
            scanner.nextLine(); // clear buffer

            System.out.print("Enter Patient Gender: ");
            String gender = scanner.nextLine();

            String sql = "INSERT INTO patients(name, age, gender) VALUES(?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setInt(2, age);
                stmt.setString(3, gender);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("✅ Patient added successfully!");
                } else {
                    System.out.println("⚠️ Could not add patient.");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error while adding patient.");
            e.printStackTrace();
        }
    }

    // Display all patients
    public void viewPatients() {
        String sql = "SELECT * FROM patients";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\nPatients List:");
            System.out.println("+------------+--------------------+----------+------------+");
            System.out.println("| Patient Id | Name               | Age      | Gender     |");
            System.out.println("+------------+--------------------+----------+------------+");

            while (rs.next()) {
                int id = rs.getInt("id"); // change to "patient_id" if needed
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");

                System.out.printf("| %-10d | %-18s | %-8d | %-10s |%n", id, name, age, gender);
                System.out.println("+------------+--------------------+----------+------------+");
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error while fetching patients.");
            e.printStackTrace();
        }
    }

    // Check if a patient exists by ID
    public boolean getPatientById(int id) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error while searching patient.");
            e.printStackTrace();
        }
        return false;
    }
}
