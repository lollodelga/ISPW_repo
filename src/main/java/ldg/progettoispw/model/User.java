package ldg.progettoispw.model;

import java.sql.Date;

public class User {
    private String name;
    private String surname;
    private Date birthDate;
    private String email;
    private String password;
    private String role;

    public User(String name, String surname, Date birthDate, String email, String password, String role) {
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getter e Setter
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

}