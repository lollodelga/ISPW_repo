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

    private final Object lock = new Object();
    private static final int attesaMs = 2000;

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

        for (int tentativo = 1; tentativo <= MAX_TENTATIVI; tentativo++) {
            try {
                return tryConnect();
            } catch (SQLException e) {
                handleConnectionFailure(tentativo, MAX_TENTATIVI, e);
            }
        }

        return null;
    }

    private Connection tryConnect() throws SQLException {
        if (conn == null || conn.isClosed()) {
            getInfo(); // carica parametri db
            conn = DriverManager.getConnection(jdbc, user, password);

            if (!connectedOnce) {
                logger.info("Connessione al database stabilita.");
                connectedOnce = true;
            }
        }
        return conn;
    }

    private void handleConnectionFailure(int tentativo, int maxTentativi, SQLException e) {
        logger.warning(String.format("Tentativo %d fallito: %s", tentativo, e.getMessage()));

        if (tentativo < maxTentativi) {
            waitBeforeRetry();
        } else {
            logger.severe(String.format("Connessione al database fallita dopo %d tentativi.", maxTentativi));
        }
    }

    private void waitBeforeRetry() {
        try {
            synchronized (lock) {
                long start = System.currentTimeMillis();
                long remaining = attesaMs;
                while (remaining > 0) {
                    lock.wait(remaining);
                    remaining = attesaMs - (System.currentTimeMillis() - start);
                }
            }
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            logger.severe("Tentativo interrotto.");
        }
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
