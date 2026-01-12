package ldg.progettoispw.model;

import java.sql.Date;
import java.sql.Time;

public class Appointment {
    private int id;
    private String studenteEmail;
    private String tutorEmail;
    private Date data;
    private Time ora;
    private String stato;

    public Appointment() {}

    public Appointment(int id, String studenteEmail, String tutorEmail, Date data, Time ora, String stato) {
        this.id = id;
        this.studenteEmail = studenteEmail;
        this.tutorEmail = tutorEmail;
        this.data = data;
        this.ora = ora;
        this.stato = stato;
    }

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudenteEmail() { return studenteEmail; }
    public void setStudenteEmail(String studenteEmail) { this.studenteEmail = studenteEmail; }

    public String getTutorEmail() { return tutorEmail; }
    public void setTutorEmail(String tutorEmail) { this.tutorEmail = tutorEmail; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public Time getOra() { return ora; }
    public void setOra(Time ora) { this.ora = ora; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
}