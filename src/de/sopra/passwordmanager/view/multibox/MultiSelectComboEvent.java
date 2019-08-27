package de.sopra.passwordmanager.view.multibox;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * <h1>projekt1</h1>
 *
 * @author Julius Korweck
 * @version 27.08.2019
 * @since 27.08.2019
 */
public class MultiSelectComboEvent extends Event {
    static final long serialVersionUID = 1L;
    public static EventType<MultiSelectComboEvent> EVENT_OK = new EventType<>(ANY, "EVENT_OK");
    public static EventType<MultiSelectComboEvent> EVENT_CANCEL = new EventType<>(ANY, "EVENT_CANCEL");

    private MultiSelectionComboBox cmb;

    public MultiSelectComboEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public MultiSelectComboEvent(MultiSelectionComboBox cmb, EventType<? extends Event> eventType) {
        super(eventType);
        this.cmb = cmb;
    }

}