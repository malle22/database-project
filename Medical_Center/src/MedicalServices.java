import Utilities.NumberGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MedicalServices {

    private Appointment appointment;
    private NumberGenerator nbrGenerator = new NumberGenerator();
    private List<String> specializationsList = new ArrayList<>();
    public Connection getDatabaseConnection() {
        String url = "jdbc:postgresql://pgserver.mau.se:5432/amak_medical_center";
        String user = "am5914";
        String password = "9gzbaurf";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection Established");
            return conn;
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            return null;
        }
    }

    // Call a database procedure
    public void addSpecialization(String specialization, int visitCost) throws Exception {
        Connection con = getDatabaseConnection();

        // PreparedStatement used to execute dynamic or parameterized SQL queries.
        // Can be used also for other things than calling stored procedures
        // See deleteEmployee() method below
        PreparedStatement pstmt = con.prepareStatement("call add_specialization(?,?)");
        pstmt.setString(1, specialization);
        pstmt.setInt(2, visitCost);
        pstmt.execute();
        pstmt.close();
        con.close();
    }

    // Call a database function

    public void listAllMedicalRecordsOfPatient(String medicalNum) throws Exception {
        String query = "SELECT * FROM fn_list_p_medical_records(?)";
        try (Connection con = getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            // Set input parameters
            pstmt.setString(1, medicalNum);

            // Execute the query
            ResultSet rs = pstmt.executeQuery();

            // Get result for each medical record
            while (rs.next()) {
                String diagnosis = rs.getString("diagnosis");
                String prescription = rs.getString("prescription");
                String description = rs.getString("description");
                String doctor = rs.getString("doctor_full_name");
                Date recordDate = rs.getDate("record_date");

                System.out.print("Diagnosis: " + diagnosis);
                System.out.print(", Description: " + description);
                System.out.print(", Prescription: " + prescription);
                System.out.print(", Doctor: " + doctor);
                System.out.println(", Record date: " + recordDate);
                System.out.println("--------------------------------");
            }
        } catch (SQLException e ){
            e.printStackTrace();
            throw new Exception("Error retrieving medical records.", e);
        }
        getDatabaseConnection().close();
    }

    public void listAllUpcomingAppointments() throws Exception{
        String query = "SELECT * FROM fn_list_all_appointments()";
        List<Appointment> appointmentList = new ArrayList<>();

        try (Connection con = getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            // Execute the query
            ResultSet rs = pstmt.executeQuery();

            // Get result for each booked appointment
            while (rs.next()) {
                String day = rs.getString("app_day");
                Time time = rs.getTime("app_time");
                String doctor = rs.getString("doctor_full_name");
                String patient = rs.getString("patient_full_name");
                //Add to a list containing appointment-objects
                appointmentList.add(appointment = new Appointment(day, doctor, patient, time));
            }
            //Print appointments
            for (Appointment appointment : appointmentList){
                System.out.println(appointment.toString());
            }

        } catch (SQLException e ){
            e.printStackTrace();
            throw new Exception("Error retrieving appointments.", e);
        }
        getDatabaseConnection().close();
    }
    public void printAllPatients() throws Exception {
    Connection con = getDatabaseConnection();
    String query = "select * from \"patient\"";
    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery(query);
    while (rs.next()) {
        System.out.print("Medical Number: " + rs.getString("medical_number"));
        System.out.print(", Name: " + rs.getString("first_name"));
        System.out.println(" " + rs.getString("last_name"));
        System.out.println("--------------------------------");
    }
    stmt.close();
    con.close();
    }

    public void listSpecializations() throws Exception{
        String query = "SELECT * FROM fn_list_specializations()";
        int counter = 0;
        if(!specializationsList.isEmpty()){
            specializationsList.clear();
        }
        try (Connection con = getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            // Execute the query
            ResultSet rs = pstmt.executeQuery();

            //Print specializations
            System.out.println("------Specializations------");
            while (rs.next()) {
                String spec = rs.getString("out_specialization");
                System.out.println(counter + ". " + spec);
                specializationsList.add(spec);
                counter++;
            }
            System.out.println("---------------------------");
        } catch (SQLException e ){
            e.printStackTrace();
            throw new Exception("Error retrieving specializations.", e);
        }
        getDatabaseConnection().close();
    }
    public void addDoctor(String firstName, String lastName, int spec, String phoneNbr) throws Exception{
        //input parameter is an index to point to which specialization was picked.
        String specialization = specializationsList.get(spec);
        String employeeNbr = nbrGenerator.Generate8DigitNbr();
        String query = "SELECT * FROM fn_add_doctor(?,?,?,?,?)";
        try(Connection con = getDatabaseConnection();
            PreparedStatement pstmt = con.prepareStatement(query)){

            pstmt.setString(1, employeeNbr);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, specialization);
            pstmt.setString(5, phoneNbr);
            pstmt.execute();
        }catch (SQLException e ){
            e.printStackTrace();
            throw new Exception("Error adding doctor.", e);
        }
        getDatabaseConnection().close();
    }

}
