package ldg.progettoispw.viewcli;

import ldg.progettoispw.controller.HomePageController;
import ldg.progettoispw.util.GControllerHome;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class HomeCLI extends BaseCLI implements GControllerHome {

    protected final HomePageController controller;
    private String userDataHeader;

    // LinkedHashMap mantiene l'ordine di inserimento
    private final Map<String, MenuEntry> menuOptions = new LinkedHashMap<>();

    protected HomeCLI() {
        super();
        this.controller = new HomePageController();
        setupMenu();
    }

    protected abstract String getFixedRole();
    protected abstract String getDashboardTitle();
    protected abstract void setupMenu();

    protected void addMenuOption(String key, String description, Runnable action) {
        menuOptions.put(key, new MenuEntry(description, action));
    }

    @Override
    public void start() {
        boolean running = true;

        while (running) {
            printHeader(getDashboardTitle());
            printUserInfo();

            // Stampa le opzioni dalla Mappa usando Printer
            for (Map.Entry<String, MenuEntry> entry : menuOptions.entrySet()) {
                Printer.println(entry.getKey() + ". " + entry.getValue().description);
            }
            Printer.println("0. Logout");

            String choice = readInput("Scegli un'opzione");

            if (choice.equals("0")) {
                logout();
                running = false;
            } else if (menuOptions.containsKey(choice)) {
                // Esegue l'azione associata
                menuOptions.get(choice).action.run();
            } else {
                showError("Opzione non valida.");
            }
        }
    }

    @Override
    public void updateUserData(String name, String surname, String birthDate, String subjects) {
        // Formattazione dati utente
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
        // Stampa i dati aggiornati
        Printer.println(userDataHeader);
    }

    protected void logout() {
        Printer.println("Logout in corso...");
        controller.logout();
    }

    private static class MenuEntry {
        String description;
        Runnable action;

        public MenuEntry(String description, Runnable action) {
            this.description = description;
            this.action = action;
        }
    }
}