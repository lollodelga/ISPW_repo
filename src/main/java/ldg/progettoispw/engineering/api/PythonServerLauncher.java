package ldg.progettoispw.engineering.api;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PythonServerLauncher {

    // 1. Dichiarazione del Logger
    private static final Logger LOGGER = Logger.getLogger(PythonServerLauncher.class.getName());

    private PythonServerLauncher() {
        throw new UnsupportedOperationException("Utility class - non deve essere istanziata");
    }

    private static Process serverProcess;
    private static volatile boolean monitorActive = false;

    /** Avvia il monitor che assicura che il server Python sia sempre attivo. */
    public static synchronized void launch() {
        if (isMonitorAlreadyActive()) {
            return;
        }

        monitorActive = true;
        startMonitorThread();
        registerShutdownHook();
    }

    private static boolean isMonitorAlreadyActive() {
        if (monitorActive) {
            // Riga 28: Sostituito System.out con LOGGER.info
            LOGGER.info("Il monitor è già attivo.");
            return true;
        }
        return false;
    }

    private static void startMonitorThread() {
        Thread.ofVirtual().start(() -> {
            while (monitorActive) {
                try {
                    ensureServerRunning();
                    Thread.sleep(3000);

                } catch (InterruptedException _) {
                    handleMonitorInterrupted();
                    break;

                } catch (IOException e) {
                    // Riga 46: Sostituito System.err con LOGGER.log(SEVERE)
                    // Passiamo l'eccezione 'e' così stampa anche lo stack trace se serve
                    LOGGER.log(Level.SEVERE, "Errore nel monitor del server Python", e);
                }
            }
        });
    }

    private static void ensureServerRunning() throws IOException {
        if (serverProcess == null || !serverProcess.isAlive()) {
            // Riga 54: Sostituito System.out con LOGGER.info
            LOGGER.info("Uvicorn non attivo. Avvio in corso...");
            startPythonServer();
        }
    }

    private static void handleMonitorInterrupted() {
        Thread.currentThread().interrupt();
        // Riga 61: Sostituito System.out con LOGGER.info
        LOGGER.info("Thread interrotto, uscita dal monitor.");
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            monitorActive = false;
            if (serverProcess != null && serverProcess.isAlive()) {
                serverProcess.destroy();
            }
        }));
    }

    /** Avvia il server uvicorn */
    private static void startPythonServer() throws IOException {

        String pythonExec = "python";
        String scriptPath = System.getProperty("user.dir") + File.separator + "src";

        java.util.Properties prop = new java.util.Properties();
        try (java.io.InputStream input = new java.io.FileInputStream("src/main/resources/config.properties")) {
            prop.load(input);

            String loadedExec = prop.getProperty("python.exec");
            if (loadedExec != null && !loadedExec.isEmpty()) {
                pythonExec = loadedExec;
            }

            String loadedDir = prop.getProperty("python.dir");
            if (loadedDir != null && !loadedDir.isEmpty()) {
                scriptPath = loadedDir;
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Impossibile leggere config.properties, uso configurazioni di default.", e);
        }

        ProcessBuilder pb = new ProcessBuilder(
                pythonExec,
                "-m", "uvicorn",
                "sentiment:app",
                "--host", "127.0.0.1",
                "--port", "8000",
                "--log-level", "critical"
        );

        File workingDir = new File(scriptPath);

        if (!workingDir.exists()) {
            LOGGER.log(Level.WARNING, "Attenzione: La cartella configurata non esiste: {0}", scriptPath);
        }

        pb.directory(workingDir);
        pb.redirectErrorStream(true);

        serverProcess = pb.start();
        LOGGER.info("Uvicorn avviato correttamente.");
    }
}