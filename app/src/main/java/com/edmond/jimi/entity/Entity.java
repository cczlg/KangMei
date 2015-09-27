package com.edmond.jimi.entity;

import java.util.Date;

public class Entity {
	public int id;
	public Date created;

	private String sortLetters;


	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
