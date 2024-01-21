# Installationsanleitung

## NodeJS und Angular
Installation von NodeJS (`https://nodejs.org/en/`) und Installation der Angular CLI. F¨ur die Installation
der Angular CLI wird der Befehl `npm install -g @angular/cli` in der Kommandozeile ausgef¨uhrt.
Mit `ng version` kann man die Version abfragen. Wir verwenden Angular CLI: 14.3.0 und Node: 16.13.2 Unser Projekt unterstützt die Node-Versionen 14.5.0 - 16.x.x 

## Backend
Bevor das Frontend gestarted wird, muss sichergestellt werden, dass das Backend gestartet ist. Dazu muss (in der Entwicklunsumgebung) `AdrViewerApplication` gestartet werden. 

## Frontend
Im Projekt in den Ordner `adr-viewer-ui` (pr_service_engineering_team1\frontend\adr-viewer-ui) navigieren (auf Ebene der package.json-Datei). In der Entwicklungsumgebung
(IntelliJ, Visual Studio Code, WebStorm, ...) oder in der Kommandozeile den Befehl `npm install` (oder `npm i`)
ausführen. Damit werden Module wie Bootstrap, Vis.Js usw. installiert.

Danach den WebClient mit `ng serve` starten. Im Browser zu `http://localhost:4200/` navigieren.




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
- Priorisierung der Tickets: EPIC für größere Themengebiete (Tickets, die zum EPIC gehören als Checkbox)

### GitHub Projects
- TaskBoard immer up-to-date halten
- Regelmäßig über die Tickets gehen, Tickets sofort zuweisen
- Derzeitigen Sprint (Development) mit Ticket verlinken

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
