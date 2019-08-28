package de.sopra.passwordmanager.view;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public abstract class AbstractViewController {
    protected Stage stage;
    protected MainWindowViewController mainWindowViewController;

    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void setMainWindowViewController(MainWindowViewController mainWindowViewController) {
        this.mainWindowViewController = mainWindowViewController;
    }

    protected  <T extends AbstractViewController> T openModal(String ressource, Class<T> clazz, Consumer<T> preOpen) throws IOException {
        AnchorPane categoryEditPane;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ressource));
        categoryEditPane = fxmlLoader.load();
        T controller = fxmlLoader.getController();

        Stage newStage = new Stage();
        Scene newScene = new Scene(categoryEditPane);
        newStage.initOwner(stage);
        newStage.initModality(Modality.WINDOW_MODAL);
        newScene.getStylesheets().add(getClass().getResource("../application/application.css").toExternalForm());
        newStage.setScene(newScene);
        controller.setStage(newStage);
        controller.setMainWindowViewController(mainWindowViewController);
        preOpen.accept(controller);
        newStage.show();
        return controller;
    }

}
