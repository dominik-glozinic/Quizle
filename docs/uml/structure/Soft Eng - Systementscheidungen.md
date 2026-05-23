
# App & Sprache

### Platforme

entschieden: Android

- Die App, die wir entwickeln möchten, funktioniert am besten als Handy-App. Wir hatten die Wahl zwischen Android und iOS. Android ist deutlich einfacher als iOS und wurde deshalb ausgewählt.

### IDE

entschieden: Android Studio

- Besser als die IntelliJ-Alternative für reine Android-Apps wegen eingebautem Emulator

### Programmiersprache

entschieden: Kotlin

- Alternative: Java
    - Kotlin ist eine modernisierte Version von Java mit vollständiger Abwärtskompatibilität – kein Nachteil beim Wechsel

---

# Netzwerk

### Lokaler Server

1. SSH – zu komplex
2. HTTP – einzige Option, die mehrere gleichzeitige Verbindungen erlaubt (entschieden)

### Welcher HTTP-Server?

1. Ktor – Standard-HTTP-Framework für Kotlin
2. Netty – sehr performant
3. NanoHTTPD – leichtgewichtig, fertig implementierter Server (entschieden)

Begründung

Obwohl NanoHTTPD für Java konzipiert ist, ist es am einfachsten einzusetzen und zu integrieren. Netty ist deutlich performanter und Ktor besser für Kotlin geeignet – ihre Komplexität ist für dieses Projekt jedoch nicht notwendig.

---

### Netzwerkerkennung

1. Automatische Suche nach lokalen Quizzen
2. Manueller Beitritt per Raumcode (entschieden)

Begründung

Schwierig zuverlässig umzusetzen, besonders in großen Netzwerken mit Geräten ohne die App. Ein Einladungssystem (Master sendet Anfrage an alle Geräte) erzeugt unnötigen Datenverkehr, kann lange dauern (z.B. in Schulnetzwerken) und könnte von einer Firewall blockiert werden.

Lösung für den Raumcode

Der manuelle Beitritt ist umständlicher, aber deutlich einfacher. Als Abhilfe: Serveradresse als QR-Code komprimieren, den Nutzer scannen lassen. Googles ML Kit Barcode Scanning API bietet eine einfach zu integrierende Lösung.

### IHostNetwork und IPlayerNetwork

Da wir mit Git arbeiten, möchten wir die Situation vermeiden, dass mehrere Personen an derselben Dateien arbeiten (Host und Player), weil die Git-Integration danach schwierig wird. IHostNetwork und IPlayerNetwork eignen sich als Schnittstellen zwischen Darstellung und Netzwerk, sodass die oben genannte Option vermieden wird.