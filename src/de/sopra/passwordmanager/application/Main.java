package de.sopra.passwordmanager.application;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.util.LanguageProvider;
import de.sopra.passwordmanager.view.LoginViewController;
import de.sopra.passwordmanager.view.MainWindowAUI;
import de.sopra.passwordmanager.view.MainWindowViewController;
import de.sopra.passwordmanager.view.MasterPasswordViewController;
import de.sopra.passwordmanager.view.dialog.SimpleConfirmation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;

import static de.sopra.passwordmanager.controller.PasswordManagerController.SAVE_FILE;

public class Main extends Application {

    private static LanguageProvider langProvider = new LanguageProvider();
    //private static final String LANGUAGE = "en_EN";
    private static final String LANGUAGE = "ja_JA";

    @Override
    public void start(Stage primaryStage) {

        loadLanguage();

        MainWindowAUI aui = null;
        try {
            /* Hauptfenster */
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Hauptfenster.fxml"));
            AnchorPane mainPane = fxmlLoader.load();
            MainWindowViewController mainWindowViewController = fxmlLoader.getController();
            PasswordManagerController passwordManagerController = new PasswordManagerController();
            passwordManagerController.setMainWindowAUI(mainWindowViewController);
            aui = mainWindowViewController;
            mainWindowViewController.setPasswordManagerController(passwordManagerController);

            Stage mainStage = new Stage();
            Scene mainScene = new Scene(mainPane);
            mainScene.getStylesheets().add(getClass().getResource("/stylesheets/application.css").toExternalForm());
            mainStage.setScene(mainScene);
            mainWindowViewController.setMainWindowViewController(mainWindowViewController);
            mainWindowViewController.setLanguageProvider(langProvider);
            mainWindowViewController.setStage(mainStage);
            mainWindowViewController.setScene(mainScene);
            mainStage.setResizable(false);
            mainStage.initStyle(StageStyle.UNDECORATED);

            mainWindowViewController.init();

            //Für clearSelection, das irgendwie komische Fehler wirft
            Thread.currentThread().setUncaughtExceptionHandler((t, e) ->
                    System.out.println("Ooops, da war ja ein Fehler-chan unterwegs... Gaaar nicht bemeerkt..."));

            if (SAVE_FILE.exists()) {
            

            	/* Loginfenster */
                LoginViewController login = mainWindowViewController.openModal("/Einloggen.fxml",
                        LoginViewController.class, preOpen ->
                        {
                            preOpen.setSourceFile(SAVE_FILE);
                            preOpen.setBackTo(mainStage);
                        });

                //set AUI link
                passwordManagerController.setLoginViewAUI(login);


            } else {
                /* Masterpasswort zum Erststart / Registrierung setzen */
                MasterPasswordViewController masterPasswordViewController =
                        mainWindowViewController.openModal("/Masterpasswort-setzen.fxml",
                                MasterPasswordViewController.class, preOpen ->
                                {
                                    preOpen.setBackTo(mainStage);
                                    preOpen.init();
                                });

                //set AUI link
                passwordManagerController.setMasterPasswordViewAUI(masterPasswordViewController);

                //Beispieleintrag
                LocalDateTime created = LocalDateTime.now();
                mainWindowViewController.getCredentialsBuilder().withName("Beispieleintrag").withPassword("Beispielpasswort").withUserName("Maxine Musterfrau").withChangeReminderDays(5).withCreated(created).withLastChanged(created).withNotes("Hier könnten Ihre Notizen stehen").withSecurityQuestion("Name des Haustieres", "Godzilla").withWebsite("www.yolo.com/yolo");
                mainWindowViewController.refreshEntry();

            }
        } catch (Exception e) {
            if (aui != null) {
                aui.showError(e.toString() + " while creating the main view - fatal error");
            }
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void loadLanguage() {
        Properties properties = new Properties();
        try {
            properties.load(Main.class.getResourceAsStream("/lang/" + LANGUAGE + ".properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        langProvider.setBaseFile(properties);
    }
}
