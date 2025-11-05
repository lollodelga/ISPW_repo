package ldg.progettoispw.model;

import java.sql.Date;
import java.sql.Time;

public class Appointment {
    private int id;
    private String studentEmail;
    private String tutorEmail;
    private Date date;
    private Time time;
    private String status;

    public Appointment() {}

    public Appointment(int id, String studentEmail, String tutorEmail, Date date, Time time, String status) {
        this.id = id;
        this.studentEmail = studentEmail;
        this.tutorEmail = tutorEmail;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getTutorEmail() { return tutorEmail; }
    public void setTutorEmail(String tutorEmail) { this.tutorEmail = tutorEmail; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Time getTime() { return time; }
    public void setTime(Time time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}