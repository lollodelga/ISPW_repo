package ldg.progettoispw.util;

import javafx.event.ActionEvent;

public interface GController {
    void changeView(int result, ActionEvent event);
}
/* Ogni controller graifco implementa tale classe, poiché contiene il comando, che permette
  all'applicativo di cambiare la view in base al risultato dell'operazione.*/