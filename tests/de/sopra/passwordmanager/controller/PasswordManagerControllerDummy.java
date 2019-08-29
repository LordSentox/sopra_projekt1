package de.sopra.passwordmanager.controller;

import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.CredentialsItem;
import de.sopra.passwordmanager.util.strategy.EntryListOrderStrategy;
import de.sopra.passwordmanager.util.strategy.EntryListSelectionStrategy;
import de.sopra.passwordmanager.view.LoginViewAUI;
import de.sopra.passwordmanager.view.MainWindowAUI;
import de.sopra.passwordmanager.view.MasterPasswordViewAUI;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 21.08.2019
 * @since 21.08.2019
 */
public class PasswordManagerControllerDummy {

    public static PasswordManagerController getNewController() {
        return create();
    }

    private static PasswordManagerController create() {
        PasswordManagerController controller = new PasswordManagerController();
        controller.setLoginViewAUI(createLogin());
        controller.setMainWindowAUI(createMainWindow(controller));
        controller.setMasterPasswordViewAUI(createMasterPass());
        controller.getPasswordManager().setMasterPassword("Hey123");
        return controller;
    }

    private static MasterPasswordViewAUI createMasterPass() {
        return new MasterPasswordView();
    }

    private static LoginViewAUI createLogin() {
        return new LoginView();
    }

    private static MainWindowAUI createMainWindow(PasswordManagerController controller) {
        return new MainView(controller);
    }

    public static class MainView implements MainWindowAUI {

        private List<Credentials> currentCredentialsList = null;
        private List<String> errorsShown = new ArrayList<>();
        private int passwordQuality = -1;
        private EntryListSelectionStrategy strategy = identity -> identity.stream().map(CredentialsItem::new).collect(Collectors.toList());
        private EntryListOrderStrategy orderStrategy = identity -> identity;
        private CredentialsBuilder currentCredentials = null;
        private Category currentSelectedCategory = null;
        private PasswordManagerController controller;

        public MainView(PasswordManagerController controller) {
            this.controller = controller;
        }

        @Override
        public void refreshLists() {
            List<Credentials> credentials = new ArrayList<>(controller.getPasswordManager().getRootCategory().getAllCredentials());
            List<CredentialsItem> selected = strategy.select(credentials);
            List<CredentialsItem> ordered = orderStrategy.order(selected);
            this.currentCredentialsList = ordered.stream().map(CredentialsItem::getCredentials).collect(Collectors.toList());
        }

        @Override
        public void refreshListStrategies(EntryListSelectionStrategy selection, EntryListOrderStrategy order) {
            if (selection != null) this.strategy = selection;
            if (order != null) this.orderStrategy = order;
            refreshLists();
        }

        @Override
        public void refreshEntry() {

        }

        @Override
        public void refreshEntryPasswordQuality(int quality) {
            this.passwordQuality = quality;
        }

        @Override
        public void showError(String error) {
            errorsShown.add(error);
        }

        /**
         * Gibt die aktuelle Passwortqualität zurück.
         *
         * @return die aktuelle Passwortqualität oder -1 falls die Qualität noch nicht geändert wurde
         */
        public int getPasswordQuality() {
            return passwordQuality;
        }

        /**
         * Gibt das aktuell angezeigte Passwort repräsentativ zurück
         *
         * @return das aktuell im Klartext angezeigte Passwort,
         * einen leeren String falls ein Passwort angezeigt wird, dass nicht im Klartext ist
         * oder null, falls noch kein gezeigt wird
         */
        public String getPasswordShown() {
            return currentCredentials.getPassword();
        }

        /**
         * Simulates the user to select a category on the left side of the view
         *
         * @param currentSelectedCategory the selected category
         */
        public void setCurrentSelectedCategory(Category currentSelectedCategory) {
            this.currentSelectedCategory = currentSelectedCategory;
        }

        /**
         * Gets the currently selected category
         *
         * @return the currently selected category or <code>null</code> if none is selected
         */
        public Category getCurrentSelectedCategory() {
            return currentSelectedCategory;
        }

        /**
         * Gibt die Liste aller erschienen Fehlermeldungen seit Erstellung des Dummys zurück
         *
         * @return eine Liste aller aufgerufenen Fehlermeldungen
         */
        public List<String> getErrorsShown() {
            return errorsShown;
        }

        /**
         * Die aktuell gezeigt Liste der Credentials
         *
         * @return die Liste der aktuell gezeigten Credentials oder null, falls keine Liste gesetzt wurde
         */
        public List<Credentials> getCurrentCredentialsList() {
            return currentCredentialsList;
        }

        /**
         * Die aktuellen Credentials als Builder
         *
         * @return Die aktuellen Credentials als Builder oder null, falls keines gesetzt wurde
         */
        public CredentialsBuilder getCurrentCredentials() {
            return currentCredentials;
        }
    }

    public static class LoginView implements LoginViewAUI {

        private Boolean lastReceivedResult = null;

        @Override
        public void handleLoginResult(boolean result) {

        }

        /**
         * Gibt das zuletzt per handleLoginResult erhaltene Ergebnis der simulierten GUI zurück.
         *
         * @return das zuletzt erhaltene Ergebnis oder null falls es noch keines gab
         */
        public Boolean getLastReceivedResult() {
            return lastReceivedResult;
        }
    }

    public static class MasterPasswordView implements MasterPasswordViewAUI {

        private int currentQuality = -1;

        @Override
        public void refreshQuality(int quality) {
            this.currentQuality = quality;
        }

        /**
         * Die aktuelle Qualität in der simulierten GUI.
         *
         * @return die aktuelle Qualität oder -1 falls noch nichts in der GUI geändert wurde
         */
        public int getCurrentQuality() {
            return currentQuality;
        }

    }

}
/***********************************************************************************************
 *
 *                  All rights reserved, SpaceParrots UG (c) copyright 2019
 *
 ***********************************************************************************************/