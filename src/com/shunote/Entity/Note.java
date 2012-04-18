package com.shunote.Entity;

/**
 * Note
 * @author Jeffrey
 * @since 2012-3-31
 *
 */
public class Note {
	
	private int id;
	private String name;
	private int root;
	private String json;
	
	public Note(int id, String name, int root,String json) {
		super();
		this.id = id;
		this.name = name;
		this.root = root;
		this.json = json;
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	public int getRoot() {
		return root;
	}
	
	public String getJson(){
		return json;
	}

}
