package org.appdapter.gui.browse;

import java.awt.Component;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.appdapter.api.trigger.AnyOper.UIHidden;
import org.appdapter.core.convert.Converter;
import org.appdapter.core.convert.NoSuchConversionException;
import org.appdapter.core.convert.ProxyMethodClass;
import org.appdapter.core.convert.ReflectUtils;
import org.appdapter.core.log.Debuggable;
import org.appdapter.gui.trigger.TriggerMenuFactory;

public class PropertyDescriptorForField extends PropertyDescriptor implements Converter {

	static Class[] STARTER_INTERFACES = new Class[] { FakeMethods.class, ProxyMethodClass.class, PropertyChangeListener.class, PropertyEditor.class, InvocationHandler.class };
	static {
		HashSet<Class> allInterfaces = new HashSet<Class>();
		allInterfaces.addAll(Arrays.asList(STARTER_INTERFACES));
		allInterfaces.addAll(Arrays.asList(ObjectFieldEditor.class.getInterfaces()));
		allInterfaces.addAll(Arrays.asList(PropertyDescriptorForField.class.getInterfaces()));
		STARTER_INTERFACES = allInterfaces.toArray(STARTER_INTERFACES);
	}
	//ObjectChoiceComboPanel choice = null;
	public Object targetObject;

	public class ObjectFieldEditor extends PropertyEditorSupport implements PropertyChangeListener, PropertyEditor, InvocationHandler {

		/**
		 * 
		 */
		private final PropertyDescriptorForField propertyDescriptorForField;

		@Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			final Object origProx = proxy;
			if (proxyClass.isInstance(proxy)) {
				proxy = getSource();
			}
			String methodName = method.getName();
			boolean isStatic = this.propertyDescriptorForField.isStatic();
			if (method == this.propertyDescriptorForField.readMethodOr || (isStatic && methodName.equals("fakeReadMethod"))) {
				return this.propertyDescriptorForField.getFieldValue(proxy);
			}
			if (method == this.propertyDescriptorForField.writeMethodOr || (isStatic && methodName.equals("fakeWriteMethod"))) {
				this.propertyDescriptorForField.setFieldValue(proxy, args[0]);
				return null;
			}
			try {
				if (origProx != proxy && proxy != null) {
					return ReflectUtils.invokeA(proxy, method, args);
				}
				return ReflectUtils.invokeA(proxy, method, args);
			} catch (IllegalAccessException e) {
				throw Debuggable.reThrowable(e);
			} catch (InvocationTargetException e) {
				throw Debuggable.reThrowable(e);
			} catch (IllegalArgumentException e) {
			} catch (NoSuchMethodException e) {
			} catch (Throwable e) {
				throw Debuggable.reThrowable(e);
			}

			try {
				return ReflectUtils.invokeA(this, method, args);
			} catch (IllegalAccessException e) {
				throw Debuggable.reThrowable(e);
			} catch (InvocationTargetException e) {
				throw Debuggable.reThrowable(e);
			} catch (IllegalArgumentException e) {
			} catch (NoSuchMethodException e) {
			} catch (Throwable e) {
				throw Debuggable.reThrowable(e);
			}
			return ReflectUtils.invokeA(propertyDescriptorForField, method, args);
		}

		//JLabel label = null;
		//JPanel objectPanel = null;
		//NamedObjectCollection fromCollection = null;
		//final PropertyValueControl provalctrl;
		//Class type = null;
		//final Container validator;
		//SmallObjectView view = null;
		public ObjectFieldEditor(PropertyDescriptorForField propertyDescriptorForField, Object sourceObject) {
			this.propertyDescriptorForField = propertyDescriptorForField;
			setSource(sourceObject);
			this.setTargetObject(sourceObject);
		}

		public String[] getTags() {
			String[] tags = super.getTags();
			/*if (editor != null) {
				tags = editor.getTags();
			}*/
			return tags;
		}

		@Override public Component getCustomEditor() {
			return super.getCustomEditor();
		}

		public Object getObject() {
			return getValue();
		}

		public Object getTargetObject() {
			return getObjectFrom(targetObject);
		}

		public void setTargetObject(Object s) {
			targetObject = s;
		}

		@Override public void propertyChange(PropertyChangeEvent evt) {
			Object newValue = evt.getNewValue();
			// BeanWrapper object = (BeanWrapper) val;
			try {
				setValue(newValue);
			} catch (Exception err) {
				throw Debuggable.reThrowable(err);
				//PropertyValueControl.theLogger.error("An error occurred", err);
			}
		}

		@Override public void setValue(Object newValue) {
			this.propertyDescriptorForField.setFieldValue(this.getTargetObject(), newValue);
		}

		@Override public boolean supportsCustomEditor() {
			return true;
		}

		@Override public Object getValue() {
			return this.propertyDescriptorForField.getFieldValue(getTargetObject());
		}

		@Override public String toString() {
			return "Editor: " + this.getTargetObject() + ".<" + this.propertyDescriptorForField.f + ">=" + getValue();
		}

		/**
		 * Returns the bean that is used as the
		 * source of events. If the source has not
		 * been explicitly set then this instance of
		 * <code>PropertyEditorSupport</code> is returned.
		 *
		 * @return the source object or this instance
		 * @since 1.5
		 */
		@Override public Object getSource() {
			if (isValidSource(targetObject))
				return targetObject;
			Object src = super.getSource();
			if (isValidSource(src))
				return src;
			return this;
		}

		private boolean isValidSource(Object t) {
			return t != null && t != this && t != this.propertyDescriptorForField;
		}

		@Override public void setSource(Object source) {
			// TODO Auto-generated method stub
			super.setSource(source);
		}
	}

	public interface FakeMethods extends ProxyMethodClass {

		public Object fakeReadMethod();

		public void fakeWriteMethod(Object o);
	}

	public Object getFieldValue(Object obj) {
		try {
			return f.get(getObjectFrom(obj));
		} catch (Throwable t) {
			throw Debuggable.reThrowable(t);
		}
	}

	Object getObjectFrom(Object proxy) {
		Object obj = proxy;
		if (isStatic()) {
			return null;
		} else {
			obj = ReflectUtils.recast(obj, getDeclaringClass);
			if (getDeclaringClass.isInstance(obj))
				return obj;
		}
		return obj;
	}

	@Override public String toString() {
		return "PropertyDecForField: " + f;
	}

	@UIHidden static public Object fakeReadMethod() {
		throw new AbstractMethodError();
	}

	@UIHidden static public void fakeWriteMethod(Object o) {
		throw new AbstractMethodError();
	}

	public void setFieldValue(Object obj, Object value) {
		try {
			f.set(getObjectFrom(obj), value);
		} catch (Throwable t) {
			throw Debuggable.reThrowable(t);
		}
	}

	public boolean isStatic() {
		if (Modifier.isStatic(f.getModifiers()))
			return true;
		if (getDeclaringClass.isInterface())
			return true;
		return false;
	}

	final Field f;
	Method readMethodOr;
	Method writeMethodOr;
	//private ObjectFieldEditor objectFieldEditor;
	private ClassLoader classLoaderToRelease;
	private Class<? extends FakeMethods> proxyClass;
	final Class getDeclaringClass;
	private Class[] localInterfaces = TriggerMenuFactory.CLASS0;
	static Map<Class, PropertyDescriptorForField> proxyToField = new HashMap<Class, PropertyDescriptorForField>();
	static Map<Field, PropertyDescriptorForField> fieldToDesc = new HashMap<Field, PropertyDescriptorForField>();

	static {
		ReflectUtils.registerConverter(new ProxyConvertor());
	}

	private PropertyDescriptorForField(Field fld, ClassLoader useCL, Object source) throws IntrospectionException {
		this(fld, propertyNameForField(fld.getName()), useCL, null, (Method) null);
	}

	@Override public Class<?> getPropertyEditorClass() {
		return super.getPropertyEditorClass();
	}

	/**
	 * Sets the method that should be used to read the property value.
	 *
	 * @param readMethod The new read method.
	 */
	public synchronized void setReadMethod(Method readMethod) throws IntrospectionException {
		readMethodOr = readMethod;
	}

	public PropertyDescriptorForField(Field fld, String propertyNameForField, ClassLoader used, Method rm, Method wm) throws IntrospectionException {
		super(propertyNameForField, rm, wm);
		f = fld;
		getDeclaringClass = f.getDeclaringClass();
		f.setAccessible(true);
		setName(propertyNameForField);
		if (used == null) {
			ClassLoader parentCL = org.appdapter.core.convert.ProxyMethodClass.class.getClassLoader();
			this.classLoaderToRelease = new URLClassLoader(new URL[0], parentCL);
		} else {
			classLoaderToRelease = used;
		}
		localInterfaces = getInterfaces(f.getDeclaringClass());
		this.proxyClass = (Class<? extends FakeMethods>) Proxy.getProxyClass(classLoaderToRelease, localInterfaces);

		proxyToField.put(proxyClass, this);
		setPropertyEditorClass(proxyClass);
		if (isStatic()) {
			setFakeMethodsFromClass(proxyClass);
		} else {
			setFakeMethodsFromClass(getClass());
		}
	}

	public void setPropertyEditorClass(Class<?> propertyEditorClass) {
		super.setPropertyEditorClass(propertyEditorClass);
	}

	/*	private InvocationHandler getObjectProx() {
			if (this.objectFieldEditor == null) {
				this.objectFieldEditor = new ObjectFieldEditor(this, targetObject);
			}
			return this.objectFieldEditor;
		}
	*/
	public boolean setFakeMethodsFromClass(Class clz) {
		try {
			readMethodOr = clz.getMethod("fakeReadMethod");
			writeMethodOr = clz.getMethod("fakeWriteMethod", Object.class);
			return true;
		} catch (Throwable e) {
			Debuggable.printStackTrace(e);
			return false;
		}
	}

	public synchronized java.lang.Class<?> getPropertyType() {
		return f.getType();
	}

	@Override public String getDisplayName() {
		return super.getDisplayName();
	}

	@Override public String getShortDescription() {
		return "" + f;
	}

	public synchronized Method getReadMethod() {
		return readMethodOr;
	}

	public synchronized Method getWriteMethod() {
		return writeMethodOr;
	}

	@Override public String getName() {
		return f.getName();
	}

	/**
	 * Sets the method that should be used to write the property value.
	 *
	 * @param writeMethod The new write method.
	 */
	public synchronized void setWriteMethod(Method writeMethod) throws IntrospectionException {
		writeMethodOr = writeMethod;
	}

	public static String propertyNameForField(String name) {
		if (name.startsWith("m_")) {
			name = name.substring(2);
		} else if (name.startsWith("f_")) {
			name = name.substring(2);
		}
		return clipPropertyNameMethod(name, "my", "_");
	}

	public static String clipPropertyNameMethod(String propertyName, String... clipOff) {
		int clipCheck = 1;
		for (String clip : clipOff) {
			int clipLen = clip.length();
			if (propertyName.length() > clipLen + 1) {
				if (propertyName.startsWith(clip)) {
					clipCheck = 0;
					propertyName = propertyName.substring(clipLen);
				}
			}
		}
		if (clipCheck < propertyName.length()) {
			if (propertyName.charAt(clipCheck) == '_') {
				propertyName = propertyName.substring(clipCheck + 1);
			}
		}

		while (propertyName.startsWith("_")) {
			propertyName = propertyName.substring(1);
		}
		return propertyName;
	}

	public String getSyntheticName(Method method) {
		// TODO Auto-generated method stub
		if (readMethodOr == method)
			return "get" + Utility.properCase(getDisplayName());
		// TODO Auto-generated method stub
		if (writeMethodOr == method)
			return "set" + Utility.properCase(getDisplayName());
		return "method " + method + " is not for " + getShortDescription();
	}

	@Override public Integer declaresConverts(Object val, Class valClass, Class objNeedsToBe) {
		return objNeedsToBe == proxyClass ? WILL : WONT;
	}

	@Override public <T> T recast(Object obj, Class<T> objNeedsToBe) throws NoSuchConversionException {
		if (objNeedsToBe.isAssignableFrom(proxyClass)) {
			return (T) getObjectFrom(obj);
		}
		Object object = ReflectUtils.recast(obj, getDeclaringClass);
		T t = (T) newProxyInstance(obj);
		return t;

	}

	private <T> T newProxyInstance(Object obj) {
		ObjectFieldEditor ofe = new ObjectFieldEditor(this, obj);
		T t;
		try {
			Constructor[] cons = proxyClass.getDeclaredConstructors();
			if (cons.length > 0) {

				for (Constructor c : cons) {
					if (c.getParameterTypes().length != 1)
						continue;
					c.setAccessible(true);
					t = (T) c.newInstance(ofe);
					return t;
				}
			}
			return (T) Proxy.newProxyInstance(this.classLoaderToRelease, getInterfaces(obj.getClass()), ofe);
		} catch (InstantiationException e) {
			throw Debuggable.reThrowable(e);
		} catch (IllegalAccessException e) {
			throw Debuggable.reThrowable(e);
		} catch (IllegalArgumentException e) {
			throw Debuggable.reThrowable(e);
		} catch (InvocationTargetException e) {
			throw Debuggable.reThrowable(e);
		}
	}

	public Class<?>[] getInterfaces(Class clz) {
		Class[] local = this.localInterfaces;
		HashSet<Class> allInterfaces = new HashSet<Class>();
		allInterfaces.addAll(Arrays.asList(local));
		if (clz.isInterface()) {
			allInterfaces.add(clz);
		}
		allInterfaces.addAll(Arrays.asList(clz.getInterfaces()));
		allInterfaces.addAll(Arrays.asList(STARTER_INTERFACES));
		local = allInterfaces.toArray(TriggerMenuFactory.CLASS0);
		return local;
	}

	public static PropertyDescriptorForField findOrCreate(Field f2) throws IntrospectionException {
		synchronized (fieldToDesc) {
			PropertyDescriptorForField pdff = fieldToDesc.get(f2);
			if (pdff == null) {
				pdff = new PropertyDescriptorForField(f2, null, null);
				if (ReflectUtils.isStatic(f2)) {
					fieldToDesc.put(f2, pdff);
					return pdff;
				}

			}
			return pdff;
		}
	}

	public PropertyDescriptor makePD(Object object) throws IntrospectionException {
		if (ReflectUtils.isStatic(f))
			return this;
		return new PropertyDescriptorForField(f, classLoaderToRelease, object);
	}

	public Field getField() {
		return f;
	}
}