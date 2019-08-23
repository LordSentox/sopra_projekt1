package de.sopra.passwordmanager.model;

import java.util.Objects;

/**
 * Eine optionale Sicherheitsfrage, welche auf einer Netzseite zum Anmelden abgefragt werden könnte.
 */
public class SecurityQuestion {

    /**
     * Die Frage, welche der Nutzer beantworten muss
     */
    private String question;

    /**
     * Die Antwort auf die Frage.
     */
    private String answer;

    public SecurityQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
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
