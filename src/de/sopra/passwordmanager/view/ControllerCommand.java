package de.sopra.passwordmanager.view;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;
import de.sopra.passwordmanager.util.CredentialsBuilder;
import de.sopra.passwordmanager.util.DevUtil;
import de.sopra.passwordmanager.view.dialog.DialogPack;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 30.08.2019
 * @since 30.08.2019
 */
public enum ControllerCommand implements Consumer<PasswordManagerController> {

    NONE("none") {
        @Override
        public void accept(PasswordManagerController controller) {
            System.out.println("Karpador uses splash... nothing happens");
        }
    },
    DATA("dummy") {
        @Override
        public void accept(PasswordManagerController controller) {
            DevUtil.fillWithData(controller);
            controller.getMainWindowAUI().refreshLists();
        }
    },
    LANG_EN("lang:en") {
        @Override
        public void accept(PasswordManagerController controller) {
            MainWindowViewController mainview = (MainWindowViewController) controller.getMainWindowAUI();
            try {
                mainview.languageProvider.loadFromResource("en_EN");
            } catch (Exception e) {
                mainview.showError(e);
                e.printStackTrace();
            }
            mainview.languageProvider.updateNodes(MainWindowViewController.class, mainview);
        }
    },
    LANG_JA("maekel") {
        @Override
        public void accept(PasswordManagerController controller) {
            MainWindowViewController mainview = (MainWindowViewController) controller.getMainWindowAUI();
            try {
                mainview.languageProvider.loadFromResource("ja_JA");
            } catch (Exception e) {
                mainview.showError(e);
                e.printStackTrace();
            }
            mainview.languageProvider.updateNodes(MainWindowViewController.class, mainview);
        }
    },
    LANG_DE("dieter") {
        @Override
        public void accept(PasswordManagerController controller) {
            MainWindowViewController mainview = (MainWindowViewController) controller.getMainWindowAUI();
            try {
                mainview.languageProvider.loadFromResource("de_DE");
            } catch (Exception e) {
                mainview.showError(e);
                e.printStackTrace();
            }
            mainview.languageProvider.updateNodes(MainWindowViewController.class, mainview);
        }
    },
    REMOVE_ALL("doris") {
        @Override
        public void accept(PasswordManagerController controller) {
            controller.removeAll();
            PasswordManagerController.SAVE_FILE.delete();
            System.exit(0);
        }
    },
    HARTMUT("hartmut") {
        @Override
        public void accept(PasswordManagerController controller) {
            Category root = controller.getPasswordManager().getRootCategory();
            Set<Credentials> credentials = root.getAllCredentials();
            for (Credentials creds : credentials) {
                CredentialsBuilder builder = new CredentialsBuilder(creds, controller.getUtilityController());
                builder.withPassword("IschBinDaHartmuht")
                        .withUserName("HardCourage");
                Collection<Category> categories = controller.getCredentialsController().getCategoriesOfCredentials(root, creds);
                controller.getCredentialsController().updateCredentials(creds, builder, categories);
            }

            for (int i = 60; i < 70; i++) {
                controller.getCategoryController().createCategory(root, "Spanien Urlaub " + i);
            }

            controller.getMasterPasswordController().changePassword("IschBinDaHartmuht", 999);
            DialogPack pack = new DialogPack("Hartmuts Information", "Was würde Hartmut sagen?", "Internet installiert. Internet gecheckt.");
            pack.addButton("Das Leben ist das, was man daraus macht", null);
            pack.addButton("Nachts ist es kälter, als draussen", null);
            pack.addButton("Das Leben ist das, was man daraus macht", null);
            pack.addButton("wewehade", null);
            DialogPack pack2 = new DialogPack("Hartmuts neues Passwort", "Das neue Masterpasswort lautet:", "IschBinDaHartmuht");
            pack2.addButton("Ok", null);
            pack.setNext(pack2);
            pack.open();

        }
    };

    private String name;

    ControllerCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ControllerCommand getByName(String name) {
        for (ControllerCommand command : values())
            if (command.getName().equals(name.toLowerCase()))
                return command;
        return NONE;
    }

}