package ldg.progettoispw.engineering.api;

import java.io.File;
import java.io.IOException;

public class PythonServerLauncher {

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
            System.out.println("[INFO] Il monitor è già attivo.");
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

                } catch (InterruptedException e) {
                    handleMonitorInterrupted();
                    break;

                } catch (IOException e) {
                    System.err.println("[ERRORE Monitor]: " + e.getMessage());
                }
            }
        });
    }

    private static void ensureServerRunning() throws IOException {
        if (serverProcess == null || !serverProcess.isAlive()) {
            System.out.println("[SERVER] Uvicorn non attivo. Avvio in corso...");
            startPythonServer();
        }
    }

    private static void handleMonitorInterrupted() {
        Thread.currentThread().interrupt();
        System.out.println("[MONITOR] Thread interrotto, uscita dal monitor.");
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
        ProcessBuilder pb = new ProcessBuilder(
                "C:\\Users\\lollo\\AppData\\Local\\Programs\\Python\\Python314\\python.exe",
                "-m", "uvicorn",
                "sentiment:app",
                "--host", "127.0.0.1",
                "--port", "8000",
                "--log-level", "critical"
        );

        // Path assoluto della directory dove c'è sentiment.py
        pb.directory(new File("C:\\Users\\lollo\\Desktop\\Università\\Triennale\\ISPW\\progettoISPW\\src"));
        pb.redirectErrorStream(true);

        serverProcess = pb.start();
        System.out.println("[SERVER] Uvicorn avviato.");
    }
}
