package com.example;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    // Update DB name in URL (currently missing)
    private static final String URL = "jdbc:mysql://localhost:3306/hospital"; 
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        // Load MySQL driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found.");
            e.printStackTrace();
            return;
        }

        // Connect to database
        try (Scanner scanner = new Scanner(System.in);
             Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {

            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            // Main menu loop
            while (true) {
                System.out.println("\n====== HOSPITAL MANAGEMENT SYSTEM ======");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("👉 Enter your choice: ");

                int choice;
                try {
                    choice = scanner.nextInt();
                } catch (Exception e) {
                    System.out.println("⚠️ Invalid input, please enter a number.");
                    scanner.nextLine(); // clear buffer
                    continue;
                }

                scanner.nextLine(); // clear buffer

                switch (choice) {
                    case 1 -> patient.addPatient();
                    case 2 -> patient.viewPatients();
                    case 3 -> doctor.viewDoctors();
                    case 4 -> bookAppointment(patient, doctor, connection, scanner);
                    case 5 -> {
                        System.out.println("🙏 Thank you for using Hospital Management System!");
                        return;
                    }
                    default -> System.out.println("⚠️ Please enter a valid choice (1–5).");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Database connection error.");
            e.printStackTrace();
        }
    }

    // Book an appointment between a patient and a doctor
    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter Patient ID: ");
            int patientId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter Doctor ID: ");
            int doctorId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
            String appointmentDate = scanner.nextLine();

            // Validate patient & doctor existence
            if (!patient.getPatientById(patientId)) {
                System.out.println("⚠️ Patient not found!");
                return;
            }
            if (!doctor.getDoctorById(doctorId)) {
                System.out.println("⚠️ Doctor not found!");
                return;
            }

            // Check doctor availability
            if (!checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                System.out.println("⚠️ Doctor is not available on this date.");
                return;
            }

            // Insert appointment
            String sql = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, patientId);
                stmt.setInt(2, doctorId);
                stmt.setString(3, appointmentDate);

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("✅ Appointment booked successfully!");
                } else {
                    System.out.println("⚠️ Failed to book appointment.");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error while booking appointment.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("⚠️ Invalid input. Please try again.");
            scanner.nextLine(); // clear buffer
        }
    }

    // Check if doctor is free on given date
    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            stmt.setString(2, appointmentDate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // available if no appointments
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error while checking doctor availability.");
            e.printStackTrace();
        }
        return false;
    }
}
