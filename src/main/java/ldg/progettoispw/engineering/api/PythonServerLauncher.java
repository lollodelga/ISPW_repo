package ldg.progettoispw.engineering.api;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PythonServerLauncher {

    // 1. Dichiarazione del Logger
    private static final Logger LOGGER = Logger.getLogger(PythonServerLauncher.class.getName());

    private PythonServerLauncher() {
        throw new UnsupportedOperationException("Utility class - non deve essere istanziata");
    }

    private static Process serverProcess;
    private static ScheduledExecutorService scheduler;

    public static synchronized void launch() {
        // Se lo scheduler esiste già ed è attivo, non facciamo nulla
        if (scheduler != null && !scheduler.isShutdown()) {
            LOGGER.info("Il monitor è già attivo.");
            return;
        }

        startMonitorService();
        registerShutdownHook();
    }

    private static void startMonitorService() {
        // Creiamo uno scheduler con un thread DAEMON (importante per chiudere l'app)
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Python-Monitor-Thread");
            t.setDaemon(true); // Fondamentale: così si chiude quando chiudi JavaFX
            return t;
        });

        // "Esegui ensureServerRunning ogni 3 secondi"
        // initialDelay: 0 (parte subito)
        // period: 3 (ogni 3 secondi)
        // unit: SECONDS
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                ensureServerRunning();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Errore nel monitor del server Python", e);
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    private static void ensureServerRunning() throws IOException {
        if (serverProcess == null || !serverProcess.isAlive()) {
            LOGGER.info("Uvicorn non attivo. Avvio in corso...");
            startPythonServer();
        }
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Spegniamo lo scheduler in modo pulito
            if (scheduler != null) {
                scheduler.shutdownNow();
            }
            // Uccidiamo il processo Python
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