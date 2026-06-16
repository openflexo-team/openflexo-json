package org.openflexo.ta.json.metamodel;

import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;

/** A type declared explicitly or inline in a JSON Schema. */
@ModelEntity
@ImplementationClass(JSONSchemaType.JSONSchemaTypeImpl.class)
public interface JSONSchemaType extends Type {

	enum Kind {
		OBJECT, ARRAY, STRING, INTEGER, NUMBER, BOOLEAN, NULL, ANY
	}

	String NAME_KEY = "name";
	String URI_KEY = "uri";
	String JSON_POINTER_KEY = "jsonPointer";
	String KIND_KEY = "kind";
	String CONST_VALUE_KEY = "constValue";
	String PROPERTIES_KEY = "properties";
	String ITEM_TYPE_KEY = "itemType";
	String ALTERNATIVE_TYPES_KEY = "alternativeTypes";
	String REFERENCED_TYPE_KEY = "referencedType";
	String ADDITIONAL_PROPERTIES_KEY = "additionalPropertiesAllowed";

	@Getter(NAME_KEY)
	String getName();

	@Setter(NAME_KEY)
	void setName(String name);

	@Getter(URI_KEY)
	String getURI();

	@Setter(URI_KEY)
	void setURI(String uri);

	@Getter(JSON_POINTER_KEY)
	String getJsonPointer();

	@Setter(JSON_POINTER_KEY)
	void setJsonPointer(String pointer);

	@Getter(KIND_KEY)
	Kind getKind();

	@Setter(KIND_KEY)
	void setKind(Kind kind);

	@Getter(CONST_VALUE_KEY)
	String getConstValue();

	@Setter(CONST_VALUE_KEY)
	void setConstValue(String constValue);

	@Getter(value = PROPERTIES_KEY, cardinality = Cardinality.LIST, inverse = JSONSchemaProperty.OWNER_KEY)
	@Embedded
	List<JSONSchemaProperty> getProperties();

	@Adder(PROPERTIES_KEY)
	void addToProperties(JSONSchemaProperty property);

	@Remover(PROPERTIES_KEY)
	void removeFromProperties(JSONSchemaProperty property);

	@Getter(ITEM_TYPE_KEY)
	JSONSchemaType getItemType();

	@Setter(ITEM_TYPE_KEY)
	void setItemType(JSONSchemaType itemType);

	@Getter(value = ALTERNATIVE_TYPES_KEY, cardinality = Cardinality.LIST)
	@Embedded
	List<JSONSchemaType> getAlternativeTypes();

	@Adder(ALTERNATIVE_TYPES_KEY)
	void addToAlternativeTypes(JSONSchemaType alternativeType);

	@Remover(ALTERNATIVE_TYPES_KEY)
	void removeFromAlternativeTypes(JSONSchemaType alternativeType);

	@Getter(REFERENCED_TYPE_KEY)
	JSONSchemaType getReferencedType();

	@Setter(REFERENCED_TYPE_KEY)
	void setReferencedType(JSONSchemaType referencedType);

	@Getter(value = ADDITIONAL_PROPERTIES_KEY, defaultValue = "true")
	boolean getAdditionalPropertiesAllowed();

	@Setter(ADDITIONAL_PROPERTIES_KEY)
	void setAdditionalPropertiesAllowed(boolean allowed);

	JSONSchemaProperty getProperty(String name);

	JSONSchemaType getEffectiveType();

	abstract class JSONSchemaTypeImpl implements JSONSchemaType {

		@Override
		public JSONSchemaProperty getProperty(String name) {
			if (name != null) {
				for (JSONSchemaProperty property : getProperties()) {
					if (name.equals(property.getName())) {
						return property;
					}
				}
			}
			return null;
		}

		@Override
		public JSONSchemaType getEffectiveType() {
			return getReferencedType() != null ? getReferencedType().getEffectiveType() : this;
		}

		@Override
		public String getTypeName() {
			return getURI() != null ? getURI() : getName();
		}
	}
}
