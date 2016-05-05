package de.leander.projekt.structure;

public enum MainState {
	NONE, /* Only for temporary use (-> "last") */
	SHOWSPICTURE, /* You can see the picture but not the name */
	SHOWSNAME, /* You can see the picture and the name */
	CAMERAINTENT /* Intent for taking a new picture */
}
