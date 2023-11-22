# Projekt Guidelines

### Workflow

- **Ticket zuweisen** 
  - Zuweisen des Assignees im GitHub Projects
- **Branch erstellen** 
  - Bei Entwicklung (Frontend oder Backend). Unter Issues das entsprechende
    Issue auswählen. Im Development-Tab rechts auf create branch klicken (checkout locally). Es
    wird ein neuer Branch erstellt, der nach dem Ticket &lt;Ticketnummer-titel&gt; benannt ist. Mit
    den Befehlen git fetch origin und git checkout &lt;newBranch&gt; kann man in seiner lokalen
    Entwicklungsumgebung arbeiten
- **Git Commit** - Entweder in der IDE (IntelliJ) oder mit einem Tool (GitHub Desktop) einen
  Commit erstellen. Namenskonvention für die Commit-Message: &lt;#ticketNumber&gt;:
  &lt;description&gt;
- **Git Push** 
  - Sobald das Ticket abgeschlossen ist (alle Requirements erfüllt), wird ein Git-Push
    durchgeführt (es darf keine Errors geben, Warnings wenn möglich reduzieren).
- **Pull Requests** 
  - Bei Änderungen am Code immer eine neue Pull Request erstellen. Bei
    wichtigen Änderungen: Informieren des Teams in der What’s App Gruppe, damit möglichst
    bald ein Review durchgeführt wird und der Branch gemerged werden kann.
- **Branch löschen**
  - Sobald ein Merge durchgeführt wurde, wird der Branch gelöscht
- **Naming conventions**
  - Commits: <#ticketNumber>: <description> 	Beispiel: #31: fixed bug in adr list
  - Code: CamelCase für Variablen, Funktonen, Klassen, etc.
    - Beispiel Variable: adrList
    - Beispiel Funktion: calculateNumberOfEntries
    - Beispiel Klasse: AdrParser

- **Dokumentation**
  - Wenn für Verständnis nötig: Klassen, Funktionen, Variablen, etc. kommentieren
  - Funktionen sollen im Code (mit Kommentaren) dokumentiert werden.
  - Komplexere Funktionen/Architekturentscheidungen dokumentieren - ADR für das
    Projekt dokumentieren - Graal Template verwenden (später eigenes Template,
    Datum)
- **Tests**
  - In Angular, spec.ts file, jede Komponente soll getestet werden
  - Code Coverage überprüfen, 80% - Run with Coverage (Backend)
  - Coverage Report generieren (Angular)

### GitHub Issues
- Aussagekräftiger Titel - [RESEARCH], [DOCUMENTATION], [FRONTEND], [BACKEND], [ORGANIZATION], [REFINEMENT], [TESTING]
- Klare Beschreibung inklusive aller relevanten Informationen
- Wenn möglich Screenshots verwenden
- Priorisierung der Tickets (eventuell über 2. Label)

### GitHub Projects
- TaskBoard immer up-to-date halten
- Regelmäßig über die Tickets gehen, Tickets sofort zuweisen

### Meetings
- Es gibt regelmäßige Meetings (wenn möglich wöchentlich).
- Backlog wird durchgesprochen, neue Issues angelegt, Austausch Fortschritt, nächste Schritte

### Pipeline
- GitHub Actions Workflow erstellen
- Nach jedem Push wird der Workflow angestoßen, und das Projekt wird neu gebaut

### Zeitaufzeichnung
- Excel wird verwendet, Zeitaufzeichnung wird tagesaktuell gehalten
- Jeder hat eine eigene Tabelle (Stunden, Aufgabenbereich, Tätigkeit)
- Burndown-Chart und Pie Chart werden automatisch aktualisiert
