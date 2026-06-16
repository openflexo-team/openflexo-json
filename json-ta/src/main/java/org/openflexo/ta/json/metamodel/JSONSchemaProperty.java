package org.openflexo.ta.json.metamodel;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;

/** A named property of an object type in a JSON Schema. */
@ModelEntity
@ImplementationClass(JSONSchemaProperty.JSONSchemaPropertyImpl.class)
public interface JSONSchemaProperty {

	String NAME_KEY = "name";
	String REQUIRED_KEY = "required";
	String TYPE_KEY = "type";
	String OWNER_KEY = "owner";

	@Getter(NAME_KEY)
	String getName();

	@Setter(NAME_KEY)
	void setName(String name);

	@Getter(value = REQUIRED_KEY, defaultValue = "false")
	boolean isRequired();

	@Setter(REQUIRED_KEY)
	void setRequired(boolean required);

	@Getter(TYPE_KEY)
	JSONSchemaType getType();

	@Setter(TYPE_KEY)
	void setType(JSONSchemaType type);

	@Getter(value = OWNER_KEY, inverse = JSONSchemaType.PROPERTIES_KEY)
	JSONSchemaType getOwner();

	@Setter(OWNER_KEY)
	void setOwner(JSONSchemaType owner);

	abstract class JSONSchemaPropertyImpl implements JSONSchemaProperty {
	}
}
