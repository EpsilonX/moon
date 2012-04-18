package com.shunote.Entity;

/**
 * Memory
 * @author Jeffrey
 * @since 2012-3-31
 */
public class Memory {
	private static Memory instance = null;
	
	private Memory(){};
	
	public static Memory getInstance(){
		if (instance==null){
			instance = new Memory();
			return instance;
		}else{
			return instance;
		}			
	}

}
