package de.sopra.passwordmanager.util;

import de.sopra.passwordmanager.view.AbstractViewController;
import javafx.scene.control.Labeled;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 29.08.2019
 * @since 29.08.2019
 */
public class LanguageProvider {

    private Properties translation;

    public LanguageProvider() {
        translation = new Properties();
    }

    public void setBaseFile(Properties properties) {
        translation = properties;
    }

    public <T extends AbstractViewController> void updateNodes(Class<T> clazz, T controller) {
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
        fields.forEach(field -> {
            try {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                castAndTranslate(field.getName(), field.get(controller));
                field.setAccessible(accessible);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void castAndTranslate(String fieldName, Object node) {
        if (node instanceof Labeled) {
            translateNode(fieldName, (Labeled) node);
        }
    }

    private boolean translateNode(String fieldName, Labeled node) {
        if (hasTranslation(fieldName)) {
            node.setText(getTranslationOrDefault(fieldName, fieldName));
            return true;
        } else System.out.println(fieldName);
        return false;
    }

    public boolean hasTranslation(String identifier) {
        return translation.getProperty(identifier) != null;
    }

    public String getTranslationOrDefault(String identifier, String def) {
        return translation.getProperty(identifier, def);
    }

}