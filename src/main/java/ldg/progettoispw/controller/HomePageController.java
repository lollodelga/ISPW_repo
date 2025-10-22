package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.util.GControllerHome;

import java.util.logging.Logger;

public class HomePageController {
    private static final Logger logger = Logger.getLogger(HomePageController.class.getName());

    public void refreshUserData(GControllerHome gControllerHome) {
        try {
            UserBean user = LoginSessionManager.loadUserSession();
            if (user != null) {
                // preferisco passare la bean al grafico e poi fare i get sul controller grafico. 
                gControllerHome.updateUserData(
                        user.getName(),
                        user.getSurname(),
                        user.getBirthDate(),
                        user.getSubjects()
                );
            } else {
                logger.warning("Nessuna sessione utente trovata.");
            }
        } catch (Exception e) {
            logger.severe("Errore durante il caricamento dei dati utente: " + e.getMessage());
        }
    }

    // Logica di logout (disaccoppiata dal controller grafico)
    public void logout() {
        LoginSessionManager.clearSession();
        // Qui puoi aggiungere altre operazioni di business se necessario
    }
}
