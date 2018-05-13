package com.github.rygh.qq.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) 
@Target({java.lang.annotation.ElementType.TYPE}) 
@Documented 
public @interface QQConsumer {
	String value();
}
