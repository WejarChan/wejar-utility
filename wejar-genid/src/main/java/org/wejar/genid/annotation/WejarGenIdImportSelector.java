package org.wejar.genid.annotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.wejar.genid.config.GenIdConfig;

public class WejarGenIdImportSelector implements ImportSelector{

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[] { GenIdConfig.class.getName() };
	}

}
