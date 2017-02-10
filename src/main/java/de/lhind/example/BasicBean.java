package de.lhind.example;

import java.io.Serializable;

public class BasicBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String column1;
	
	private String column2;
	
	private String column3;
	
	private boolean column4;

	public BasicBean() {	
	}

	public BasicBean(String column1, String column2, String column3, boolean column4) {
		this.column1 = column1;
		this.column2 = column2;
		this.column3 = column3;
		this.column4 = column4;
	}
	
	public String getColumn1() {
		return column1;
	}

	public void setColumn1(String column1) {
		this.column1 = column1;
	}

	public String getColumn2() {
		return column2;
	}

	public void setColumn2(String column2) {
		this.column2 = column2;
	}

	public String getColumn3() {
		return column3;
	}

	public void setColumn3(String column3) {
		this.column3 = column3;
	}

	public boolean isColumn4() {
		return column4;
	}

	public void setColumn4(boolean column4) {
		this.column4 = column4;
	}
}
