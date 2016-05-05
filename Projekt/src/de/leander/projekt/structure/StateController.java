package de.leander.projekt.structure;

import android.util.Log;

public class StateController {

	private MainState mainstate;
	private DialogState dialogstate;
	private MainState last;

	
	public StateController(MainState mainstate, DialogState dialogstate, MainState last) {
		super();
		this.mainstate = mainstate;
		this.dialogstate = dialogstate;
		this.last = last;
	}

	public MainState getMainstate() {
		return mainstate;
	}

	public DialogState getDialogstate() {
		return dialogstate;
	}

	public MainState getLast(){
		return last;
	}
	
	public StateController() {
		mainstate = MainState.SHOWSPICTURE;
		dialogstate = DialogState.NONE;
		last = MainState.NONE;
	}

	public void showInfoDialog() throws Exception {
		if (mainstate != MainState.SHOWSNAME
				&& mainstate != MainState.SHOWSPICTURE)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.NONE)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		dialogstate = DialogState.INFO;
		Log.d("StateController", "changed dialogstate to '" + dialogstate + "'");
	}

	public void dismissInfoDialog() throws Exception {
		if (mainstate != MainState.SHOWSNAME
				&& mainstate != MainState.SHOWSPICTURE)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.INFO)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		dialogstate = DialogState.NONE;
		Log.d("StateController", "changed dialogstate to '" + dialogstate + "'");
	}

	public void showDeleteDialog() throws Exception {
		if (mainstate != MainState.SHOWSNAME)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.NONE)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		dialogstate = DialogState.DELETE;
		Log.d("StateController", "changed dialogstate to '" + dialogstate + "'");
	}

	public void dismissDeleteDialog() throws Exception {
		if (mainstate != MainState.SHOWSNAME)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.DELETE)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		dialogstate = DialogState.NONE;
		Log.d("StateController", "changed dialogstate to '" + dialogstate + "'");
	}

	public void showPicture() throws Exception {
		if (mainstate != MainState.SHOWSNAME
				&& mainstate != MainState.CAMERAINTENT)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.NONE
				&& dialogstate != DialogState.DELETE)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		mainstate = MainState.SHOWSPICTURE;
		Log.d("StateController", "changed mainstate to '" + mainstate + "'");
	}

	public void showName() throws Exception {
		if (mainstate != MainState.CAMERAINTENT
				&& mainstate != MainState.SHOWSPICTURE)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.NONE)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		mainstate = MainState.SHOWSNAME;
		Log.d("StateController", "changed mainstate to '" + mainstate + "'");
	}
	
	public void showMFileDialog() throws Exception {
		if (mainstate != MainState.SHOWSNAME)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.NONE)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		dialogstate = DialogState.MFILE;
		Log.d("StateController", "changed dialogstate to '" + dialogstate + "'");
	}
	
	public void dismissMFileDialog() throws Exception {
		if (mainstate != MainState.SHOWSNAME)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.MFILE)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		dialogstate = DialogState.NONE;
		Log.d("StateController", "changed dialogstate to '" + dialogstate + "'");
	}

	public void showCameraIntent() throws Exception {
		if (mainstate != MainState.SHOWSNAME
				&& mainstate != MainState.SHOWSPICTURE)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.NONE)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		last = mainstate;
		Log.d("StateController", "changed last to '" + last + "'");
		mainstate = MainState.CAMERAINTENT;
		Log.d("StateController", "changed mainstate to '" + mainstate + "'");
	}
	
	public void dismissCameraIntent() throws Exception {
		if (mainstate != MainState.CAMERAINTENT)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.NONE)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		mainstate = last;
		Log.d("StateController", "changed mainstate to '" + mainstate + "'");
		last = MainState.NONE;
		Log.d("StateController", "changed last to '" + last + "'");
	}
	
	public void showNShotDialog() throws Exception {
		if (mainstate != MainState.CAMERAINTENT)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.NONE)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		dialogstate = DialogState.NSHOT;
		Log.d("StateController", "changed dialogstate to '" + dialogstate + "'");
	}
	
	public void dismissNShotDialog() throws Exception {
		if (mainstate != MainState.CAMERAINTENT)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.NSHOT)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		dialogstate = DialogState.NONE;
		Log.d("StateController", "changed dialogstate to '" + dialogstate + "'");
	}
	
	public void showAddPicFSDialog() throws Exception {
		if (mainstate != MainState.SHOWSNAME
				&& mainstate != MainState.SHOWSPICTURE)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.NONE)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		dialogstate = DialogState.OPIC;
		Log.d("StateController", "changed dialogstate to '" + dialogstate + "'");
	}

	public void dismissAddPicFSDialog() throws Exception {
		if (mainstate != MainState.SHOWSNAME
				&& mainstate != MainState.SHOWSPICTURE)
			throw new Exception("Wrong mainstate: " + mainstate.toString());
		if (dialogstate != DialogState.OPIC)
			throw new Exception("Wrong dialogstate: " + dialogstate.toString());
		dialogstate = DialogState.NONE;
		Log.d("StateController", "changed dialogstate to '" + dialogstate + "'");
	}
}
