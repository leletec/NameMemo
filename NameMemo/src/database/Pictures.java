package database;

/**
 * TODO
 */
public class Pictures {
	private String source;
	private String name;
	private int called;
	private int gotright;
	private int inarow;
	private int imagingmode;
	public final static int Imported = 10;
	public final static int Camera = 20;
	public final static int Phone = 30;

	public Pictures(String source, String name, int called, int gotright, int inarow, int imagingmode) {
		this.source = source;
		this.name = name;
		this.called = called;
		this.gotright = gotright;
		this.inarow = inarow;
		this.imagingmode = imagingmode;
	}

	public Pictures() {
	}

	public String getSource() {
		return source;
	}

	public String getName() {
		return name;
	}

	public int getCalled() {
		return called;
	}

	public int getGotright() {
		return gotright;
	}

	public int getInarow() {
		return inarow;
	}
	
	public int getImagingcode() {
		return imagingmode;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCalled(int called) {
		this.called = called;
	}

	public void setGotright(int gotright) {
		this.gotright = gotright;
	}

	public void setInarow(int inarow) {
		this.inarow = inarow;
	}
	
	public void setImagingcode(int imagingcode) {
		this.imagingmode = imagingcode;
	}
}