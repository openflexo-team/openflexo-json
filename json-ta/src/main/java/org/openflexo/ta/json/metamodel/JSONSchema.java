package org.openflexo.ta.json.metamodel;

import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;

/** Compatibility alias for the JSON schema document metamodel. */
@ModelEntity
@ImplementationClass(JSONSchema.JSONSchemaImpl.class)
public interface JSONSchema extends JSONSchemaDocument {

	abstract class JSONSchemaImpl extends JSONSchemaDocumentImpl implements JSONSchema {
	}
}
