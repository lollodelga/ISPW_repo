package ldg.progettoispw.viewcli;

import ldg.progettoispw.controller.HomePageController;
import ldg.progettoispw.util.GControllerHome;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class HomeCLI extends BaseCLI implements GControllerHome {

    private static final Logger LOGGER = Logger.getLogger(HomeCLI.class.getName());

    protected final HomePageController controller;
    private String userDataHeader;

    // Usiamo LinkedHashMap per mantenere l'ordine di inserimento (1, 2, 3...)
    private final Map<String, MenuEntry> menuOptions = new LinkedHashMap<>();

    protected HomeCLI() {
        super();
        this.controller = new HomePageController();
        // Chiamiamo il metodo astratto che i figli useranno per riempire la mappa
        setupMenu();
    }

    // --- Metodi Astratti Nuovi ---
    protected abstract String getFixedRole();
    protected abstract String getDashboardTitle();
    protected abstract void setupMenu(); // I figli riempiranno il menu qui

    // --- Helper per aggiungere opzioni ---
    protected void addMenuOption(String key, String description, Runnable action) {
        menuOptions.put(key, new MenuEntry(description, action));
    }

    @Override
    public void start() {
        boolean running = true;

        while (running) {
            printHeader(getDashboardTitle());
            printUserInfo();

            // 1. Stampa le opzioni dalla Mappa
            for (Map.Entry<String, MenuEntry> entry : menuOptions.entrySet()) {
                LOGGER.info(entry.getKey() + ". " + entry.getValue().description);
            }
            LOGGER.info("0. Logout");

            String choice = readInput("Scegli un'opzione");

            if (choice.equals("0")) {
                logout();
                running = false;
            } else if (menuOptions.containsKey(choice)) {
                // 2. Esegue l'azione associata (Command Pattern)
                menuOptions.get(choice).action.run();
            } else {
                showError("Opzione non valida.");
            }
        }
    }

    // ... (updateUserData, printUserInfo, logout restano uguali a prima) ...
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
        LOGGER.info(userDataHeader);
    }

    protected void logout() {
        LOGGER.info("Logout in corso...");
        controller.logout();
    }

    // Piccola classe interna per tenere insieme descrizione e azione
    private static class MenuEntry {
        String description;
        Runnable action;

        public MenuEntry(String description, Runnable action) {
            this.description = description;
            this.action = action;
        }
    }
}