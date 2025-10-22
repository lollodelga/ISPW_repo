package ldg.progettoispw.engineering.adapter;

import ldg.progettoispw.engineering.exception.DBException;
import java.sql.Date;

public interface DateTarget {
    Date convert(String dateString) throws DBException;
}
