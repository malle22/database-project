import java.sql.*;

public class MedicalServices {
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
                System.out.println("while");
                String diagnosis = rs.getString("diagnosis");
                String prescription = rs.getString("prescription");
                String description = rs.getString("description");
                String patient = rs.getString("patient_full_name");
                String doctor = rs.getString("doctor_full_name");
                String employeeNbr = rs.getString("employee_number");
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

}
