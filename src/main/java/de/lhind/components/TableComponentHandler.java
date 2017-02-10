package de.lhind.components;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;

import com.sun.faces.facelets.tag.MethodRule;

public class TableComponentHandler extends ComponentHandler {
	
	public TableComponentHandler(ComponentConfig config) {
		super(config);
	}
	
	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset tempMetaRuleset = super.createMetaRuleset(type);
		MetaRule tempItemsMetaRule = new MethodRule(Table.PropertyKeys.items.name(), Object.class, new Class[] {});
		tempMetaRuleset.addRule(tempItemsMetaRule);
		return tempMetaRuleset;
	}

	@Override
	public void onComponentPopulated(FaceletContext ctx, UIComponent c, UIComponent parent) {
		super.onComponentPopulated(ctx, c, parent);
		if (c instanceof Table) {
			((Table) c).onComponentPopulated();
		}
	}

	@Override
	public void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
		super.onComponentCreated(ctx, c, parent);
		if (c instanceof Table) {
			((Table) c).onComponentCreated();
		}
	}

}
