package ldg.progettoispw.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class ConnectionFactory {
    private static final Logger logger = Logger.getLogger(ConnectionFactory.class.getName());

    // Singleton dell'istanza della classe
    private static ConnectionFactory instance = null;

    // Connessione singola per tutta la durata dell'app (per utente)
    private Connection conn = null;

    private ConnectionFactory() {
        // costruttore privato
    }

    public static synchronized ConnectionFactory getInstance() {
        if (instance == null) {
            instance = new ConnectionFactory();
        }
        return instance;
    }

    public synchronized Connection getDBConnection() {
        final int MAX_TENTATIVI = 3;
        final int ATTESA_MS = 2000;

        for (int tentativo = 1; tentativo <= MAX_TENTATIVI; tentativo++) {
            try {
                if (conn == null || conn.isClosed()) {
                    Properties properties = new Properties();
                    properties.put("user", "root");
                    properties.put("password", "Forzalazio1900");

                    conn = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/progetto_ispw",
                            properties
                    );

                    logger.info("Connessione al database stabilita.");
                }

                return conn;

            } catch (SQLException e) {
                logger.warning("Tentativo " + tentativo + " fallito: " + e.getMessage());

                if (tentativo < MAX_TENTATIVI) {
                    try {
                        Thread.sleep(ATTESA_MS); // NOSONAR - retry mechanism, non serve wait()
                    } catch (InterruptedException _) {
                        Thread.currentThread().interrupt();
                        logger.severe("Tentativo interrotto.");
                        break;
                    }
                } else {
                    logger.severe("Connessione al database fallita dopo " + MAX_TENTATIVI + " tentativi.");
                }
            }
        }

        return null;
    }

    // Metodo per chiudere manualmente la connessione, es. alla chiusura dell'app
    public synchronized void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                logger.info("Connessione al database chiusa.");
            }
        } catch (SQLException e) {
            logger.warning("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }
}