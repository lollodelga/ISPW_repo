package ldg.progettoispw.viewcli.studente;

import ldg.progettoispw.viewcli.HomeCLI;

public class HomeStudentCLI extends HomeCLI {

    @Override
    protected String getFixedRole() {
        return "Studente";
    }

    @Override
    protected String getDashboardTitle() {
        return "DASHBOARD STUDENTE";
    }

    @Override
    protected void setupMenu() {

        addMenuOption("1", "Cerca Tutor e Prenota",
                () -> new SearchTutorCLI().start());

        addMenuOption("2", "Richieste in sospeso",
                () -> new AppInAttesaStudentCLI().start());

        addMenuOption("3", "Richieste completate (e Recensioni)",
                () -> new AppRispostiStudentCLI().start());
    }
}