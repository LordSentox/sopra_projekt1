package de.sopra.passwordmanager.model;

import de.sopra.passwordmanager.util.EncryptedString;

import java.util.Objects;

/**
 * Eine optionale Sicherheitsfrage, welche auf einer Netzseite zum Anmelden abgefragt werden könnte.
 */
public class SecurityQuestion {

    /**
     * Die Frage, welche der Nutzer beantworten muss
     */
    private EncryptedString question;

    /**
     * Die Antwort auf die Frage.
     */
    private EncryptedString answer;

    public SecurityQuestion(EncryptedString question, EncryptedString answer) {
        this.question = question;
        this.answer = answer;
    }

    public EncryptedString getQuestion() {
        return question;
    }

    public EncryptedString getAnswer() {
        return answer;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SecurityQuestion other = (SecurityQuestion) object;
        return Objects.equals(question, other.question) &&
                Objects.equals(answer, other.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, answer);
    }
}
