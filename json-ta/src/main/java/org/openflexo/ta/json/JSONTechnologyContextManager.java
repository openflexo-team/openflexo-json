package org.openflexo.ta.json;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.ta.json.metamodel.JSONSchemaType;

public class JSONTechnologyContextManager extends TechnologyContextManager<JSONTechnologyAdapter> {

	private static final Logger logger = Logger.getLogger(JSONTechnologyContextManager.class.getPackage().getName());

	private final Hashtable<JSONSchemaType, JSONIndividualType> individualsOfType;

	public JSONTechnologyContextManager(JSONTechnologyAdapter adapter, FlexoResourceCenterService resourceCenterService) {
		super(adapter, resourceCenterService);
		individualsOfType = new Hashtable<>();
	}

	public JSONIndividualType getIndividualOfType(JSONSchemaType schemaType) {
		if (schemaType == null) {
			return null;
		}
		if (individualsOfType.get(schemaType) != null) {
			return individualsOfType.get(schemaType);
		}
		try {
			JSONIndividualType returned = new JSONIndividualType(schemaType);
			individualsOfType.put(schemaType, returned);
			return returned;
		} catch (ClassCastException e) {
			logger.warning(e.getMessage());
			return null;
		}
	}
}
