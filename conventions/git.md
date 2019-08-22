## Branch Bennennungsrichtlinien
Den Git-conventions vom Atlassian-Team folgend werden Branches in
Kleinbuchstaben mit Bindestrichen benannt. Wenn man zum Beispiel eine
Test-Branch für den CredentialsController hinzufügt wird daraus:

```
credentials-controller-tests
```

## Commit Message Guidelines
Diese Richtlinien sind schamlos fast unverändert übernommen und nur übersetzt aus [diesen Guidelines](https://gist.github.com/robertpainsi/b632364184e70900af4ab688decf6f53) weil gut und einfach.

```
Kurze Zusammenfassung (72 Zeichen oder weniger)

Optionaler detaillierter Text, der die commit message weiter erklärt. Die leere
Zeile ist nötig, damit die Erklärung von der eigentlichen Message unterschieden
werden kann. Der Text sollte so formatiert sein, dass nach 72 Zeichen ein
Zeilenumbruch erfolgt.

Commit Messages werden ausschließlich auf Englisch verfasst.
Commit Messages sollen im Imperativ stehen, damit sie mit den üblichen
git-Kommandos übereinstimmen. Zum Beispiel: "Fix bug" anstatt "Fixed bug". 
```

### Eine gute Commit-Message sollte immer den folgenden Englischen Satz vervollständigen können
If applied, this commit will *\<your subject line here\>*

### Die goldenen Regeln für guten Stil in Commit-Messages
* Hauptnachricht von der Beschreibung durch leere Zeile trennen (wenn nicht
  durch GUI gegeben)
* Die erste Zeile nicht mit einem Punkt beenden
* Das erste Wort der Hauptnachricht und eines jeden Absatzes in der Beschreibung
  wird groß geschrieben
* Die Hauptnachricht hat Befehlsform
* Zeilenumbruch spätestens nach 72 Zeichen
* Die Beschreibung wird nur dafür genutzt um zu schreiben, was und warum etwas
  getan wurde, nicht um zu zeigen, wie etwas geändert wurde.

### Worüber gibt die Commit-Message Auskunft?
* Warum wurde die Änderung vorgenommen?
* Wie wird ein möglicher Fehler behoben?
* Was wird durch den Patch verändert?
* Was genau war das Ursprungsproblem?

**Die erste Zeile ist die Hauptnachricht, das Wichtigste, und das einzige was
alle lesen werden**

Details zur Englischen Fassung: https://wiki.openstack.org/wiki/GitCommitMessages#Information_in_commit_messages

### Sources
* http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html
* https://wiki.openstack.org/wiki/GitCommitMessages
* http://chris.beams.io/posts/git-commit/
