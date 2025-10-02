package ldg.progettoispw.controller;

import javafx.event.ActionEvent;
import ldg.progettoispw.model.applicativo.Registration;
import ldg.progettoispw.util.GController;

public class RegisterController {
    private final String[] registValues = new String[7];
    private final GController gController;
    private final ActionEvent event;

    /**
     * Costruttore: riceve riferimenti alla view (controller grafico e evento)
     * da passare al model per aggiornamenti futuri.
     */
    public RegisterController(GController gController, ActionEvent event) {
        this.gController = gController;
        this.event = event;
    }

    /**
     * Metodo chiamato dal controller grafico con i dati inseriti dall’utente.
     * Passa tutto al model (`Registration`) per i controlli e l’elaborazione.
     */
    public void register(String name, String surname, String birth, String email,
                         String password, String subjects, String role) {

        registValues[0] = name;
        registValues[1] = surname;
        registValues[2] = birth;
        registValues[3] = email;
        registValues[4] = password;
        registValues[5] = subjects;
        registValues[6] = role;

        Registration registration = new Registration(gController, event);
        registration.start(registValues);
    }
}