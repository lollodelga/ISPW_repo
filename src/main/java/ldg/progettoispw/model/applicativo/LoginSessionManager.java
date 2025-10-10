package ldg.progettoispw.model.applicativo;

import ldg.progettoispw.model.bean.UserBean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class LoginSessionManager {
    private static final String SESSION_FILE = "session.csv";
    private static final Logger logger = Logger.getLogger(LoginSessionManager.class.getName());

    private LoginSessionManager() {
        throw new UnsupportedOperationException("Classe di utilità, non istanziabile.");
    }

    // Salva i dati dell'utente loggato nel file
    public static void saveUserSession(UserBean userBean) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(SESSION_FILE))) {
            String[] data = userBean.getArray();
            // Importante: Separo ogni campo con una virgola, ma dentro il campo 'materie' sostituisco eventuali virgole
            data[5] = data[5].replace(",", ";"); // Proteggo il campo materie
            writer.write(String.join(",", data));
        } catch (IOException e) {
            logger.severe("Errore durante il salvataggio della sessione: " + e.getMessage());
        }
    }

    // Legge i dati dell'utente loggato dal file
    public static UserBean loadUserSession() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(SESSION_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                String[] data = line.split(",", 6); // divido massimo in 6 parti
                // Rimetto eventuali ';' dentro le materie
                if (data.length == 6) {
                    data[5] = data[5].replace(";", ",");
                }
                UserBean userBean = new UserBean();
                userBean.setOfAll(data); // popola tutto il bean
                return userBean;
            }
        } catch (IOException e) {
            logger.warning("Errore durante il caricamento della sessione: " + e.getMessage());
        }
        return null;
    }

    // Cancella il file della sessione
    public static void clearSession() {
        try {
            Files.deleteIfExists(Paths.get(SESSION_FILE));
            logger.info("Sessione cancellata.");
        } catch (IOException e) {
            logger.warning("Errore durante la cancellazione della sessione: " + e.getMessage());
        }
    }
}
