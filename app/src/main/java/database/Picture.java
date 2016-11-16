package database;

/**
 * Class for the datasets related to the pictures
 */
public class Picture {
	private String source;
	private String name;
	private int called;
	private int gotRight;
	private int inarow;
	private String showAs;
	private int highscore;

	Picture() {}

	public String getSource() {
		return source;
	}

	public String getName() {
		return name;
	}

	public int getCalled() {
		return called;
	}

	public int getGotRight() {
		return gotRight;
	}

	public int getInarow() {
		return inarow;
	}

	public String getShowAs() {
		return (showAs == null)? name : showAs;
	}

	public int getHighscore() {
		return highscore;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setName(String name) {
		this.name = name;
	}

	void setCalled(int called) {
		this.called = called;
	}

	void setGotRight(int gotRight) {
		this.gotRight = gotRight;
	}

	void setInarow(int inarow) {
		this.inarow = inarow;
	}

	void setShowAs(String showAs) {
		this.showAs = showAs;
	}

	void setHighscore(int highscore) {
		this.highscore = highscore;
	}

	/**
	 *
	 * @param o The Picture to be compared
	 * @return  if the two Pictures match in all their attributes
	 */
	@Override
	public boolean equals(Object o) {
		Picture p = (Picture) o;
		return (p.source.equals(source) && p.name.equals(name) && p.called == called && p.gotRight == gotRight && p.inarow == inarow && p.highscore == highscore);
	}
}
