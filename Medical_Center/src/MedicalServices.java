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
    private List<String> genderList = new ArrayList<>(List.of("Female", "Male", "Other"));
    public Connection getDatabaseConnection() {
        String url = "jdbc:postgresql://pgserver.mau.se:5432/amak_medical_center";
        String user = "am5914";
        String password = "9gzbaurf";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
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

    public void listAllMedicalRecordsOfPatient(String medicalNum) throws Exception {
        String query = "SELECT * FROM fn_list_p_medical_records(?)";
        try (Connection con = getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            // Set input parameters
            pstmt.setString(1, medicalNum);

            // Execute the query
            ResultSet rs = pstmt.executeQuery();
            System.out.println("---------------------------------Medical Records-------------------------------");
            // Get result for each medical record
            while (rs.next()) {
                String diagnosis = rs.getString("diagnosis");
                String prescription = rs.getString("prescription");
                String description = rs.getString("description");
                String doctor = rs.getString("doctor_full_name");
                Date recordDate = rs.getDate("record_date");

                System.out.printf("Diagnosis: %-14s Description: %-38s Prescription: %-16s sign. Dr. %-20s  %-10s %n",
                        diagnosis, description, prescription, doctor, recordDate);
            }
            System.out.println("-------------------------------------------------------------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error retrieving medical records.", e);
        }
        getDatabaseConnection().close();
    }

    void changeAvailability(String e_num, String day, Time time) throws Exception{
        try {
            // Establish a connection to the database
            Connection con = getDatabaseConnection();

            // Call the stored function
            String query = "{ ? = call fn_change_availability(?, ?, ?) }";
            CallableStatement callableStatement = con.prepareCall(query);

            // Set the function parameters
            callableStatement.registerOutParameter(1, java.sql.Types.BOOLEAN);
            callableStatement.setString(2, e_num);
            callableStatement.setString(3, day);
            callableStatement.setTime(4, time);

            // Execute the function
            callableStatement.execute();

            // Get the result
            Boolean result = callableStatement.getBoolean(1);

            if (result) {
                System.out.println("Availability change successful");
            } else {
                System.out.println("Availability change failed. Appointment already booked.");
            }

            // Close resources
            callableStatement.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void listAllUpcomingAppointments() throws Exception{
        String query = "SELECT * FROM fn_list_all_appointments()";
        List<Appointment> appointmentList = new ArrayList<>();

        try (Connection con = getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            // Execute the query
            ResultSet rs = pstmt.executeQuery();
            System.out.println("--------------------------------Upcoming Appointments--------------------------------");
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
            System.out.println("-------------------------------------------------------------------------------------");

        } catch (SQLException e ){
            e.printStackTrace();
            throw new Exception("Error retrieving appointments.", e);
        }
        getDatabaseConnection().close();
    }


    void listAllUpcomingAppointmentsOfDoctor(String eNum) throws Exception{
        String query = "SELECT * FROM fn_list_all_appointments_for_doctor(?)";

        try (Connection con = getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            // Set input parameters
            pstmt.setString(1, eNum);

            // Execute the query
            ResultSet rs = pstmt.executeQuery();
            System.out.println("----------------Upcoming Appointments----------------");
            // Get result for each medical record
            while (rs.next()) {
                String day = rs.getString("app_day");
                Time time = rs.getTime("app_time");
                String patient = rs.getString("patient_full_name");
                System.out.printf("%-10s %-12s Patient: %s%n", day, time, patient);
            }
            System.out.println("-----------------------------------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getDatabaseConnection().close();
    }

     void listDoctorsPatients(String eNum) throws Exception {
        String query = "SELECT * FROM fn_list_all_appointments_for_doctor(?)";

        try (Connection con = getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            // Set input parameters
            pstmt.setString(1, eNum);

            // Execute the query
            ResultSet rs = pstmt.executeQuery();
            System.out.println("-------Your patient list------");
            // Get result for each medical record
            while (rs.next()) {
                String mNum = rs.getString("medical_number");
                String patient = rs.getString("patient_full_name");

                System.out.print("Medical Number: " + mNum);
                System.out.println(", Patient: " + patient);
                System.out.println("--------------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
         getDatabaseConnection().close();
    }

    public void addMedicalRecord(String diagnosis, String description, String perscription, String mNum, String eNum) throws Exception{

        String query = "SELECT * FROM fn_add_medical_record(?,?,?,?,?)";
        try(Connection con = getDatabaseConnection();
            PreparedStatement pstmt = con.prepareStatement(query)){

            pstmt.setString(1, diagnosis);
            pstmt.setString(2, description);
            pstmt.setString(3, perscription);
            pstmt.setString(4, mNum);
            pstmt.setString(5, eNum);
            pstmt.execute();
        }catch (SQLException e ){
            e.printStackTrace();
            throw new Exception("Error adding medical record.", e);
        }
        getDatabaseConnection().close();
    }

    public void addPatient(String firstName, String lastName, int genderIndex, String address, String phone, Date birthDate)throws Exception{
        String gender = genderList.get(genderIndex);
        String mNum = nbrGenerator.Generate8DigitNbr();
        String query = "SELECT * FROM fn_add_patient(?,?,?,?,?,?,?)";
        try(Connection con = getDatabaseConnection();
            PreparedStatement pstmt = con.prepareStatement(query)){

            pstmt.setString(1, mNum);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, gender);
            pstmt.setString(5, address);
            pstmt.setString(6, phone);
            pstmt.setDate(7, birthDate);
            pstmt.execute();
        }catch (SQLException e ){
            e.printStackTrace();
            throw new Exception("Error adding patient.", e);
        }
        getDatabaseConnection().close();
    }
    public void printGenderList(){
        for (int i = 0; i < genderList.size(); i++) {
            System.out.println(i + " " + genderList.get(i));
        }
    }
    public void printSpecializationList(){

        for (int i = 0; i < specializationsList.size(); i++) {
            System.out.println(i + " " + specializationsList.get(i));
        }
    }
    public void printDoctorsOfSpecialization(int spec) throws Exception{
        String query = "SELECT * FROM fn_doctors_with_specialization(?)";
        String specialization = specializationsList.get(spec);
        try (Connection con = getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            // Set input parameters
            pstmt.setString(1, specialization);

            // Execute the query
            ResultSet rs = pstmt.executeQuery();
            System.out.println("-------Available doctors------");
            // Get result for each medical record
            while (rs.next()) {
                String doctor = rs.getString("full_name");
                String eNum = rs.getString("e_num");

                System.out.print("Employee Number: " + eNum);
                System.out.println(", Doctor: " + doctor);
                System.out.println("--------------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        getDatabaseConnection().close();
    }

    public void printDoctorsAvailabilities(String eNum) throws Exception{
        String query = "SELECT * FROM fn_list_availabilites(?)";

        try (Connection con = getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            // Set input parameters
            pstmt.setString(1, eNum);

            // Execute the query
            ResultSet rs = pstmt.executeQuery();
            System.out.println("-------Available appointments------");
            // Get result for each medical record
            while (rs.next()) {
                String day = rs.getString("out_day");
                Time time = rs.getTime("out_time");

                System.out.print("Day: " + day);
                System.out.println(", Time: " + time);
                System.out.println("--------------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        getDatabaseConnection().close();
    }

    public void bookAppointment(String eNum, String day, Time time, String mNum) throws Exception{

        String query = "SELECT * FROM fn_book_appointment(?,?,?,?)";
        try(Connection con = getDatabaseConnection();
            PreparedStatement pstmt = con.prepareStatement(query)){

            pstmt.setString(1, eNum);
            pstmt.setString(2, day);
            pstmt.setTime(3, time);
            pstmt.setString(4, mNum);
            pstmt.execute();
        }catch (SQLException e ){
            e.printStackTrace();
            throw new Exception("Error booking appointment.", e);
        }
        getDatabaseConnection().close();
    }

    public void updatePatientInformation(String mNum, String firstName, String lastName, String address, String phone)throws Exception{

        String query = "SELECT * FROM fn_update_patient_info(?,?,?,?,?)";
        try(Connection con = getDatabaseConnection();
            PreparedStatement pstmt = con.prepareStatement(query)){

            pstmt.setString(1, mNum);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, address);
            pstmt.setString(5, phone);

            pstmt.execute();
        }catch (SQLException e ){
            e.printStackTrace();
            throw new Exception("Error editing patient information.", e);
        }
        getDatabaseConnection().close();
    }

    public void listPatients() throws Exception {
    Connection con = getDatabaseConnection();
    String query = "select * from \"patient\"";
    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery(query);
    System.out.println("-----------------Registered Patients-----------------");
    while (rs.next()) {
        System.out.print("Medical Number: " + rs.getString("medical_number"));
        System.out.print(", Name: " + rs.getString("first_name"));
        System.out.println(" " + rs.getString("last_name"));
    }
    System.out.println("-----------------------------------------------------");
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

    public void listDoctors()throws Exception{
        String query = "SELECT * FROM fn_list_doctors()";
        try (Connection con = getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            // Execute the query
            ResultSet rs = pstmt.executeQuery();
            System.out.println("--------------------------------Doctors--------------------------------");
            while (rs.next()) {
                String doctorID = rs.getString("doctor_id");
                String doctorName = rs.getString("doctor_name");
                boolean available = rs.getBoolean("available");
                if(available){
                    System.out.printf("Employee Number: %-12s Dr. %-20s Available: %b%n", doctorID, doctorName, available);
                }
                else{
                    System.out.printf("Employee Number: %-12s Dr. %-20s Available: %b%n", doctorID, doctorName, available);
                }
            }
            System.out.println("-----------------------------------------------------------------------");
        }catch (SQLException e ){
            e.printStackTrace();
            throw new Exception("Error retreiving doctors.", e);
        }
        getDatabaseConnection().close();
    }

    public void deleteDoctor(String doctorID) throws Exception {
        try (Connection con = getDatabaseConnection();
            CallableStatement cstmt = con.prepareCall("{ ? = call fn_delete_doctor(?) }")){

            //register return value
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            //register input values
            cstmt.setString(2, doctorID);
            cstmt.execute();

            //Get results
            boolean isDoctorDeleted = cstmt.getBoolean(1);
            if (isDoctorDeleted){
                System.out.println("Doctor successfully deleted from database.");
            }
            else{
                System.out.println("Doctor not deleted, there is dependencies connected to the doctor. \nChange: Doctor is not available for appointments!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error deleting doctors.", e);
        }
    }

    public void listPatientsAndCost() throws Exception {
        String query = "SELECT * FROM fn_list_all_patients_with_total_costs()";
        try (Connection con = getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            // Execute the query
            ResultSet rs = pstmt.executeQuery();
            System.out.println("-------------------------Patients & Cost-------------------------");
            while (rs.next()) {
                String patientID = rs.getString("medical_number");
                String patientName = rs.getString("full_name");
                int cost = rs.getInt("total_cost");
                System.out.printf("Medical Number: %-12s %-20s Total: %s kr%n", patientID, patientName, cost);
            }
            System.out.println("-----------------------------------------------------------------");
        }catch (SQLException e ){
            e.printStackTrace();
            throw new Exception("Error retreiving doctors.", e);
        }
        getDatabaseConnection().close();
    }

    public void listDiagnosisAndPrescription(String mNum) throws Exception {
        String query = "SELECT * FROM get_diagnosis_and_prescription(?)";
        try (Connection con = getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            //input parameter
            pstmt.setString(1, mNum);

            // Execute the query
            ResultSet rs = pstmt.executeQuery();
            System.out.println("-----------------------Diagnosis and Prescription-----------------------");
            while (rs.next()) {
                String diagnosis = rs.getString("diagnosis");
                String prescription = rs.getString("prescription");
                System.out.printf("Diagnosis: %-20s Prescription: %s%n", diagnosis, prescription);            }
            System.out.println("-----------------------------------------------------------------------");
        }catch (SQLException e ){
            e.printStackTrace();
            throw new Exception("Error retrieving diagnosis and prescription.", e);
        }
        getDatabaseConnection().close();
    }
}
