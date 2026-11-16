# Clean Client (Fabric, Minecraft 1.21.11)

Menü: **Rechts-Shift** (in den Minecraft-Steuerungen änderbar).
Linksklick auf ein Modul = an/aus, Rechtsklick = Einstellungen ein-/ausklappen.
Einstellungen werden in `config/cleanclient.properties` gespeichert.

## Bauen
1. JDK 21 installieren.
2. Wrapper erzeugen (einmalig): `gradle wrapper --gradle-version 9.2.1`
3. `./gradlew build` -> Jar liegt in `build/libs/`
4. Jar in den `mods`-Ordner legen (Fabric Loader + Fabric API nötig).

Zum Entwickeln: Ordner in IntelliJ IDEA öffnen, Gradle importieren, `./gradlew runClient`.

## Neue Module hinzufügen
- In `ModuleManager.init()` ein `new Module(...)` mit Settings eintragen.
- Anzeige in `Hud.render()` ergänzen.
