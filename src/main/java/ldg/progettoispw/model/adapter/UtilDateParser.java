package ldg.progettoispw.model.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class UtilDateParser {

    public java.util.Date parse(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf.parse(dateString);
    }
}
