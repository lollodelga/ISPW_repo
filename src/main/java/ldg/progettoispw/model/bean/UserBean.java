package ldg.progettoispw.model.bean;


public class UserBean {
    private String name;
    private String surname;
    private String birthDate;
    private String email;
    private String password;
    private String subjects;

    public UserBean() {/*costruttore di default*/}

    // Getters e setters
    public String getName() { return name; }

    public String getSurname() { return surname; }

    public String getBirthDate() { return birthDate; }

    public String getSubjects() { return subjects; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

      // Per salvare su file
    public String[] getArray() {
        return new String[]{name, surname, birthDate, email, password, subjects};
    }

    // Per caricare da file
    public void setOfAll(String[] data) {
        if (data.length != 6) {
            throw new IllegalArgumentException("Dati utente incompleti");
        }
        this.name = data[0];
        this.surname = data[1];
        this.birthDate = data[2];
        this.email = data[3];
        this.password = data[4];
        this.subjects = data[5];
    }
}