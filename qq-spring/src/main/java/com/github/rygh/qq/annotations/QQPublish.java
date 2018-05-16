package com.github.rygh.qq.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Autowired;

@Retention(RetentionPolicy.RUNTIME) 
@Target({java.lang.annotation.ElementType.FIELD}) 
@Documented 
@Autowired
public @interface QQPublish {
	String value();
}
