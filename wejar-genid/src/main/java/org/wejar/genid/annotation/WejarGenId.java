package org.wejar.genid.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标志该字段是要生成的Id的字段
 * @ClassName:  GenId   
 * @author: WejarChan
 * @date:   2018年10月28日 上午11:00:35
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface WejarGenId {
    GenIdType type();
}
