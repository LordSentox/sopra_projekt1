package de.sopra.passwordmanager.view;


import de.sopra.passwordmanager.util.LanguageProvider;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.function.Consumer;

public abstract class AbstractViewController {
    protected Stage stage;
    protected Scene scene;
    protected MainWindowViewController mainWindowViewController;

    protected String styleSheet;

    protected LanguageProvider languageProvider;

    //Nur der Name der Datei: "application/style.css" -> "style"
    //Auf null setzen zum nicht verwenden
    public void setStyleSheet(String styleSheet) {
        if (this.styleSheet != null) {
            scene.getStylesheets().remove(getClass().getResource("/stylesheets/" + this.styleSheet + ".css").toExternalForm());
        }
        this.styleSheet = styleSheet;
        if (this.styleSheet != null) {
            scene.getStylesheets().add(getClass().getResource("/stylesheets/" + this.styleSheet + ".css").toExternalForm());
        }
    }

    public void setLanguageProvider(LanguageProvider languageProvider) {
        this.languageProvider = languageProvider;
    }

    protected final String t(String identifier, String def) {
        return languageProvider.getTranslationOrDefault(identifier, def);
    }

    protected final String t(String identifier) {
        return t(identifier, identifier);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void setMainWindowViewController(MainWindowViewController mainWindowViewController) {
        this.mainWindowViewController = mainWindowViewController;
    }

    public <T extends AbstractViewController> T openModal(String ressource, Class<T> clazz, Consumer<T> preOpen) throws IOException {
        return openModal(stage, ressource, clazz, preOpen);
    }

    protected <T extends AbstractViewController> T openModal(Stage parent, String ressource, Class<T> clazz, Consumer<T> preOpen) throws IOException {
        AnchorPane categoryEditPane;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ressource));
        categoryEditPane = fxmlLoader.load();
        T controller = fxmlLoader.getController();

        if (controller instanceof MasterPasswordViewAUI)
            mainWindowViewController.getPasswordManagerController().setMasterPasswordViewAUI((MasterPasswordViewAUI) controller);
        else if (controller instanceof LoginViewAUI)
            mainWindowViewController.getPasswordManagerController().setLoginViewAUI((LoginViewAUI) controller);

        Stage newStage = new Stage();
        Scene newScene = new Scene(categoryEditPane);
        newStage.initOwner(parent);
        newStage.initStyle(StageStyle.TRANSPARENT);
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.setResizable(false);
        newScene.getStylesheets().add(getClass().getResource("/stylesheets/application.css").toExternalForm());
        newStage.setScene(newScene);
        controller.setStage(newStage);
        controller.setScene(newScene);
        controller.setLanguageProvider(languageProvider);
        languageProvider.updateNodes(clazz, controller);
        controller.setStyleSheet(styleSheet);
        controller.setMainWindowViewController(mainWindowViewController);
        preOpen.accept(controller);
        newStage.show();
        return controller;
    }

}
