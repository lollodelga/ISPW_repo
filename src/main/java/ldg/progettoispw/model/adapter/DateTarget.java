package ldg.progettoispw.model.adapter;

import ldg.progettoispw.exception.DBException;
import java.sql.Date;

public interface DateTarget {
    Date convert(String dateString) throws DBException;
}
