package de.lhind.components;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;

/**
 * Uses a {@link DataTable} as child components to display the data.
 * 
 */
@FacesComponent(Table.COMPONENT_TYPE)
public class Table extends UIPanel implements NamingContainer, SystemEventListener {

	public static final String COMPONENT_TYPE = "de.lhind.component.Table";

	public static final String COMPONENT_FAMILY = "de.lhind.component";

	private static final String DEFAULT_ROW_VAR = "rowVar";

	public static final String TABLE_PANEL_STYLE_CLASS = "panel-info ";

	public static final String TABLE_STYLE_CLASS = "editor-tabelle editor-tabelle-breit ";

	public static final String TABLE_SELECTABLE_STYLE_CLASS = "tabelle-selektierbar ";

	public static final String FACET_HEADER_ADDITIONAL = "headerAdditional";

	public static final String FACET_HEADER = "header";

	public enum PropertyKeys {

		items, displayColumns, rowVar;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {
		}

		@Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
		}
	}

	protected enum FacetKeys {

		header, headerAdditional;

		String toString;

		FacetKeys(String toString) {
			this.toString = toString;
		}

		FacetKeys() {
		}

		@Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
		}

	}

	@Override
	public String getFamily() {
		return Table.COMPONENT_FAMILY;
	}

	/* Properties */

	public MethodExpression getItems() {
		return (MethodExpression) getStateHelper().eval(PropertyKeys.items, null);
	}

	public void setItems(MethodExpression value) {
		getStateHelper().put(PropertyKeys.items, value);
	}

	public String getDisplayColumns() {
		return (String) getStateHelper().eval(PropertyKeys.displayColumns, null);
	}

	public void setDisplayColumns(String value) {
		getStateHelper().put(PropertyKeys.displayColumns, value);
		setColumnsCreated(false);
	}

	public String getRowVar() {
		return (String) getStateHelper().eval(PropertyKeys.rowVar, Table.DEFAULT_ROW_VAR);
	}

	public void setRowVar(String value) {
		getStateHelper().put(PropertyKeys.rowVar, value);
	}

	@Override
	public void setValueExpression(String name, ValueExpression binding) {
		super.setValueExpression(name, binding);
		switch (name) {
		case "domainClass":
		case "displayColumns":
			setColumnsCreated(false);
			break;
		}
	}

	/* Child components */

	private static final String CHILD_DATA_TABLE = "wrappedDataTable";

	private DataTable getChildDataTable() {
		DataTable tempDataTable = (DataTable) getStateHelper().eval(CHILD_DATA_TABLE);
		if (tempDataTable == null) {
			tempDataTable = ComponentUtil.createJsfComponent(getFacesContext(), DataTable.class);
			setChildDataTable(tempDataTable);
		}
		return tempDataTable;
	}

	private void setChildDataTable(DataTable value) {
		getStateHelper().put(CHILD_DATA_TABLE, value);
	}

	/* Private states */

	private static final String PRIVATE_STATE_POPULATED = "privateStatePopulated";

	private boolean isPopulated() {
		return (boolean) getStateHelper().eval(PRIVATE_STATE_POPULATED, false);
	}

	private void setPopulated(boolean value) {
		getStateHelper().put(PRIVATE_STATE_POPULATED, value);
	}

	private static final String PRIVATE_STATE_COLUMNS_CREATED = "privateStateColumnsCreated";

	private boolean isColumnsCreated() {
		return (boolean) getStateHelper().eval(PRIVATE_STATE_COLUMNS_CREATED, false);
	}

	private void setColumnsCreated(boolean value) {
		getStateHelper().put(PRIVATE_STATE_COLUMNS_CREATED, value);
	}
	
	/* Instance variables */

	protected Map<String, UIComponent> childColumnMap;
	
	/* Constructor */
	
	public Table() {
		getFacesContext().getViewRoot().subscribeToViewEvent(PostAddToViewEvent.class, this);
	}

	/* Methods */

	/**
	 * Is called by the component handler when the component is created.
	 */
	public void onComponentCreated() {
		getChildren().add(getChildDataTable());
	}

	/**
	 * Is called by the component handler when the component is populated.
	 */
	public void onComponentPopulated() {
		if (isPopulated()) {
			// Has to be recreated every time
			return;
		}
		populateTableControl();
		
		setPopulated(true);
	}

	/**
	 * Populates the child {@link DataTable} with the necessary components and properties.
	 */
	protected void populateTableControl() {
		DataTable tempDataTable = getChildDataTable();
		tempDataTable.setPaginator(true);
		tempDataTable.setPaginatorAlwaysVisible(false);
		tempDataTable.setPaginatorPosition("top");
	}

	/**
	 * Sets the varying table properties when rendering
	 */
	protected void setVaryingTableProperties() {
		DataTable tempDataTable = getChildDataTable();
		tempDataTable.setLazy(false);
		tempDataTable.setRows(5);
		tempDataTable.setVar(getRowVar());
		tempDataTable.setSelectionMode(null);
		tempDataTable.setStyleClass(Table.TABLE_STYLE_CLASS);
	}

	/**
	 * Returns the object that is currently selected or the object in the row that has
	 * triggered the current action.
	 * 
	 * @return
	 */
	public Object getObjectOfCurrentRow() {
		try {
			return getChildDataTable().getRowData();
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		super.encodeBegin(context);

		ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", null);
		writer.writeAttribute("id", getClientId(), null);
		getChildDataTable().setId(getId() + "_table");

		setVaryingTableProperties();

		fillTable();
		
		getChildDataTable().setValue(getItemsResult());
		getChildDataTable().encodeBegin(context);
	}

	private Object getItemsResult() {
		Object itemsResult = null;
		if (getItems() != null) {
			itemsResult = getItems().invoke(getFacesContext().getELContext(), null);
		}
		return itemsResult;
	}
	/**
	 * Determines which columns should be displayed and fills the table with the necessary
	 * components and items.
	 */
	protected void fillTable() {
		String[] tempFieldNames = null;
		if (getDisplayColumns() != null) {
			tempFieldNames = getDisplayColumns().split(",");
		}
		getChildDataTable().getFacets().clear();
		// Create child columns and button panels
		if (!isColumnsCreated()) {
			createColumnsFromChildren();
			// Create columns from fields
			createColumnsFromFields(tempFieldNames);
			// Reorder columns
			reorderChildrenColumns(tempFieldNames);
			setColumnsCreated(true);
		}
	}

	/**
	 * Creates a map of children that are nested inside this component.
	 */
	protected void createChildColumnMap() {
		if (childColumnMap == null) {
			childColumnMap = new HashMap<>();
		}

		for (UIComponent tempChild : getChildren()) {
			if (tempChild instanceof Column) {
				childColumnMap.put(((Column) tempChild).getId(), tempChild);
			}
		}
	}

	/**
	 * Moves all remaining children from this component to the nested table.
	 */
	protected void createColumnsFromChildren() {
		DataTable tempDataTable = getChildDataTable();
		// Create children map from children
		createChildColumnMap();
		for (UIComponent tempChild : childColumnMap.values()) {
			if (this.equals(tempChild.getParent())) {
				tempDataTable.getChildren().add(tempChild);
			}
		}
	}
	
	/**
	 * Reorders the table's children so that they are in accordance with the displayColumns attribute.
	 * @param someFields
	 * 			some fields to render
	 */
	private void reorderChildrenColumns(String[] someFields) {
		DataTable tempDataTable = getChildDataTable();
		Map<String, Integer> positionMap = new HashMap<>();
		for (int i = 0; i < someFields.length; i++) {
			positionMap.put(someFields[i], i);
		}
		tempDataTable.getChildren().sort((component1, component2) -> {
			String componentName1 = component1.getId();
			String componentName2 = component2.getId();
			Integer position1 = positionMap.get(componentName1);
			Integer position2 = positionMap.get(componentName2);
			if (position1 != null && position2 != null) {
				return position1.compareTo(position2);
			}
			return 0;
		});
	}

	/**
	 * Creates the necessary {@link Column}s from the given domain class and the given fields.
	 * 
	 * @param aDomainClass the domain class
	 * @param someFields the fields of the domain class to be displayed
	 */
	protected void createColumnsFromFields(String[] someFields) {
		if (someFields != null) {
			for (String tempFieldName : someFields) {
				UIColumn tempColumn = createColumnForField(tempFieldName);
				getChildDataTable().getChildren().add(tempColumn);
			}
		}
	}

	/**
	 * Creates a column for one field.
	 * 
	 * @param aFieldName the name of the field
	 * @return the column to be nested inside the data table
	 */
	protected UIColumn createColumnForField(String aFieldName) {
		UIComponent tempChildComponent = childColumnMap.get(aFieldName);
		if (tempChildComponent instanceof Column) {
			// Custom PrimeFaces column, use this instead of anything else
			childColumnMap.remove(aFieldName);
			return (Column) tempChildComponent;
		}
		// Create PrimeFaces child column
		Column tempColumn = ComponentUtil.createJsfComponent(getFacesContext(), Column.class);
		tempColumn.setId(aFieldName);

		createColumnHeader(aFieldName, tempColumn);

		// Value expression for getting the value of the model field
		ValueExpression tempValueExpression =
				ComponentUtil.createValueExpression(createValueExpressionForFieldName(aFieldName), Object.class);

		HtmlOutputLabel tempValueLabel =
				ComponentUtil.createJsfComponent(getFacesContext(), HtmlOutputLabel.class);
		tempValueLabel.setValueExpression("value", tempValueExpression);
		tempColumn.getChildren().add(tempValueLabel);

		childColumnMap.remove(aFieldName);

		return tempColumn;
	}


	/**
	 * Creates the header for a column.
	 * 
	 * @param aFieldName the field name
	 * @param aResourceBundle the resource bundle
	 * @param aColumn the column that is used
	 * @param aPatrisColumn the {@link de.lhind.pans.gui.components.patris.Column} that
	 *            provides the configuration for this column
	 * @param isSortable determines whether the column should be sortable
	 */
	protected void createColumnHeader(String aFieldName, Column aColumn) {
		String tempHeaderName = aFieldName;
		HtmlOutputText tempColumnHeader = ComponentUtil.createJsfComponent(getFacesContext(), HtmlOutputText.class);
		tempColumnHeader.setValue(tempHeaderName);
		aColumn.setHeader(tempColumnHeader);
	}

	private String createValueExpressionForFieldName(String aFieldName) {
		return "#{" + getRowVar() + "." + aFieldName + "}";
	}
	

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		getChildDataTable().encodeChildren(context);
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		getChildDataTable().encodeEnd(context);
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");

		super.encodeEnd(context);
	}

	@Override
	public void decode(FacesContext context) {
		getChildDataTable().decode(context);
		super.decode(context);
	}

	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {
		if (event instanceof PostAddToViewEvent) {
			createColumnsFromChildren();
		}
	}

	@Override
	public boolean isListenerForSource(Object source) {
		return source.equals(this) || source instanceof UIViewRoot;
	}
}
