Deckblatt
Christoph-Scheiner-Gymnasium Ingolstadt
Rahmenthema des wissenschaftspropädeutischen Seminars: Programmieren von Android-Apps
Leitfach: Informatik
Thema der Arbeit: Erstellung einer Android-App zum Lernen von Namen mit besonderem Augenmerk auf Datenaustausch mit anderen Systemen
Verfasser: Leander Dreier
Reifeprüfungsjahrgang: 2017
Kursleiter: OStR Pabst
Abgabetermin: 
Inhaltsverzeichnis
1.	Einleitung
2.	Grundfunktionen des Programms
3.	Das Laden der Bilder
4.	Die Datenbank
4.1	Einstellungen in der Datenbank
4.2	Bilder in der Datenbank
4.3	Hinzufügen von Wertepaaren
4.3.1	Aufnahme eines neuen Fotos
4.3.2	Auswahl eines bereits vorhandenen Bildes
5.	Datenaustausch mit anderen Systemen
5.1	Near Field Communication (NFC)
5.2	Bluetooth
5.3	Vergleich und Fazit
6.	Danksagungen
7.	Literaturverzeichnis
8.	Eidesstattliche Erklärung
1. Einleitung
Ich wurde letztens gefragt, was das Thema meiner Seminararbeit sei, worauf ich antwortete: „Ich programmiere eine Namenlern-App“. Der Fragende schien damit nicht wirklich etwas anfangen zu können, es schien ihm nicht klar zu sein, wozu man solch eine App brauchen könnte, er könne sich die Namen seiner Klassenkameraden schließlich auch ohne Hilfe eines Computers merken.

Es gibt aber durchaus Anwendungsgebiete für mein Programm: Sie kann von Lehrern verwendet werden, um sich die Namen ihrer Schüler leichter zu merken, da dies aufgrund der vielen und häufig wechselnden Schüler oft ein Problem ist. Ein Lehrer hat bereits angefragt, diese App verwenden zu dürfen. Aber natürlich beschränkt sich meine Zielgruppe nicht nur auf Lehrer. Alle Arbeitnehmer und -geber, die mit viel Personal arbeiten oder alle anderen, die Schwierigkeiten haben, sich Namen zu merken, können sich ihr Leben mit diesem Programm ein Stück leichter machen. 

Das Grundprinzip der Anwendung ist es, je ein Bild mit einem Namen zu verknüpfen und den Benutzer zu testen, ob er den Namen zu den Bildern weiß.

Dass es aber nicht nur möglich ist, mit dieser Software Namen und Bilder zu verknüpfen, zeigt eine Anfrage seitens der Klinik für Kinder- und Jugendpsychiatrie und Psychotherapie der Kliniken St. Elisabeth in Neuburg an der Donau, die App in der Diagnostik und Therapie von Autismuspatienten einzusetzen. Dabei werden Emotionen mit Gesichtsausdrücken verknüpft, so zum Beispiel ein lachender Mund mit Freude.

2. Grundfunktionen des Programms
Wie oben beschrieben, geht es in NameMemo um die Zuordnung von Namen zu Bildern und das Lernen dieser Verknüpfung. (In der weiteren Arbeit wird von einer Standardzuordnung Name-Bild ausgegangen.)

Dies wird folgendermaßen umgesetzt: Zuerst sieht der Nutzer ein Bild und einen Button mit der Beschriftung „Name anzeigen“. Jetzt ist der Nutzer angehalten, sich den zu dem gezeigten Bild gehörigen Namen zu überlegen. Hat er das getan, drückt er den gezeigten Button, worauf nun der tatsächliche Name und zwei weitere Buttons – „Ja“ und „Nein“ – erscheinen. Hat der Nutzer den Namen nun gewusst, drückt er auf „Ja“, andernfalls auf „Nein“ und ein neues Bild erscheint, wo das Prozedere von vorne beginnt. Das Ergebnis wird in einer Datenbank abgespeichert und beeinflusst unter anderem, wie häufig das Bild von nun an erscheinen wird.

Zudem hat der Nutzer, ab einer gewissen Anzahl aufeinanderfolgender, positiver Bestätigungen (Standard: 3), die Möglichkeit den Eintrag aus der Datenbank zu löschen um ihn somit als „gelernt“ für sich abzuhaken. Je näher man dieser Hürde ist, desto seltener wird das Bild, mit den Standardeinstellungen, zur Abfrage aufkommen. Wurden alle Einträge entfernt, erscheint eine Meldung darüber sowie ein Platzhaltebild.

3. Das Laden der Bilder
Zu Beginn wurden die anzuzeigenden Bilder in der ImageView durch Bitmaps erzeugt. Macht man allerdings ein neues Foto mit dem von Android bereitgestellten Intent, so muss dieses Bild oft erst noch gedreht werden. Dieser ist notwendig, da die Ausrichtung des Handys bei dem Erstellen des Bildes nur in Form von Exif-Daten (vergleichbar mit Metadaten) gespeichert wird. Erzeugt man nun die Bitmap des gewünschten Bildes, muss man diese Daten erst auslesen und das Bild manuell drehen, bevor sie in die ImageView geladen werden kann. Dies ist sehr teuer, weshalb es bei einigen Geräten zu Abstürzen des Programms führt.

Aus diesem Grund verwendet NameMemo nun das Framework Glide, die auf der dazugehörigen GitHub-Seite wie folgt beschrieben wird: „Glide is a fast and efficient open source media management and image loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface.“i Glide kümmert sich nicht nur automatisch um die korrekte Rotation des Bildes, sondern sorgt auch für einfache Größenmanipulation dessen.

4. Die Datenbank
Wie oben bereits erwähnt, wird zum Speichern sowohl der „Bilderdaten“ als auch der Einstellungen eine Datenbank verwendet um die Daten bei einem Schließen der Anwendung nicht zu verlieren.

4.1 Einstellungen in der Datenbank
