package de.sopra.passwordmanager.view;

/**
 * @author Etienne
 * Bietet eine Methode zum aktualisieren des Masterpasswort Fensters
 */
public interface MasterPasswordViewAUI {

    /**
     * Aktualisiert die Elemente, die zum Anzeigen der Qualität des Masterpassworts erforderlich sind
     *
     * @param quality Die Qualität des Passworts
     */
    void refreshQuality(int quality);

}
