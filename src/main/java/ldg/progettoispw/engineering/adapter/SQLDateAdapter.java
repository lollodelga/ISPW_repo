package ldg.progettoispw.engineering.adapter;

import ldg.progettoispw.engineering.exception.DBException;
import java.sql.Date;
import java.text.ParseException;

public class SQLDateAdapter implements DateTarget {
    private final UtilDateParser adaptee = new UtilDateParser();

    @Override
    public Date convert(String dateString) throws DBException {
        try {
            java.util.Date utilDate = adaptee.parse(dateString);
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            throw new DBException("Formato data invalido. Usa yyyy-MM-dd: " + dateString, e);
        }
    }
}
