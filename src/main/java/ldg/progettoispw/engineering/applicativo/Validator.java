package ldg.progettoispw.engineering.applicativo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Validator {

    private Validator() {
        throw new UnsupportedOperationException("Classe di utilità, non istanziabile.");
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,7}$";
        return Pattern.matches(regex, email);
    }

    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[:!?$%&;])[A-Za-z\\d:!?$%&;]{8,16}$";
        return Pattern.matches(regex, password);
    }

    public static boolean isValidDate(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        try {
            Date date = format.parse(dateStr);
            return !date.after(new Date());
        } catch (ParseException _) {
            return false;
        }
    }
}