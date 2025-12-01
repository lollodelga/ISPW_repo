package ldg.progettoispw.engineering.applicativo;

import java.util.regex.Pattern;

public class ReviewFilter {

    // Lista di parolacce (puoi ampliarla)
    private static final String BAD_WORDS_REGEX =
            "(?i).*\\b(cazzo|merda|stronzo|troia|puttana|vaffanculo|deficiente|idiota)\\b.*";

    private static final Pattern BAD_WORDS_PATTERN = Pattern.compile(BAD_WORDS_REGEX);

    /**
     * Controlla se la recensione contiene parolacce.
     * @param text testo della recensione
     * @return true se contiene parolacce, false se è pulita
     */
    public boolean containsBadWords(String text) {
        if (text == null || text.isBlank()) return false;

        // Matcher con find() invece di matches() per catturare la parola ovunque
        return BAD_WORDS_PATTERN.matcher(text).find();
    }
}
