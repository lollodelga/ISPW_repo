package ldg.progettoispw.viewCLI;

import ldg.progettoispw.controller.HomePageController;
import ldg.progettoispw.util.GControllerHome;

public abstract class HomeCLI extends BaseCLI implements GControllerHome {

    protected final HomePageController controller;
    private String userDataHeader;

    public HomeCLI() {
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
        System.out.println(userDataHeader);
    }

    protected void logout() {
        System.out.println(">>> Logout in corso...");
        controller.logout();
    }
}