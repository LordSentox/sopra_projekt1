package de.sopra.passwordmanager.application;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.view.LoginViewController;
import de.sopra.passwordmanager.view.MainWindowViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application{
		
	@Override
	public void start(Stage primaryStage) {
		
		try {
			/* Hauptfenster */
			AnchorPane mainPane = new AnchorPane();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/Hauptfenster.fxml"));
			mainPane = fxmlLoader.load();
			MainWindowViewController mainWindowViewController = (MainWindowViewController) fxmlLoader.getController();
			mainWindowViewController.setPasswordManagerController(new PasswordManagerController());
			
			Stage mainStage = new Stage();
			Scene mainScene = new Scene(mainPane);
			mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			mainStage.setScene(mainScene);
			
			/* Loginfenster */
			AnchorPane loginPane = new AnchorPane();
			fxmlLoader = new FXMLLoader(getClass().getResource("../view/Einloggen.fxml"));
			loginPane = fxmlLoader.load();
			LoginViewController loginViewController = (LoginViewController) fxmlLoader.getController();
			loginViewController.setMainWindowViewController(mainWindowViewController);
			loginViewController.setStage(primaryStage);
			loginViewController.setMainStage(mainStage);
			
			
			Scene loginScene = new Scene(loginPane);
			loginScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(loginScene);
			primaryStage.show();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
