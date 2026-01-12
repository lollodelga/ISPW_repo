package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.Tutor;
import ldg.progettoispw.model.User;

import java.util.ArrayList;
import java.util.List;

public class TutorSearchDAOMemory implements TutorSearchDAO {

    @Override
    public List<Tutor> findTutorsBySubject(String subject) throws DBException {
        List<Tutor> risultati = new ArrayList<>();

        // Normalizzo la stringa di ricerca
        String searchKey = subject.toLowerCase();

        // 1. Scorro la lista condivisa in UserDAOMemory (invece di MockDatabase)
        for (User u : UserDAOMemory.USERS_LIST) {

            // 2. Controllo se è un TUTOR
            if ("TUTOR".equalsIgnoreCase(u.getRole())) {

                // 3. Recupero le sue materie dalla mappa condivisa in UserDAOMemory
                List<String> materieTutor = UserDAOMemory.TUTOR_SUBJECTS.get(u.getEmail());

                if (materieTutor != null) {
                    boolean matchTrovato = false;

                    // 4. Controllo se insegna la materia cercata
                    for (String mat : materieTutor) {
                        if (mat.toLowerCase().contains(searchKey)) {
                            matchTrovato = true;
                            break;
                        }
                    }

                    // 5. Se c'è match, creo l'oggetto Tutor e lo aggiungo
                    if (matchTrovato) {
                        Tutor t = new Tutor(u.getEmail(), u.getName(), u.getSurname());

                        // Aggiungo tutte le materie al tutor per visualizzazione
                        for (String m : materieTutor) {
                            t.addMateria(m);
                        }

                        risultati.add(t);
                    }
                }
            }
        }

        return risultati;
    }
}