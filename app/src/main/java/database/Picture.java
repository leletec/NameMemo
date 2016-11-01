package database;

/**
 * Class for the datasets related to the pictures
 */
public class Picture {
	private String source;
	private String name;
	private int called;
	private int inarow;
	private String showAs;

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

	public int getInarow() {
		return inarow;
	}

	public String getShowAs() {
		return (showAs == null)? name : showAs;
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

	void setInarow(int inarow) {
		this.inarow = inarow;
	}

	void setShowAs(String showAs) {
		this.showAs = showAs;
	}

	/**
	 *
	 * @param o The Picture to be compared
	 * @return  if the two Pictures match in all their attributes
	 */
	@Override
	public boolean equals(Object o) {
		Picture p = (Picture) o;
		return (p.source.equals(source) && p.name.equals(name) && p.called == called && p.inarow == inarow);
	}
}
