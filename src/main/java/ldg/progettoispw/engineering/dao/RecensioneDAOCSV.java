package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.Recensione;
import ldg.progettoispw.util.RecensioneDAO; // Import corretto

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecensioneDAOCSV implements RecensioneDAO {

    private static final String CSV_FILE = "recensioni.csv";
    private static final String SEP = ";";
    private final File file;

    public RecensioneDAOCSV() {
        this.file = new File(CSV_FILE);
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            System.err.println("Errore init CSV: " + e.getMessage());
        }
    }

    @Override
    public void insertRecensione(RecensioneBean bean) throws DBException {
        String cleanText = bean.getRecensione().replace(";", ",").replace("\n", " ");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            // tutor;student;text;sentiment
            String line = String.format("%s%s%s%s%s%s%d",
                    bean.getTutorEmail(), SEP, bean.getStudentEmail(), SEP,
                    cleanText, SEP, bean.getSentimentValue());
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            throw new DBException("Errore scrittura CSV", e);
        }
    }

    @Override
    public List<Recensione> getRecensioniByTutor(String tutorEmail) throws DBException {
        List<Recensione> list = new ArrayList<>();
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(SEP);
                if (p.length >= 4 && p[0].equals(tutorEmail)) {
                    Recensione r = new Recensione();
                    r.setId(0);
                    r.setEmailStudente(p[1]);
                    r.setTesto(p[2]);
                    try { r.setSentimentScore(Integer.parseInt(p[3])); } catch(Exception e){ r.setSentimentScore(0);}
                    list.add(r);
                }
            }
        } catch (IOException e) {
            throw new DBException("Errore lettura CSV", e);
        }
        return list;
    }

    @Override
    public boolean isEmpty() {
        return !file.exists() || file.length() == 0;
    }

    @Override
    public List<RecensioneBean> getAllRecensioni() throws DBException {
        List<RecensioneBean> list = new ArrayList<>();
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(SEP);
                if (p.length >= 4) {
                    RecensioneBean b = new RecensioneBean();
                    b.setTutorEmail(p[0]);
                    b.setStudentEmail(p[1]);
                    b.setRecensione(p[2]);
                    try { b.setSentimentValue(Integer.parseInt(p[3])); } catch(Exception e){ b.setSentimentValue(0);}
                    list.add(b);
                }
            }
        } catch (IOException e) {
            throw new DBException("Errore migration CSV", e);
        }
        return list;
    }

    @Override
    public void deleteAll() throws DBException {
        if (file.exists()) {
            try (PrintWriter pw = new PrintWriter(file)) {
                pw.print(""); // Sovrascrive con vuoto
            } catch (IOException e) {
                throw new DBException("Errore svuotamento CSV", e);
            }
        }
    }
}