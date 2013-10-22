package br.com.caelum.vraptor.http;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Class that represents a method or constructor parameter.
 * 
 * @author Otávio Scherer Garcia
 */
public final class Parameter implements AnnotatedElement {

	private final int index;
	private final String name;
	private final AccessibleObject delegate;
	private final Type parameterizedType;
	private final Class<?> parameterType;
	private final Annotation[] annotations;

	public Parameter(int index, String name, AccessibleObject delegate) {
		this.index = index;
		this.name = name;
		this.delegate = delegate;

		if (delegate instanceof Method) {
			Method method = (Method) delegate;
			parameterizedType = method.getGenericParameterTypes()[index];
			parameterType = method.getParameterTypes()[index];
			annotations = method.getParameterAnnotations()[index];
		} else if (delegate instanceof Constructor) {
			Constructor<?> constr = (Constructor<?>) delegate;
			parameterizedType = constr.getGenericParameterTypes()[index];
			parameterType = constr.getParameterTypes()[index];
			annotations = constr.getParameterAnnotations()[index];
		} else {
			throw new UnsupportedOperationException("We can only evaluate methods or constructors " + delegate.getClass());
		}
	}

	public String getName() {
		return name;
	}

	public Type getParameterizedType() {
		return parameterizedType;
	}

	public Class<?> getType() {
		return parameterType;
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> clazz) {
		return getAnnotation(clazz) != null;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> clazz) {
		for (Annotation a : getDeclaredAnnotations()) {
			if (a.annotationType().equals(clazz)) {
				return clazz.cast(a);
			}
		}
		return null;
	}

	@Override
	public Annotation[] getAnnotations() {
		return getDeclaredAnnotations();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return annotations;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Parameter) {
			Parameter other = (Parameter) obj;
			return other.index == index && other.delegate.equals(delegate);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(index, delegate);
	}
}