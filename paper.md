Deckblatt
=========

<span id="anchor"></span>Christoph-Scheiner-Gymnasium Ingolstadt

Rahmenthema des wissenschaftspropädeutischen Seminars: *Programmieren
von Android-Apps*

Leitfach: *Informatik*

Thema der Arbeit: *Erstellung einer Android-App zum Lernen von Namen mit
besonderem Augenmerk auf Datenaustausch mit anderen Systemen*

Verfasser: *Leander Dreier*

Reifeprüfungsjahrgang: *2017*

Kursleiter: *OStR Pabst*

Abgabetermin: *08. November 2016*

Inhaltsverzeichnis

1.[Einleitung](#1. Einleitung|outline)

2.[Grundfunktionen des
Programms](#2. Grundfunktionen des Programms|outline)

3.[Das Laden der Bilder](#3. Das Laden der Bilder|outline)

4.[Die Datenbank](#4. Die Datenbank|outline)

4.1[Einstellungen in der
Datenbank](#4.1 Einstellungen in der Datenbank|outline)

4.2[Bilder in der Datenbank](#4.2 Bilder in der Datenbank|outline)

4.3[Hinzufügen von Wertepaaren](#4.3 Hinzufügen von Wertepaaren|outline)

4.3.1[Aufnahme eines neuen
Fotos](#4.3.1 Aufnahme eines neues Fotos|outline)

4.3.2[Auswahl eines bereits vorhandenen
Bildes](#4.3.2 Auswahl eines bereits vorhandenen Bildes|outline)

5.Datenaustausch mit anderen Systemen

5.1Near Field Communication (NFC)

5.2Bluetooth

5.3Vergleich und Fazit

6.Danksagungen

7.[Literaturverzeichnis](#7. Literaturverzeichnis|outline)

8.Eidesstattliche Erklärung

1. Einleitung
=============

Ich wurde letztens gefragt, was das Thema meiner Seminararbeit sei,
worauf ich antwortete: „Ich programmiere eine Namenlern-App“. Der
Fragende schien damit nicht wirklich etwas anfangen zu können, es schien
ihm nicht klar zu sein, wozu man solch eine App brauchen könnte, er
könne sich die Namen seiner Klassenkameraden schließlich auch ohne Hilfe
eines Computers merken.

Es gibt aber durchaus Anwendungsgebiete für mein Programm: Sie kann von
Lehrern verwendet werden, um sich die Namen ihrer Schüler leichter zu
merken, da dies aufgrund der vielen und häufig wechselnden Schüler oft
ein Problem ist. Ein Lehrer hat bereits angefragt, diese App verwenden
zu dürfen. Aber natürlich beschränkt sich meine Zielgruppe nicht nur auf
Lehrer. Alle Arbeitnehmer und -geber, die mit viel Personal arbeiten
oder alle anderen, die Schwierigkeiten haben, sich Namen zu merken,
können sich ihr Leben mit diesem Programm ein Stück leichter machen.

Das Grundprinzip der Anwendung ist es, je ein Bild mit einem Namen zu
verknüpfen und den Benutzer zu testen, ob er den Namen zu den Bildern
weiß.

Dass es aber nicht nur möglich ist, mit dieser Software Namen und Bilder
zu verknüpfen, zeigt eine Anfrage seitens der Klinik für Kinder- und
Jugendpsychiatrie und Psychotherapie der Kliniken St. Elisabeth in
Neuburg an der Donau, die App in der Diagnostik und Therapie von
Autismuspatienten einzusetzen. Dabei werden Emotionen mit
Gesichtsausdrücken verknüpft, so zum Beispiel ein lachender Mund mit
Freude.

2. Grundfunktionen des Programms
================================

Wie oben beschrieben, geht es in NameMemo um die Zuordnung von Namen zu
Bildern und das Lernen dieser Verknüpfung. (In der weiteren Arbeit wird
von einer Standardzuordnung Name-Bild ausgegangen.)

Dies wird folgendermaßen umgesetzt: Zuerst sieht der Nutzer ein Bild und
einen Button mit der Beschriftung „Name anzeigen“. Jetzt ist der Nutzer
angehalten, sich den zu dem gezeigten Bild gehörigen Namen zu überlegen.
Hat er das getan, drückt er den gezeigten Button, worauf nun der
tatsächliche Name und zwei weitere Buttons – „Ja“ und „Nein“ –
erscheinen. Hat der Nutzer den Namen nun gewusst, drückt er auf „Ja“,
andernfalls auf „Nein“ und ein neues Bild erscheint, wo das Prozedere
von vorne beginnt. Das Ergebnis wird in einer Datenbank abgespeichert
und beeinflusst unter anderem, wie häufig das Bild von nun an erscheinen
wird.

Zudem hat der Nutzer, ab einer gewissen Anzahl aufeinanderfolgender,
positiver Bestätigungen (Standard: 3), die Möglichkeit den Eintrag aus
der Datenbank zu löschen um ihn somit als „gelernt“ für sich abzuhaken.
Je näher man dieser Hürde ist, desto seltener wird das Bild, mit den
Standardeinstellungen, zur Abfrage aufkommen. Wurden alle Einträge
entfernt, erscheint eine Meldung darüber sowie ein Platzhaltebild.

3. Das Laden der Bilder
=======================

Zu Beginn wurden die anzuzeigenden Bilder in der ImageView durch Bitmaps
erzeugt. Macht man allerdings ein neues Foto mit dem von Android
bereitgestellten Intent, so muss dieses Bild oft erst noch gedreht
werden. Dieser ist notwendig, da die Ausrichtung des Gerätes bei dem
Erstellen des Bildes nur in Form von Exif-Daten (vergleichbar mit
Metadaten) gespeichert wird. Erzeugt man nun die Bitmap des gewünschten
Bildes, muss man diese Daten erst auslesen und das Bild manuell drehen,
bevor sie in die ImageView geladen werden kann. Dies ist sehr teuer,
weshalb es bei einigen Geräten zu Abstürzen des Programms führt.

Aus diesem Grund verwendet NameMemo nun das Framework Glide, die auf der
dazugehörigen GitHub-Seite wie folgt beschrieben wird: „Glide is a fast
and efficient open source media management and image loading framework
for Android that wraps media decoding, memory and disk caching, and
resource pooling into a simple and easy to use interface.“[^1] Glide
kümmert sich nicht nur automatisch um die korrekte Rotation des Bildes,
sondern sorgt auch für einfache Größenmanipulation dessen. Das bedeutet,
dass jedes Bild, welches in eine ImageView geladen wird, in seiner Größe
angepasst wird.

Hierfür werden beim Start der App Werte für Höhe und Breite generiert
und gespeichert. Die Breite entspricht dabei der Breite jener Fläche des
Displays, dass die Anwendung enthält.[^2] Die Höhe allerdings entspricht
nur 75% der Höhe jener Fläche um dem Platzanspruch der Action Bar
gerecht zu werden. Für die „normale“ ImageView werden diese Werte als
minimale und maximale Größe gesetzt und die Bilder werden auch in dieser
Größe von Glide geladen. Für die Bildvorschau beim Hinzufügen von
Dateien in Listenform werden die Werte vor Weiterverwendung noch
halbiert.

4. Die Datenbank
================

Wie oben bereits erwähnt, wird zum Speichern sowohl der „Bilderdaten“
als auch der Einstellungen eine Datenbank verwendet um die Daten bei
einem Schließen der Anwendung nicht zu verlieren.

Zu diesem Zweck wird Engine SQLite eingesetzt, da sie bereits auf jedem
Android-gerät vorhanden ist. Die beiden Tabellen „settings“ und
„pictures“ werden von je einer DAO (Data Access Object)-Klasse
verwaltet. Nur darauf wird im sonstigen Code zugegriffen und so sind sie
für die gesamte Interaktion mit der Datenbank zuständig.

4.1 Einstellungen in der Datenbank
----------------------------------

Die Tabelle „settings“ ist dabei, wie es der Name vermuten lässt, für
die Einstellungen zuständig. So wird hier gespeichert, wie oft der
Benutzer bei einem Bild nacheinander auf „Ja“ drücken muss, bevor er
angeboten bekommt, es zu löschen. Weiter wird gespeichert, ob die Bilder
nacheinander oder in einer zufälligen Reihenfolge nach dem in
[2.](#2. Grundfunktionen des Programms|outline) beschriebenen
Algorithmus erscheinen sollen (Standard: zufällig), ob alle Bilddateien
in einem Ordner gesammelt werden sollen: ist diese Option auf „an“
gestellt, werden Bilder, die vom Speicher hinzugefügt werden, in diesen
Ordner kopiert (Standardwert: an), und ob man eben bei diesem Hinzufügen
von Bilder aus dem Speicher des Gerätes einen Android üblichen Intent
oder einen einfacher gehaltenen Dialog in Listenform verwenden möchte
(Standard: Intent).

Die Standardwerte werden bei dem Starten des Programms gesetzt, sofern
die Einträge nicht bereits in der Datenbank vorhanden sind, also im
Normalfall nur bei dem ersten Start der App oder nach einem Reset.

In der Anwendung kann der Nutzer, wenn er sich in der MainActivity
befindet, alle oben beschriebenen Einstellungen in einem dafür
vorgesehenen Dialog ändern. In diesem Dialog findet man auch die
Möglichkeit alle Daten aus der Datenbank, also aus beiden Tabellen, zu
löschen, was auch zu einem Reset der Einstellungen führt.

Auf technischer Ebene besteht jede „Einstellung“ aus einem String
(„Kennung“) und einer Ganzzahl („Wert“). Die Kennung ist hierbei der
Primärschlüssel, um Überschneidungen zu verhindern. Soll eine Änderung
vorgenommen werden, wird in der Tabelle nach der Kennung gesucht und der
dazugehörige Wert geändert. Zum Zwischenspeichern werden die Werte,
wieder anhand ihrer Kennung, bei onStart() und bei einer Änderung aus
der Datenbank ausgelesen.

4.2 Bilder in der Datenbank
---------------------------

In der anderen Tabelle – „pictures“ – werden die verschiedenen
benötigten Daten zu den jeweiligen Bildern gesichert. Zum einen wird der
Pfad zu der entsprechenden Bilddatei gespeichert. Es wird darauf
verzichtet, die Datei aus der Datenbank rekonstruieren zu können, um
Speicherplatz und Ressourcen zu sparen. Weiter werden der vom Nutzer
zugeordnete Name, die Anzahl an Gesamtaufrufen und die Anzahl an
positiven Rückmeldungen des Nutzer (drücken von „Ja“) in Folge
gesichert. Letzterer Wert wird auf 0 zurückgesetzt, sobald nach
Erscheinen des Bildes auf „Nein“ gedrückt wird.

Der Pfad der Bilder ist dabei der Primärschlüssel der Tabelle, was
bewirkt, dass man eine Bilddatei nicht mehrmals verwenden kann. Dadurch
kann man eine gesuchte Zeile durch eben diesen Pfad genau identifizieren
um sie dann zu ändern. Für einfachere Handhabung zur Laufzeit werden
Daten zu Bildern nicht direkt aus der Datenbank abgerufen. Stattdessen
wird, wenn nötig eine Liste aller Einträge der Tabelle abgefragt und als
Array gespeichert. Dieser wird dann unter anderem dafür verwendet, das
nächste anzuzeigende Bild zu bestimmen und die Informationen dazu
auszulesen.

4.3 Hinzufügen von Wertepaaren
------------------------------

In dem Programm sind bereits drei Beispielbilder vorhanden. Der Zweck
der App ist aber natürlich eigene Bilder hinzuzufügen um dann den Namen
der jeweils zu sehenden Person zu lernen. Dafür stehen dem Nutzer lokal
zwei Möglichkeiten zur Verfügung: die Aufnahme eines neuen Fotos mit der
geräteinternen Kamera und das Auswählen eines bereits vorhandenen Bildes
in dem Speicher des Handys.

### 4.3.1 Aufnahme eines neues Fotos

Möchte man ein neues Foto schießen, so gelangt man in einen Intent der
Kamera-App seines Gerätes, wo man wie gewohnt sein Bild aufnehmen kann.
Hat man dies erfolgreich abgeschlossen, kann man dem Bild in dem
erscheinenden Dialog noch einen Namen zuweisen. Nachdem man auch dies
getan hat ist die Verknüpfung in der Datenbank gespeichert und wird von
nun an mit auftauchen. Die Bilddateien werden bei diesem Prozess in
einem eigenen Ordner gespeichert. Er befindet sich im Gerätespeicher
unter „Pictures“ und heißt wie die App, also „NameMemo“ und wird vom
Programm angelegt, falls er noch nicht existiert.

Dieses Feature benötigt keine besonderen Berechtigungen, da es die
Kamera nicht direkt, sondern über einen standardisierten Intent
verwendet.

### 4.3.2 Auswahl eines bereits vorhandenen Bildes

…

7. Literaturverzeichnis
=======================

Bump Technologies: *Glide*, Internetseite:
<https://github.com/bumptech/glide/blob/master/README.md> vom
13.07.2016, aufgerufen am 16.09.2016

Android Open Source Project: *Display*, Internetseite:
<https://developer.android.com/reference/android/view/Display.html>,
aufgerufen am 20.09.2016

[^1]: BUMP TECHNOLOGIES

[^2]: ANDROID OPEN SOURCE PROJECT
