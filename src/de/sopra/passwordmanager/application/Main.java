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

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainWindowAUI aui = null;
        try {
            /* Hauptfenster */
            AnchorPane mainPane = new AnchorPane();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Hauptfenster.fxml"));
            mainPane = fxmlLoader.load();
            MainWindowViewController mainWindowViewController = (MainWindowViewController) fxmlLoader.getController();
            PasswordManagerController passwordManagerController = new PasswordManagerController();
            passwordManagerController.setMainWindowAUI(mainWindowViewController);
            aui = mainWindowViewController;
            mainWindowViewController.setPasswordManagerController(passwordManagerController);
            mainWindowViewController.init();

            Stage mainStage = new Stage();
            Scene mainScene = new Scene(mainPane);
            mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            mainStage.setScene(mainScene);

            if (PasswordManagerController.SAVE_FILE.exists()) {
                /* Loginfenster */
                AnchorPane loginPane = new AnchorPane();
                fxmlLoader = new FXMLLoader(getClass().getResource("../view/Einloggen.fxml"));
                loginPane = fxmlLoader.load();
                LoginViewController loginViewController = fxmlLoader.getController();
                loginViewController.setMainWindowViewController(mainWindowViewController);
                loginViewController.setStage(primaryStage);
                loginViewController.setMainStage(mainStage);

                Scene loginScene = new Scene(loginPane);
                loginScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
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
}
