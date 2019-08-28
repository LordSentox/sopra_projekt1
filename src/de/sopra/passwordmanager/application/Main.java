package de.sopra.passwordmanager.application;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.view.LoginViewController;
import de.sopra.passwordmanager.view.MainWindowAUI;
import de.sopra.passwordmanager.view.MainWindowViewController;
import de.sopra.passwordmanager.view.MasterPasswordViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainWindowAUI aui = null;
        try {
            /* Hauptfenster */
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Hauptfenster.fxml"));
            AnchorPane mainPane = fxmlLoader.load();
            MainWindowViewController mainWindowViewController = fxmlLoader.getController();
            PasswordManagerController passwordManagerController = new PasswordManagerController();
            passwordManagerController.setMainWindowAUI(mainWindowViewController);
            aui = mainWindowViewController;
            mainWindowViewController.setPasswordManagerController(passwordManagerController);
            mainWindowViewController.init();

            Stage mainStage = new Stage();
            Scene mainScene = new Scene(mainPane);
            mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            mainStage.setScene(mainScene);
            mainWindowViewController.setMainWindowViewController(mainWindowViewController);
            mainWindowViewController.setStage(mainStage);

            if (PasswordManagerController.SAVE_FILE.exists()) {
                /* Loginfenster */
                fxmlLoader = new FXMLLoader(getClass().getResource("../view/Einloggen.fxml"));
                AnchorPane loginPane = fxmlLoader.load();
                LoginViewController loginViewController = fxmlLoader.getController();
                loginViewController.setSourceFile(PasswordManagerController.SAVE_FILE);
                loginViewController.setMainWindowViewController(mainWindowViewController);
                loginViewController.setStage(primaryStage);
                
                Scene loginScene = new Scene(loginPane);
                loginScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
                primaryStage.initStyle(StageStyle.UNDECORATED);
                primaryStage.setScene(loginScene);
                primaryStage.show();

                //set AUI link
                passwordManagerController.setLoginViewAUI(loginViewController);

            } else {
                /* Masterpasswort zum Erststart / Registrierung setzen */
                AnchorPane masterPasswordPane = new AnchorPane();
                fxmlLoader = new FXMLLoader(getClass().getResource("../view/Masterpasswort-setzen.fxml"));
                masterPasswordPane = fxmlLoader.load();
                MasterPasswordViewController masterPasswordViewController = fxmlLoader.getController();
                masterPasswordViewController.setMainWindowViewController(mainWindowViewController);
                masterPasswordViewController.setStage(primaryStage);
                masterPasswordViewController.setMainStage(mainStage);
                masterPasswordViewController.init();

                Scene masterPasswordScene = new Scene(masterPasswordPane);
                masterPasswordScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
                primaryStage.setScene(masterPasswordScene);
                primaryStage.show();

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

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
