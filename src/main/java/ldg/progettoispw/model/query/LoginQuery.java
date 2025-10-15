package ldg.progettoispw.model.query;

public class LoginQuery {
    private LoginQuery() {
        throw new IllegalStateException("Utility class");
    }
    public static final String CHECK_USER_EXISTS = "SELECT COUNT(*) FROM user WHERE email = ?";
    public static final String CHECK_PASSWORD = "SELECT COUNT(*) FROM user WHERE email = ? AND password = ?";
    public static final String GET_USER_ROLE = "SELECT ruolo FROM user WHERE email = ? AND password = ?";
}