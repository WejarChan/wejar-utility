package org.wejar.redis.annotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.wejar.redis.config.WejarRedisConfig;

public class WejarRedisImportSelector implements ImportSelector{

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[] { WejarRedisConfig.class.getName() };
	}
}
