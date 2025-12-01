package ldg.progettoispw.engineering.api;

import java.io.File;
import java.io.IOException;

public class PythonServerLauncher {

    private static Process serverProcess;
    private static volatile boolean monitorActive = false;

    /**
     * Avvia il monitor che assicura che il server Python sia sempre attivo.
     */
    public static synchronized void launch() {
        if (monitorActive) {
            System.out.println("[INFO] Il monitor è già attivo.");
            return;
        }

        monitorActive = true;

        // Thread che controlla continuamente lo stato del server
        Thread monitorThread = new Thread(() -> {
            while (monitorActive) {
                try {
                    // Se il processo non esiste o è morto → riavvia
                    if (serverProcess == null || !serverProcess.isAlive()) {
                        System.out.println("[SERVER] Uvicorn non attivo. Avvio in corso...");
                        startPythonServer();
                    }

                    Thread.sleep(3000); // 3 secondi tra un controllo e l’altro

                } catch (Exception e) {
                    System.err.println("[ERRORE Monitor]: " + e.getMessage());
                }
            }
        });

        monitorThread.setDaemon(true);
        monitorThread.start();

        // Hook quando Java termina
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            monitorActive = false;
            if (serverProcess != null && serverProcess.isAlive()) {
                serverProcess.destroy();
            }
        }));
    }

    /**
     * Avvia il server uvicorn
     */
    private static void startPythonServer() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "python",
                "-m", "uvicorn",
                "sentiment:app",
                "--host", "127.0.0.1",
                "--port", "8000",
                "--log-level", "critical"
        );

        // 👉 Path assoluto della directory dove c'è sentiment.py
        pb.directory(new File("C:\\Users\\lollo\\Desktop\\Università\\Triennale\\ISPW\\progettoISPW\\src\\main"));

        pb.redirectErrorStream(true);
        serverProcess = pb.start();

        System.out.println("[SERVER] Uvicorn avviato.");
    }

}
