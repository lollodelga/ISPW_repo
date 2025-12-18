package ldg.progettoispw;

import ldg.progettoispw.viewCLI.FirstPageCLI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("   SELEZIONA MODALITÀ DI AVVIO   ");
        System.out.println("=================================");
        System.out.println("1. Interfaccia Grafica (JavaFX)");
        System.out.println("2. Riga di Comando (CLI)");
        System.out.print("Inserisci scelta (1 o 2): ");

        Scanner scanner = new Scanner(System.in);
        String scelta = scanner.nextLine();

        if (scelta.equals("1")) {
            System.out.println(">>> Avvio Interfaccia Grafica...");
            MainFXML project = new MainFXML();
            project.run();
        }
        else if (scelta.equals("2")) {
            System.out.println(">>> Avvio Riga di Comando...");

            FirstPageCLI homeCLI = new FirstPageCLI();
            homeCLI.start();
        }
        else {
            System.out.println("❌ Scelta non valida. Riavvia il programma.");
        }
    }
}