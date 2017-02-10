package de.lhind.components;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;

import com.sun.faces.component.visit.FullVisitContext;

/**
 * Utility class for frequently used component-related tasks.
 * 
 * @author Jan-Christopher Pien
 */
public class ComponentUtil {

	private ComponentUtil() {
	}

	/**
	 * Creates a new component in the JSF component tree.
	 * 
	 * @param aFacesContext the FacesContext
	 * @param aComponentType the component type
	 * @param aComponentClazz the desired component class
	 * @return the new component
	 * @throws ClassCastException if the component can not be cast to the desired component
	 *             class
	 */
	public static <T> T createJsfComponent(FacesContext aFacesContext, String aComponentType, Class<T> aComponentClazz)
			throws ClassCastException {
		Object tempComponent = aFacesContext.getApplication().createComponent(aComponentType);
		if (aComponentClazz.isAssignableFrom(tempComponent.getClass())) {
			return aComponentClazz.cast(tempComponent);
		} else {
			throw new ClassCastException("Component " + aComponentType
					+ " could not be cast to desired component class " + aComponentClazz.getSimpleName());
		}
	}

	/**
	 * Creates a new component in the JSF component tree. Use this method if the component
	 * provides a public static field <b><i>COMPONENT_TYPE</i></b>, which can be used to
	 * determine the desired component type.
	 * 
	 * @param aFacesContext the FacesContext
	 * @param aComponentClazz the desired component class. This class will be used to determine
	 *            the component type of the desired component. If there is no public static
	 *            field <b><i>COMPONENT_TYPE</i></b> an IllegalStateException will be thrown.
	 * @return the new component
	 * @throws IllegalStateException if the component type can not be determined from the
	 *             desired component class
	 * @throws ClassCastException if the component can not be cast to the desired component
	 *             class
	 */
	public static <T> T createJsfComponent(FacesContext aFacesContext, Class<T> aComponentClazz)
			throws IllegalStateException, ClassCastException {
		try {
			Field tempComponentTypeField = aComponentClazz.getField("COMPONENT_TYPE");
			String tempComponentType = (String) tempComponentTypeField.get(null);
			return ComponentUtil.createJsfComponent(aFacesContext, tempComponentType, aComponentClazz);
		} catch (NoSuchFieldException | IllegalAccessException | SecurityException e) {
			throw new IllegalStateException("The component type of the desired component class "
					+ aComponentClazz.getSimpleName() + "could not be determined.", e);
		}
	}

	/**
	 * Moves all the children components of one component to another
	 * 
	 * @param aSourceComponent The component from which the children should be moved
	 * @param aDestinationComponent The component to which the children should be moved
	 */

	public static void addChildrenToComponent(UIComponent aSourceComponent, UIComponent aDestinationComponent) {
		List<UIComponent> tempCopy = new ArrayList<>(aSourceComponent.getChildren());
		for (Iterator<UIComponent> tempIterator = tempCopy.iterator(); tempIterator.hasNext();) {
			UIComponent tempComponent = tempIterator.next();
			aDestinationComponent.getChildren().add(tempComponent);
			tempIterator.remove();
		}
	}

	/**
	 * Creates a MethodExpression with the given return type and given parameter types.
	 * 
	 * @param anExpression the expression to create
	 * @param aReturnType the return type of the expression
	 * @param someParameterTypes the parameter types
	 * @return
	 */
	public static MethodExpression createMethodExpression(String anExpression, Class<?> aReturnType,
			Class<?>... someParameterTypes) {
		FacesContext tempFacesContext = FacesContext.getCurrentInstance();
		return tempFacesContext.getApplication().getExpressionFactory()
				.createMethodExpression(tempFacesContext.getELContext(), anExpression, aReturnType, someParameterTypes);
	}

	/**
	 * Creates a ValueExpression with the given return type.
	 * 
	 * @param anExpression
	 * @param aReturnType
	 * @return
	 */
	public static ValueExpression createValueExpression(String anExpression, Class<?> aReturnType) {
		FacesContext tempFacesContext = FacesContext.getCurrentInstance();
		return tempFacesContext.getApplication().getExpressionFactory()
				.createValueExpression(tempFacesContext.getELContext(), anExpression, aReturnType);
	}

	/**
	 * Finds the first component with a given id in the component tree.
	 * 
	 * @param anId
	 * @return
	 */
	public static UIComponent findComponent(final String anId) {

		FacesContext tempContext = FacesContext.getCurrentInstance();
		UIViewRoot tempRoot = tempContext.getViewRoot();
		final UIComponent[] tempFound = new UIComponent[1];

		tempRoot.visitTree(new FullVisitContext(tempContext), (context, component) -> {
			if ((component != null) && (component.getId() != null) && component.getId().equals(anId)) {
				tempFound[0] = component;
				return VisitResult.COMPLETE;
			}
			return VisitResult.ACCEPT;
		});

		return tempFound[0];
	}

    /**
     * Parses an expression into a {@link ValueExpression} for later
     * evaluation using {@link ExpressionFactory#createValueExpression}.
     *
     * @param context The EL context used to parse the expression.
     * @param expression The expression to parse
     * @param expectedType The type the result of the expression
     *     will be coerced to after evaluation.
     * @return The parsed expression
     * @throws NullPointerException Thrown if expectedType is null.
     * @throws ELException Thrown if there are syntactical errors in the
     *     provided expression.
     */     
	public static ValueExpression createValueExpression(String expression, Class<?> expectedType, FacesContext context) {
		return context.getApplication().getExpressionFactory().createValueExpression(context.getELContext(), expression, expectedType);
	}

}
