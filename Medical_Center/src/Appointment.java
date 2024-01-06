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

    @Override
    public String toString() {
        return "Appointment: " +
                "day = '" + day + '\'' +
                ", time = " + time +'\'' +
                ", patient = '" + patient + '\'' +
                ", doctor = '" + doctor;

    }
}
