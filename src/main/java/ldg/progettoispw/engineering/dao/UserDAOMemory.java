package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.User;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAOMemory implements UserDAO {

    // 1. Modificato "public" in "protected" (Risolve Code Smell: Visibility)
    // Essendo nello stesso package, gli altri DAO (Login/Registration) possono ancora accedervi.
    protected static final List<User> USERS_LIST = new ArrayList<>();
    protected static final Map<String, List<String>> TUTOR_SUBJECTS = new HashMap<>();

    // --- BLOCCO STATICO: Carica i dati finti all'avvio ---
    static {
        // 1. Studente Mario
        User s = new User("Mario", "Rossi", Date.valueOf("2000-01-01"), "studente@demo.it", "password", "STUDENTE");
        USERS_LIST.add(s);

        // 2. Tutor Luigi
        User t = new User("Luigi", "Verdi", Date.valueOf("1990-05-15"), "tutor@demo.it", "password", "TUTOR");
        USERS_LIST.add(t);

        // 3. Materie Tutor Luigi
        List<String> mats = new ArrayList<>();
        mats.add("Matematica");
        mats.add("Fisica");
        TUTOR_SUBJECTS.put("tutor@demo.it", mats);

        // 2. Rimosso System.out per pulizia CLI (Risolve Code Smell: Logger/Print)
    }

    @Override
    public String[] takeData(String email, String password) throws DBException {
        String[] data = new String[6];

        // Scorro la lista interna statica
        for (User u : USERS_LIST) {
            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {

                data[0] = u.getName();
                data[1] = u.getSurname();

                if (u.getBirthDate() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    data[2] = sdf.format(u.getBirthDate());
                } else {
                    data[2] = null;
                }

                data[3] = u.getEmail();
                data[4] = u.getPassword();
                data[5] = u.getRole();

                return data;
            }
        }
        return data; // Utente non trovato, ritorno array vuoto
    }

    @Override
    public String takeSubjects(String email) throws DBException {
        // Cerco nella mappa interna statica
        List<String> subjects = TUTOR_SUBJECTS.get(email);

        if (subjects != null && !subjects.isEmpty()) {
            return String.join(", ", subjects);
        }
        return "";
    }

    // 3. Rimosso 'throws DBException' (Risolve Code Smell: Exception not thrown)
    // Il metodo lavora in RAM, quindi non può generare errori DB.
    // Java permette all'implementazione di non dichiarare eccezioni dell'interfaccia se non le usa.
    public void registerUser(String email, String password, String ruolo, String nome, String cognome, String dataNascita) {
        User u = new User(nome, cognome, Date.valueOf(dataNascita), email, password, ruolo);
        USERS_LIST.add(u);
        if("TUTOR".equalsIgnoreCase(ruolo)) {
            TUTOR_SUBJECTS.put(email, new ArrayList<>());
        }
    }
}