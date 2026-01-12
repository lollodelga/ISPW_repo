package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.User;
import java.util.ArrayList;

public class RegistrationDAOMemory implements RegistrationDAO {

    @Override
    public int checkInDB(User user) throws DBException {
        // 1. Controllo se esiste già nella lista condivisa
        for (User u : UserDAOMemory.USERS_LIST) {
            if (u.getEmail().equals(user.getEmail())) {
                return 1; // Utente già esistente
            }
        }

        // 2. Se non esiste, lo aggiungo
        UserDAOMemory.USERS_LIST.add(user);
        System.out.println("[MEMORY] Utente registrato: " + user.getEmail());

        return 0; // Successo
    }

    @Override
    public void insertSubject(String subject) throws DBException {
        System.out.println("[MEMORY] (Simulazione) Inserimento materia globale: " + subject);
    }

    @Override
    public void createAssociation(String email, String subject) throws DBException {
        // Se non esiste ancora una lista per questo tutor, creala
        UserDAOMemory.TUTOR_SUBJECTS.putIfAbsent(email, new ArrayList<>());

        // Aggiungi la materia alla lista di quel tutor
        UserDAOMemory.TUTOR_SUBJECTS.get(email).add(subject);

        System.out.println("[MEMORY] Associato " + subject + " a " + email);
    }
}