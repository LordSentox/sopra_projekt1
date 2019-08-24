package de.sopra.passwordmanager.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 21.08.2019
 * @since 21.08.2019
 */
public class Path {

    public static final String ROOT_CATEGORY = "Alle";

    /**
     * Die Liste aller Elemente im Pfad. Der root ist das erste Element.
     */
    private List<String> pathElements;
    /**
     * Der aktuelle Zeiger auf ein Element im Path
     */
    private int current;

    /**
     * Generiert ein Path Objekt aus seiner String Repräsentation.
     * Das neue Objekt zeigt auf das letzte Element im Pfad
     *
     * @param path der String, der den Pfad repräsentiert
     * @see #toString()
     */
    public Path(String path) {
        this(Arrays.asList(path.split(Pattern.quote("/"))));
    }

    /**
     * Generiert ein Path Objekt aus bereits vorhandenen Elementen.
     * Das neue Objekt zeigt auf das letzte Element im Pfad
     *
     * @param pathElements die bereits vorhandenen Elemente
     */
    public Path(List<String> pathElements) {
        this(pathElements, pathElements.size() - 1);
    }

    /**
     * Generiert ein Path Objekt aus bereits vorhandenen Elementen.
     * Das neue Objekt zeigt auf das angegebene Element im Pfad.
     * Leere Elemente zu Beginn oder am Ende der Liste werden entfernt.
     *
     * @param pathElements die bereits vorhandenen Elemente
     * @param current      der Zeiger auf ein Element,
     *                     Angaben über die Ränder hinaus werden auf das nächst mögliche Element angeglichen
     */
    public Path(List<String> pathElements, int current) {
        //entfernt leere Elemente am Ende und Anfang des Paths
        while (pathElements.get(0).isEmpty())
            pathElements = pathElements.subList(1, pathElements.size());
        while (pathElements.get(pathElements.size() - 1).isEmpty())
            pathElements = pathElements.subList(0, pathElements.size() - 2);
        this.pathElements = pathElements;
        navigate(current);
    }

    /**
     * Erstellt eine Kopie des Pfades, welche auf das vorherige Element im Pfad zeigt.
     * Hierfür werden die Elemente nicht geändert, nur der Zeiger auf das aktuelle Element.
     * Gibt es keinen Parent ändert sich der Zeiger nicht.
     *
     * @return eine Kopie des Pfades, welche auf den Parent des aktuellen Elementes zeigt
     */
    public Path getParent() {
        return new Path(pathElements, current - 1);
    }

    /**
     * Gib den Namen des aktuellen Elementes zurück.
     * Das aktuelle Element wird durch den internen Zeiger bestimmt.
     *
     * @return den Namen des aktuellen Elementes
     * @see #navigate(int)
     * @see #getParent()
     * @see #getChild()
     */
    public String getName() {
        return pathElements.get(current);
    }

    /**
     * Erstellt eine Kopie des Pfades, welche auf das nachfolgende Element im Pfad zeigt.
     * Hierfür werden die Elemente nicht geändert, nur der Zeiger auf das aktuelle Element.
     * Gibt es kein Child ändert sich der Zeiger nicht.
     *
     * @return eine Kopie des Pfades, welche auf das Child des aktuellen Elementes zeigt
     */
    public Path getChild() {
        return new Path(pathElements, current + 1);
    }

    /**
     * Prüft ob das aktuell gewählte Element im Pfad ein nachfolgendes Element besitzt
     *
     * @return <code>true</code> wenn das aktuell gewählte Element ein Child Element besitzt,
     * sonst <code>false</code>
     */
    public boolean hasChild() {
        return pathElements.size() - 1 > current;
    }

    /**
     * Prüft ob das aktuell gewählte Element im Pfad ein vorheriges Element besitzt
     *
     * @return <code>true</code> wenn das aktuell gewählte Element ein Parent Element besitzt,
     * sonst <code>false</code>
     */
    public boolean hasParent() {
        return current > 0;
    }

    /**
     * Erstellt eine Kopie des Pfades, welche das aktuelle Element als letztes vorsieht.
     * Ist identisch zu dem Aufruf <code>subPath(0, current+1)</code> <br>
     * Diese Methode entfernt ausschließlich Elemente nach dem aktuellen Element und ist nicht fähig Elemente einzufügen
     *
     * @return eine Kopie des Pfades, welche Elemente nach dem aktuellen Element nicht mehr enthält
     */
    public Path absolutePath() {
        return subPath(0, current + 1);
    }

    /**
     * Berechnet einen Teil des Pfades, welcher von inklusive <tt>start</tt> bis exklusive <tt>target</tt> reicht.
     * Der Zeiger im neuen Pfad zeugt auf der letzte Element im neuen Pfad.
     *
     * @param start  inklusive, start Element
     * @param target exklusive, target Element
     * @return ein Teilpfad des original Pfades
     * @see List#subList(int, int)
     */
    public Path subPath(int start, int target) {
        return new Path(pathElements.subList(start, target), target - start - 1);
    }

    /**
     * Die Anzahl der Elemente im Pfad unabhängig des Zeigers auf das aktuelle Element
     *
     * @return die Anzahl der Elemente des Pfades
     */
    public int length() {
        return pathElements.size();
    }

    /**
     * Setzt den Zeiger auf das aktuelle Element im Pfad manuell.
     * Um saubere Beziehungen zwischen den Pfaden darzustellen {@link #getParent()} und {@link #getChild()} verwenden.
     * Wenn <tt>layer</tt> über die Ränder hinaus zeigt, wird auf das nächst mögliche Element angeglichen
     *
     * @param layer der neue Zeiger auf ein Element im Pfad
     */
    public void navigate(int layer) {
        //current wird auf den nächstmöglichen Wert gesetzt, wenn der gegebene Wert außerhalb der möglichen Werte liegt
        this.current = layer < 0 ? 0 : (layer >= pathElements.size() ? pathElements.size() - 1 : layer);
    }

    /**
     * Wandelt den aktuellen Pfad in eine String Repräsentation um.
     * Die String Repräsentation beginnt mit dem ersten Element und trennt alle Weiteren mit <code>/</code> voneinander
     *
     * @return eine String Repräsentation von Path
     * @see #Path(String)
     */
    @Override
    public String toString() {
        return String.join("/", pathElements);
    }

}