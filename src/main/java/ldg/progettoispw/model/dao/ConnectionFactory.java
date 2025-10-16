package ldg.progettoispw.model.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class ConnectionFactory {
    private static final Logger logger = Logger.getLogger(ConnectionFactory.class.getName());
    private boolean connectedOnce = false;
    private static final String PATH = "src/main/resources/db.properties";
    private static ConnectionFactory instance = null;
    private Connection conn = null;

    private String jdbc;
    private String user;
    private String password;

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
                    getInfo(); // carica parametri db

                    conn = DriverManager.getConnection(jdbc, user, password);

                    if (!connectedOnce) {
                        logger.info("Connessione al database stabilita.");
                        connectedOnce = true;
                    }
                }

                return conn; // torna sempre la stessa connessione

            } catch (SQLException e) {
                logger.warning("Tentativo " + tentativo + " fallito: " + e.getMessage());

                if (tentativo < MAX_TENTATIVI) {
                    try {
                        // Uso di wait al posto di Thread.sleep
                        synchronized (this) {
                            this.wait(ATTESA_MS);
                        }
                    } catch (InterruptedException ex) {
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


    /**
     * Legge i parametri di connessione dal file db.properties.
     */
    private void getInfo() {
        try (FileInputStream fis = new FileInputStream(PATH)) {
            Properties prop = new Properties();
            prop.load(fis);

            jdbc = prop.getProperty("db.url");
            user = prop.getProperty("db.user");
            password = prop.getProperty("db.password");

            if (jdbc == null || user == null || password == null) {
                logger.warning("Parametri di connessione mancanti nel file db.properties");
            }

        } catch (IOException e) {
            logger.warning("Errore nell'accesso al file db.properties: " + e.getMessage());
        }
    }

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
