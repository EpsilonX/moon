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
	private String date;
	private int nodenum;
	public Note(int id, String name, int root,String json,String date,int nodenum) {
		super();
		this.id = id;
		this.name = name;
		this.root = root;
		this.json = json;
		this.date = date;
		this.nodenum =nodenum;
	}
	
	public int getNodenum() {
		return nodenum;
	}

	public void setNodenum(int nodenum) {
		this.nodenum = nodenum;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public void setName(String name) {
		this.name = name;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
