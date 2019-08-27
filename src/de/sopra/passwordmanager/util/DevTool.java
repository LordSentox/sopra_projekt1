package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.controller.PasswordManagerController;
import de.sopra.passwordmanager.model.Category;
import de.sopra.passwordmanager.model.Credentials;

import java.time.LocalDateTime;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class DevTool {

    public static void fillWithData(PasswordManagerController controller) {
        Category rootCategory = controller.getPasswordManager().getRootCategory();
        rootCategory.getSubCategories().clear();
        rootCategory.getCredentials().clear();

        Category subCategory1 = new Category("Mehreres");
        Category subCategory2 = new Category("Einiges");

        Category subsubCategory = new Category("Weniges");

        rootCategory.addSubCategory(subCategory1);
        rootCategory.addSubCategory(subCategory2);
        subCategory2.addSubCategory(subsubCategory);

        Credentials creds1 = new CredentialsBuilder("TestEintrag1", "DerDödel3000", "MeinPasswort", "https://www.google.de/")
                .withSecurityQuestion("Wer bist du?", "Keine Maschine")
                .withCreated(LocalDateTime.now())
                .withNotes("Ich dachte mir so, lass mal Notes einfügen")
                .build(controller.getUtilityController());

        Credentials creds2 = new CredentialsBuilder("NameDesEintrags", "Nutzername", "pw300++", "https://www.gibts-nicht.de/")
                .withSecurityQuestion("Was stellst du dar?", "Alles oder nichts")
                .withSecurityQuestion("Für wen hälst du dich?", "Für mich")
                .withCreated(LocalDateTime.now())
                .withNotes("Es war einmal...\n\n\n\n- jemand der keine Lust hatte kreativ für dummy Daten zu werden")
                .build(controller.getUtilityController());

        LocalDateTime dateTime = LocalDateTime.now().minusDays(33);
        Credentials creds3 = new CredentialsBuilder("Wichtig", "EinerOderKeiner", "BesseresPasswort1!", "https://www.ich-bins.de/")
                .withSecurityQuestion("Der Geburtsname deiner Mutter", "Hab ich vergessen")
                .withSecurityQuestion("Wie ist dein Nutzername?", "EinerOderKeiner")
                .withCreated(dateTime)
                .withLastChanged(dateTime)
                .withChangeReminderDays(7)
                .withNotes("Man muss echt Hobbies haben all den Quatsch zu lesen,\nden ich hier reingeschrieben habe...")
                .build(controller.getUtilityController());

        subCategory1.addCredentials(creds1);
        subCategory2.addCredentials(creds2);
        subsubCategory.addCredentials(creds3);
        subsubCategory.addCredentials(creds1);
    }

}