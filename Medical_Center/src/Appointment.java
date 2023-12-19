import java.sql.Time;
public class Appointment
{
    private String day;
    private String doctor;
    private String patient;
    private Time time;

    public Appointment(String day, String doctor, String patient, Time time){
        this.day = day;
        this.time = time;
        this.doctor = doctor;
        this.patient = patient;
    }
    public String getDay() {
        return day;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getPatient() {
        return patient;
    }

    public Time getTime() {
        return time;
    }
    @Override
    public String toString() {
        return "Appointment: " +
                "day = '" + day + '\'' +
                ", time = " + time +'\'' +
                ", patient = '" + patient + '\'' +
                ", doctor = '" + doctor;

    }
}
