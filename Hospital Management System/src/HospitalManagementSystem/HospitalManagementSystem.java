package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem
{

    private static final String url = "jdbc:mysql://localhost:3306/hospital";

    private static final String username = "root";

    private static final String password = "karthick";

    public static void main(String[] args)
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");

        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        Scanner sc = new Scanner(System.in);
        try
        {
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient =  new Patient(connection,sc);
            Doctor doctor = new Doctor(connection);
            while (true)
            {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1.Add patient");
                System.out.println("2.View Patients");
                System.out.println("3.View Doctors");
                System.out.println("4.Book Appointment");
                System.out.println("5.View Appointment");
                System.out.println("6.Exit");
                System.out.print("Enter Choice: ");
                int c = sc.nextInt();

                switch (c)
                {
                    case 1:
                        patient.addpatient();
                        System.out.println();
                        break;

                    case 2:
                        patient.viewPatients();
                        System.out.println();
                        break;

                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patient,doctor,connection,sc);
                        System.out.println();
                        break;

                    case 5:
                        viewAppointments(connection);
                        System.out.println();
                        break;
                    case 6:
                        System.out.println("Thank You! for Using My Website");
                        return;
                    default:
                        System.out.println("Invalid Choise");
                        break;
                }
            }


        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection,Scanner sc) {
        System.out.print("Enter Patient Id: ");
        int patientId = sc.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = sc.nextInt();
        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String appointmentDate = sc.next();
        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate , connection)) {
                String appointmentQuery = "INSERT INTO appointments(patient_id , doctor_id , appointment_date) VALUES (?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Appointment Book Successfully");
                    } else {
                        System.out.println("Failed to Book Appointment!");
                    }
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                System.out.println("Doctor not available on this date!");
            }
        }
        else
        {
            System.out.println("Either Doctor or Patient Doesn't Exit ");
        }
    }
    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection)
    {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
            {
                int count = resultSet.getInt(1);
                if(count==0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    public static void viewAppointments(Connection connection) {
        String query = "SELECT a.id, p.name AS patient, d.name AS doctor, d.specialization, a.appointment_date " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "JOIN doctors d ON a.doctor_id = d.id " +
                "ORDER BY a.appointment_date";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n----- APPOINTMENT LIST -----");
            System.out.printf("%-5s %-20s %-20s %-20s %-15s\n",
                    "ID", "Patient", "Doctor", "Specialization", "Date");
            System.out.println("----|---------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d %-20s %-20s %-20s %-15s\n",
                        rs.getInt("id"),
                        rs.getString("patient"),
                        rs.getString("doctor"),
                        rs.getString("specialization"),
                        rs.getString("appointment_date"));
                System.out.println("----|----------------------------------------------------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

