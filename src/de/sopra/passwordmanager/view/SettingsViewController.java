package de.sopra.passwordmanager.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.sopra.passwordmanager.view.dialog.SimpleConfirmation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.sopra.passwordmanager.view.MainWindowViewController.currentTheme;
import static de.sopra.passwordmanager.view.MainWindowViewController.player;

public class SettingsViewController extends AbstractViewController {

    class ThemeItem {
        private String name;
        private String themePath;
        private String musicPath;

        public ThemeItem(String name, String propertiesData) {
            this.name = name.replace("_", " ");
            if (propertiesData == null) {
                this.themePath = null;
                this.musicPath = null;
            } else if (propertiesData.contains("?")) {
                String[] split = propertiesData.split(Pattern.quote("?"));
                this.themePath = split[0];
                this.musicPath = split[1];
            } else themePath = propertiesData;
        }

        public String getName() {
            return name;
        }

        public String getMusicPath() {
            return musicPath;
        }

        public String getThemePath() {
            return themePath;
        }

        public void setTheme(AbstractViewController viewController) {
            viewController.setStyleSheet(themePath);
        }

        public void playMusic() {
            try{                
                if (player == null) {
                    if (musicPath == null) return;
                    Media media = new Media(ClassLoader.getSystemResource(musicPath).toString());
                    player = new MediaPlayer(media);
                    player.setAutoPlay(true);
                    player.setCycleCount(MediaPlayer.INDEFINITE);
                    player.play();
                    player.setVolume(1.0);
                } else {
                    player.stop();
                    player = null;
                    playMusic();
                }
            } catch (Exception e) {
                mainWindowViewController.showError(e); 
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @FXML
    private JFXComboBox<ThemeItem> comboBoxSelectTheme;

    @FXML
    private JFXButton buttonSetMP, buttonImport, buttonExport, buttonResetAll;

    public void onChangeMasterpasswordClicked() {
    		mainWindowViewController.masterPassordIsShit();

        try {
            /* MasterpasswortSetzenFenster */
            openModal(stage, "/Masterpasswort-setzen.fxml", MasterPasswordViewController.class, control -> {
                control.init();
                control.openedBySettings();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void onImportDataClicked() {
		mainWindowViewController.masterPassordIsShit();
		
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Öffne Datei");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("XML", "*.xml"));
        File fileToOpen = fileChooser.showOpenDialog(stage);
        if (fileToOpen != null) {
            try {
                openModal(stage, "/Einloggen.fxml", LoginViewController.class, controller ->
                {
                    controller.setSourceFile(fileToOpen);
                    controller.setBackTo(stage);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onExportDataClicked() {
		mainWindowViewController.masterPassordIsShit();
		
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichere Datei");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("XML", "*.xml"));
        File fileToSave = fileChooser.showSaveDialog(stage);
        if (fileToSave != null) {
            // Hinzufügen der XML-Dateiendung, falls sie noch nicht vom Benutzer eingetragen wurde.
            if (!fileToSave.toString().toLowerCase().endsWith(".xml")) {
                fileToSave = new File(fileToSave.toString().concat(".xml"));
            }
            mainWindowViewController.getPasswordManagerController().getIOController().exportFile(fileToSave);
        }
    }

    public void onResetDataClicked() {
		mainWindowViewController.masterPassordIsShit();
		
        SimpleConfirmation removeConfirmation = new SimpleConfirmation("Passwortmanager zurücksetzen", null, "Passwortmanager wirklich zurücksetzen?") {
            @Override
            public void onSuccess() {
                mainWindowViewController.getPasswordManagerController().removeAll();
            }
        };
        removeConfirmation.open();
    }

    public void init() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/stylesheets/sheets.properties"));
        } catch (IOException e) {
            mainWindowViewController.showError(e);
            e.printStackTrace();
        }
        List<ThemeItem> items = properties.entrySet().stream()
                .map(entry -> new ThemeItem(entry.getKey().toString(), entry.getValue().toString()))
                .collect(Collectors.toList());
        items.add(0, new ThemeItem(" - Kein Theme - ", null));
        comboBoxSelectTheme.setItems(FXCollections.observableArrayList(items));
        if (currentTheme == null)
            comboBoxSelectTheme.getSelectionModel().select(0);
        else
            comboBoxSelectTheme.getSelectionModel().select(
                    comboBoxSelectTheme.getItems().stream().filter(item -> item.getName().equals(currentTheme)).findAny().get());
        comboBoxSelectTheme.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentTheme = newValue.getName();
            newValue.setTheme(SettingsViewController.this);
            newValue.setTheme(mainWindowViewController);
            newValue.playMusic();
        });
    }

    public void onCancelSettingsClicked() {
        stage.close();
    }

    public void onCloseClicked() {
        stage.close();
    }

}
