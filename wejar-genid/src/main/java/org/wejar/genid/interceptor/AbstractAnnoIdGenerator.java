package org.wejar.genid.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.wejar.genid.annotation.GenIdType;
import org.wejar.genid.exception.GenIdTypeNotSupportException;
import org.wejar.genid.generator.IdGenerator;

public class AbstractAnnoIdGenerator {
	
	private Map<GenIdType, IdGenerator> generatorMap = new HashMap<GenIdType, IdGenerator>();

	/**
	 * 注册id生成器 @Title: registGenerator @param type @param generator void @throws
	 */
	public void registGenerator(GenIdType type, IdGenerator generator) {
		if (generator != null && type != null) {
			this.generatorMap.put(type, generator);
		}
	}

	/**
	 * 是否支持此类型 @Title: containsType @param type @return boolean @throws
	 */
	public boolean containsType(GenIdType type) {
		return this.generatorMap.containsKey(type);
	}

	/**
	 * 根据genIdType生成1个Id @Title: getNextId @param type @return Object @throws
	 */
	public Object getNextId(GenIdType type) {
		IdGenerator generator = this.generatorMap.get(type);
		if (generator != null) {
			return generator.nextId();
		}
		throw new GenIdTypeNotSupportException(type);
	}
}
