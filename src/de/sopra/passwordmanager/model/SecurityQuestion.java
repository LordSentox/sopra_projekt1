package de.sopra.passwordmanager.model;

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
}
