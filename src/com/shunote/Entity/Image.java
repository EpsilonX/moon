package com.shunote.Entity;

import android.graphics.Bitmap;

public class Image {
	
	private String url;
	private String data;
	private Bitmap bmp;
	
	
	public Image(String url, String data) {
		super();
		this.url = url;
		this.data = data;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Bitmap getBmp() {
		return bmp;
	}
	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}
	
	
}
