package de.lhind.example;

import java.io.Serializable;

public class BasicBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String title;
	
	private String label;
	
	private String value;
	
	private boolean additional;

	public BasicBean() {	
	}

	public BasicBean(String column1, String column2, String column3, boolean column4) {
		this.title = column1;
		this.label = column2;
		this.value = column3;
		this.additional = column4;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String column1) {
		this.title = column1;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String column2) {
		this.label = column2;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String column3) {
		this.value = column3;
	}

	public boolean isAdditional() {
		return additional;
	}

	public void setAdditional(boolean column4) {
		this.additional = column4;
	}
}
