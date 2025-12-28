package ldg.progettoispw.viewcli.tutor;

import ldg.progettoispw.viewcli.HomeCLI;

public class HomeTutorCLI extends HomeCLI {

    @Override
    protected String getFixedRole() {
        return "Tutor";
    }

    @Override
    protected String getDashboardTitle() {
        return "DASHBOARD TUTOR";
    }

    @Override
    protected void setupMenu() {
        // Configuriamo il menu del tutor
        addMenuOption("1", "Recensioni ricevute (e Statistiche)",
                () -> new ManageReviewsCLI().start());

        addMenuOption("2", "Gestione Richieste in attesa",
                () -> new AppPendTutorCLI().start());

        addMenuOption("3", "Storico Appuntamenti (Concludi/Annulla)",
                () -> new AppRispostiTutorCLI().start());
    }
}