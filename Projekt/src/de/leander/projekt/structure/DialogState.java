package de.leander.projekt.structure;

public enum DialogState {
	NONE, /* Standart */
	DELETE, /* Delete picture because you have learned it */
	INFO, /* Appinfo */
	MFILE, /* Delete the reference to a no longer existing picture */
	NSHOT, /* Take a new picture with the camera */
	OPIC, /* Open picture from file on device */
	PREVIEW /* Shows preview of picture in OPIC */	
}
