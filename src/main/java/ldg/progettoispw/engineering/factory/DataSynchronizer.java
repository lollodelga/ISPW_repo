package ldg.progettoispw.engineering.factory;

import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.util.RecensioneDAO;
import ldg.progettoispw.viewcli.Printer;

import java.util.List;

public class DataSynchronizer {

    public void syncData() {
        var currentType = PersistenceConfig.getInstance().getType();
        if (currentType == PersistenceConfig.PersistenceType.DEMO) return;

        RecensioneDAO source;
        RecensioneDAO dest;

        // CORREZIONE: Dichiarazione su righe separate
        String sourceName;
        String destName;

        if (currentType == PersistenceConfig.PersistenceType.CSV) {
            // Target: CSV -> Prendo da JDBC
            dest = DAOFactory.getRecensioneDAOCSV();
            source = DAOFactory.getRecensioneDAOJDBC();
            sourceName = "DATABASE";
            destName = "CSV";
        } else {
            // Target: JDBC -> Prendo da CSV
            dest = DAOFactory.getRecensioneDAOJDBC();
            source = DAOFactory.getRecensioneDAOCSV();
            sourceName = "CSV";
            destName = "DATABASE";
        }

        // --- Logica Spostamento ---
        // Se la sorgente ha dati E la destinazione è vuota -> Sposta
        if (!source.isEmpty()) {
            if (dest.isEmpty()) {
                Printer.printlnBlu(">> Spostamento dati da " + sourceName + " a " + destName + "...");
                try {
                    // 1. Copia
                    List<RecensioneBean> data = source.getAllRecensioni();
                    int count = 0;
                    for (RecensioneBean b : data) {
                        dest.insertRecensione(b);
                        count++;
                    }
                    // 2. Cancella Sorgente
                    source.deleteAll();

                    Printer.printlnBlu(">> Completato! Spostati " + count + " record.");
                    Printer.println(">> Archivio " + sourceName + " svuotato.");

                } catch (DBException e) {
                    Printer.errorPrint("ERRORE CRITICO SPOSTAMENTO: " + e.getMessage());
                }
            } else {
                Printer.println(">> ATTENZIONE: Dati presenti in entrambi gli archivi (" + sourceName + ", " + destName + ").");
                Printer.println(">> Nessun merge automatico eseguito per sicurezza.");
            }
        }
    }
}