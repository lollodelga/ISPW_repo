package ldg.progettoispw.engineering.applicativo;

import ldg.progettoispw.engineering.exception.DBException;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Utility class per la conversione di stringhe in java.sql.Date.
 * Accetta date nel formato "yyyy-MM-dd" e le converte in un oggetto
 * SQL Date compatibile con il database.
 */
public class DateUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private DateUtils() {
        // Costruttore privato per evitare istanziazioni
    }

    /**
     * Converte una stringa nel formato "yyyy-MM-dd" in un java.sql.Date.
     *
     * @param dateString la data in formato testo
     * @return la corrispondente java.sql.Date
     * @throws DBException se la stringa non rispetta il formato richiesto
     */
    public static Date toSqlDate(String dateString) throws DBException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false); // impedisce formati ambigui o errati
            java.util.Date utilDate = sdf.parse(dateString);
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            throw new DBException("Formato data invalido. Usa yyyy-MM-dd: " + dateString, e);
        }
    }
}
