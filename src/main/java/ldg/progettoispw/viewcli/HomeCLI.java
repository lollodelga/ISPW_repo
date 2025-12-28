package ldg.progettoispw.viewcli;

import ldg.progettoispw.controller.HomePageController;
import ldg.progettoispw.util.GControllerHome;

import java.util.logging.Logger;

public abstract class HomeCLI extends BaseCLI implements GControllerHome {

    // Logger statico per questa classe
    private static final Logger LOGGER = Logger.getLogger(HomeCLI.class.getName());

    protected final HomePageController controller;
    private String userDataHeader;

    // FIX SONARQUBE: Il costruttore di una classe abstract deve essere protected
    protected HomeCLI() {
        super();
        this.controller = new HomePageController();
    }

    protected abstract String getFixedRole();

    @Override
    public void updateUserData(String name, String surname, String birthDate, String subjects) {
        this.userDataHeader = String.format("""
            ------------------------------------------
            Dati Utente:
            Nome: %s %s
            Ruolo: %s
            Nascita: %s
            ------------------------------------------
            """, name, surname, this.getFixedRole(), birthDate);
    }

    protected void printUserInfo() {
        controller.refreshUserData(this);
        // FIX SONARQUBE: Sostituito System.out con LOGGER
        LOGGER.info(userDataHeader);
    }

    protected void logout() {
        // FIX SONARQUBE: Sostituito System.out con LOGGER
        LOGGER.info("Logout in corso...");
        controller.logout();
    }
}