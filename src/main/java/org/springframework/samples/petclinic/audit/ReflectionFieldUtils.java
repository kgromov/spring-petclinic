package org.springframework.samples.petclinic.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

public class ReflectionFieldUtils {
	private static final Logger log = LoggerFactory.getLogger(ReflectionFieldUtils.class);


	public static  <T> Map<String, Object> getObjectValuesByField(T object, Class<? super T> baseClass) {
//		object.getClass().getDeclaredFields();
		return analyzeType(object.getClass()).keySet()
			.stream()
			.map(field -> ReflectionUtils.findField(object.getClass(), field))
			.filter(Objects::nonNull)
			.peek(ReflectionUtils::makeAccessible)
			.collect(Collectors.toMap(Field::getName, field -> ofNullable(ReflectionUtils.getField(field, object)).orElse("")));
	}
	private static Map<String, Method> analyzeType(Class<?> type) {
		try {
			Map<String, Method> fieldToGetter = new HashMap<>();
			BeanInfo beanInfo = Introspector.getBeanInfo(type, Object.class);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				String propertyName = propertyDescriptor.getName();
				Method getter = propertyDescriptor.getReadMethod();
				Method setter = propertyDescriptor.getWriteMethod();
//                Field field = type.getDeclaredField(propertyName);
				log.trace("Property " + propertyName);
				log.trace("Getter " + getter);
				log.trace("Setter " + setter);
				fieldToGetter.put(propertyName, getter);
			}
			return fieldToGetter;
		} catch (Exception e) {
			log.error("Can't introspect class = {}", type, e);
			throw new RuntimeException(e);
		}
	}
}
