# Installationsanleitung

## NodeJS und Angular
Installation von [NodeJS](https://nodejs.org/en/) und Installation der Angular CLI. Für die Installation
der Angular CLI wird der Befehl `npm install -g @angular/cli` in der Kommandozeile ausgeführt.
Mit `ng version` kann man die Version abfragen. Wir verwenden Angular CLI: 14.3.0 und Node: 16.13.2 Unser Projekt unterstützt die Node-Versionen 14.5.0 - 16.x.x 

## Backend
Bevor das Frontend gestarted wird, muss sichergestellt werden, dass das Backend gestartet ist. Dazu muss (in der Entwicklunsumgebung) `AdrViewerApplication` gestartet werden (Java Version: mind. 17). 
Da das Repository public ist, und für den Zugriff auf die GitHub API ein GitHub-Token benötigt wird, muss man im Ordner `backend/ADR_Viewer/src/main/resources` ein `secrets.yaml` file einfügen, welches den API-Token enthält. Aus Sicherheitsgründen kann das `secrets.yaml` file nicht direkt im Repository zur Verfügung gestellt werden. 

## Frontend
Im Projekt in den Ordner `adr-viewer-ui` (pr_service_engineering_team1\frontend\adr-viewer-ui) navigieren (auf Ebene der package.json-Datei). In der Entwicklungsumgebung
(IntelliJ, Visual Studio Code, WebStorm, ...) oder in der Kommandozeile den Befehl `npm install` (oder `npm i`)
ausführen. Damit werden Module wie Bootstrap, Vis.Js usw. installiert.

Danach den WebClient mit `ng serve` starten. Im Browser zu [http://localhost:4200/](http://localhost:4200/) navigieren.

