package org.wejar.genid.exception;

import org.wejar.genid.annotation.GenIdType;

public class GenIdTypeNotSupportException extends RuntimeException {

	private static final long serialVersionUID = -8321610262640858179L;

	public GenIdTypeNotSupportException(GenIdType type) {
		super("自动生成id，找不到相应的 IdGenerator 。 genIdType："+type);
	}
}
