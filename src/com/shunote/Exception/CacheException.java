package com.shunote.Exception;

/**
 * Exception in Cache
 * @author jeffrey
 *
 */
public class CacheException extends Exception {

	private static final long serialVersionUID = -3766570269335871265L;

	/**

	* 带自定义错误信息的输出

	* @param message

	*/

	public CacheException(String message){

	super(message);

	}

	/**

	* 自定义错误信息和异常抛出

	* @param message

	* @param cause

	*/

	public CacheException(String message,Throwable cause){

	super(message,cause);

	}

	/**

	* 只有异常抛出

	* @param cause

	*/

	public CacheException(Throwable cause){

	super(cause);

	}


}
