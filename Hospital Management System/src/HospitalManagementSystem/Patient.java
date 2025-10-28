package HospitalManagementSystem;

import java.net.CookieHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Patient {
    private Connection connection;
    private Scanner sc;

    public Patient(Connection connection , Scanner sc)
    {
        this.connection = connection;
        this.sc=sc;
    }

    public void addpatient()
    {
        sc.nextLine();
        System.out.print("Enter patient name: ");
        String name = sc.nextLine();
        System.out.print("Enter Patient Age: ");
        int age=sc.nextInt();
        System.out.print("Enter Patient Gender: ");
        String gender = sc.next();

        try
        {
            String query = "INSERT INTO patients(name,age,gender) VALUES (? ,?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,name);
            preparedStatement.setInt(2,age);
            preparedStatement.setString(3,gender);
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows>0)
            {
                System.out.println("Patient Added Successfully");
            }
            else {
                System.out.println("Failed to Add Patient");
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    public void viewPatients() {
        String query = "select * from patients";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Patients: ");
            System.out.println("+-------------+--------------------+--------+---------------+");
            System.out.println("| Patient Id  | Name               | Age    | Gender        |");
            System.out.println("+-------------+--------------------+--------+---------------+");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("Name");
                int age = resultSet.getInt("Age");
                String gender = resultSet.getString("Gender");
                System.out.printf("|  %-11s| %-19s| %-7s| %-14s|\n",id,name,age,gender);
                System.out.println("+-------------+--------------------+--------+---------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getPatientById(int id)
    {
           String query = "SELECT * FROM patients WHERE id=?";
           try
           {
               PreparedStatement preparedStatement = connection.prepareStatement(query);
               preparedStatement.setInt(1,id);
               ResultSet resultSet = preparedStatement.executeQuery();
               if(resultSet.next())
               {
                   return true;
               }
               else
               {
                   return false;
               }
           }
           catch (SQLException e)
           {
               e.printStackTrace();
           }
           return false;
    }

}
