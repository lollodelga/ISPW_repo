package testing;

import ldg.progettoispw.controller.LoginCtrlApplicativo;
import ldg.progettoispw.engineering.exception.InvalidEmailException;
import ldg.progettoispw.engineering.exception.UserDoesNotExistException;
import org.junit.Assert;
import org.junit.Test;

public class TestLogin {

    /**
     * Verifica che venga sollevata l'eccezione corretta se il formato dell'email non è valido
     * (es. manca la chiocciola o il dominio).
     */
    @Test
    public void testLoginEmailFormatoNonValido() {
        LoginCtrlApplicativo controller = new LoginCtrlApplicativo();

        // Email senza chiocciola
        String emailErrata = "mario.rossigmail.com";

        Assert.assertThrows(
                InvalidEmailException.class,
                () -> controller.verificaCredenziali(emailErrata, "password123")
        );
    }

    /**
     * Verifica che venga sollevata l'eccezione corretta se i campi sono vuoti o nulli.
     */
    @Test
    public void testLoginCampiVuoti() {
        LoginCtrlApplicativo controller = new LoginCtrlApplicativo();

        Assert.assertThrows(
                InvalidEmailException.class,
                () -> controller.verificaCredenziali("", "")
        );
    }

    /**
     * Verifica che venga sollevata l'eccezione corretta se si prova a fare login
     * con un utente che non esiste nel database.
     * Nota: Questo test richiede che il DB sia raggiungibile.
     */
    @Test
    public void testLoginUtenteNonEsistente() {
        LoginCtrlApplicativo controller = new LoginCtrlApplicativo();

        // Usiamo un'email sicuramente non presente nel DB
        String emailInesistente = "utente.fantasma@inesistente.com";

        Assert.assertThrows(
                UserDoesNotExistException.class,
                () -> controller.verificaCredenziali(emailInesistente, "password123")
        );
    }
}