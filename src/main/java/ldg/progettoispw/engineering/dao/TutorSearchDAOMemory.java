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
        String searchKey = subject.toLowerCase();

        // 1. Scorro la lista condivisa
        for (User u : UserDAOMemory.USERS_LIST) {

            // Se non è un tutor, passo al prossimo (riduce annidamento)
            if (!"TUTOR".equalsIgnoreCase(u.getRole())) {
                continue;
            }

            // Recupero le materie
            List<String> materieTutor = UserDAOMemory.TUTOR_SUBJECTS.get(u.getEmail());

            // Se insegna la materia, lo creo e lo aggiungo
            if (materieTutor != null && insegnaMateria(materieTutor, searchKey)) {
                risultati.add(creaTutorCompleto(u, materieTutor));
            }
        }

        return risultati;
    }

    /**
     * Metodo helper per verificare se nella lista materie c'è quella cercata.
     * Riduce la complessità ciclomatica del metodo principale.
     */
    private boolean insegnaMateria(List<String> materieTutor, String searchKey) {
        for (String mat : materieTutor) {
            if (mat.toLowerCase().contains(searchKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Metodo helper per costruire l'oggetto Tutor e popolatorlo.
     */
    private Tutor creaTutorCompleto(User u, List<String> materieTutor) {
        Tutor t = new Tutor(u.getEmail(), u.getName(), u.getSurname());

        for (String m : materieTutor) {
            t.addMateria(m);
        }

        return t;
    }
}