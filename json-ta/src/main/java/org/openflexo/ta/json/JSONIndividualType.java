package org.openflexo.ta.json;

import java.lang.reflect.Type;
import java.util.Objects;

import org.openflexo.connie.type.ProxyType;
import org.openflexo.foundation.fml.TechnologyAdapterTypeFactory;
import org.openflexo.foundation.fml.TechnologySpecificType;
import org.openflexo.foundation.technologyadapter.SpecificTypeInfo;
import org.openflexo.foundation.utils.FlexoObjectReference;
import org.openflexo.foundation.utils.FlexoObjectReference.ReferenceOwner;
import org.openflexo.ta.json.metamodel.JSONSchemaProperty;
import org.openflexo.ta.json.metamodel.JSONSchemaType;
import org.openflexo.ta.json.metamodel.JSONSchemaType.Kind;
import org.openflexo.ta.json.model.JSONNode;
import org.openflexo.toolbox.PropertyChangedSupportDefaultImplementation;
import org.openflexo.toolbox.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

/** A JSON node typed by a JSON Schema type. */
public class JSONIndividualType extends PropertyChangedSupportDefaultImplementation implements TechnologySpecificType<JSONTechnologyAdapter> {

	public static final String SCHEMA_TYPE = "schemaType";

	private final JSONSchemaType schemaType;
	private SpecificTypeInfo<JSONTechnologyAdapter> typeInfo;

	public JSONIndividualType(JSONSchemaType schemaType) {
		this.schemaType = schemaType;
	}

	public JSONSchemaType getSchemaType() {
		return schemaType;
	}

	public static JSONIndividualType getJSONIndividualOfType(JSONSchemaType schemaType) {
		if (schemaType == null) {
			return null;
		}
		JSONTechnologyAdapter technologyAdapter = findTechnologyAdapter(schemaType);
		if (technologyAdapter != null) {
			return technologyAdapter.getTechnologyContextManager().getIndividualOfType(schemaType);
		}
		return new JSONIndividualType(schemaType);
	}

	public static JSONIndividualType UNDEFINED_JSON_INDIVIDUAL_TYPE = new JSONIndividualType(null);

	@Override
	public String getSerializationRepresentation() {
		return schemaType != null ? schemaType.getURI() : "";
	}

	@Override
	public JSONTechnologyAdapter getSpecificTechnologyAdapter() {
		return findTechnologyAdapter(schemaType);
	}

	@Override
	public boolean isResolved() {
		return schemaType != null;
	}

	@Override
	public void resolve() {
	}

	@Override
	public void registerSpecificTypeInfo(SpecificTypeInfo<JSONTechnologyAdapter> typeInfo) {
		this.typeInfo = typeInfo;
	}

	public SpecificTypeInfo<JSONTechnologyAdapter> getSpecificTypeInfo() {
		return typeInfo;
	}

	@Override
	public Class<?> getBaseClass() {
		return JSONNode.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		if (aType instanceof ProxyType) {
			return isTypeAssignableFrom(((ProxyType) aType).getReferencedType(), permissive);
		}
		if (aType instanceof JSONIndividualType) {
			JSONIndividualType other = (JSONIndividualType) aType;
			if (getSchemaType() == null) {
				return other.getSchemaType() == null;
			}
			return getSchemaType().equals(other.getSchemaType());
		}
		return aType == JSONNode.class || aType instanceof JSONNode;
	}

	@Override
	public boolean isOfType(Object object, boolean permissive) {
		if (!(object instanceof JSONNode)) {
			return false;
		}
		if (getSchemaType() == null) {
			return true;
		}
		return matches((JSONNode) object, getSchemaType().getEffectiveType(), permissive);
	}

	private boolean matches(JSONNode node, JSONSchemaType expectedType, boolean permissive) {
		if (node == null || node.getNode() == null || expectedType == null) {
			return permissive;
		}
		JsonNode jsonNode = node.getNode();
		if (expectedType.getConstValue() != null && !expectedType.getConstValue().equals(jsonNode.asText())) {
			return false;
		}
		Kind kind = expectedType.getKind();
		if (kind == Kind.ANY) {
			return true;
		}
		if (kind == Kind.OBJECT) {
			return jsonNode.isObject() && matchesObject(node, expectedType, permissive);
		}
		if (kind == Kind.ARRAY) {
			return jsonNode.isArray();
		}
		if (kind == Kind.STRING) {
			return jsonNode.isTextual();
		}
		if (kind == Kind.INTEGER) {
			return jsonNode.isInt() || jsonNode.isLong() || jsonNode.isIntegralNumber();
		}
		if (kind == Kind.NUMBER) {
			return jsonNode.isNumber();
		}
		if (kind == Kind.BOOLEAN) {
			return jsonNode.isBoolean();
		}
		if (kind == Kind.NULL) {
			return jsonNode.isNull();
		}
		return permissive;
	}

	private boolean matchesObject(JSONNode node, JSONSchemaType expectedType, boolean permissive) {
		for (JSONSchemaProperty property : expectedType.getProperties()) {
			JSONNode child = node.getNodeWithKey(property.getName());
			if (child == null) {
				if (property.isRequired()) {
					return false;
				}
			}
			else if (!matches(child, property.getType().getEffectiveType(), permissive)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String simpleRepresentation() {
		if (getSpecificTypeInfo() != null && StringUtils.isNotEmpty(getSpecificTypeInfo().getSerializationForm())) {
			return getSpecificTypeInfo().getSerializationForm();
		}
		return getSchemaType() != null ? "JSONIndividualType(schemaType=" + getSchemaType().getName() + ")" : "JSONIndividual";
	}

	@Override
	public String fullQualifiedRepresentation() {
		if (getSpecificTypeInfo() != null && StringUtils.isNotEmpty(getSpecificTypeInfo().getSerializationForm())) {
			return getSpecificTypeInfo().getSerializationForm();
		}
		return getClass().getName() + "(" + SCHEMA_TYPE + "=" + (getSchemaType() != null ? getSchemaType().getName() : "") + ")";
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}

	@Override
	public int hashCode() {
		return Objects.hash(schemaType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		JSONIndividualType other = (JSONIndividualType) obj;
		return Objects.equals(schemaType, other.schemaType);
	}

	private static JSONTechnologyAdapter findTechnologyAdapter(JSONSchemaType schemaType) {
		// JSONSchemaType resources are introduced in the next phase. Until then,
		// schema-bound individual types still work without a resolved adapter.
		return null;
	}

	public static class JSONIndividualTypeFactory extends TechnologyAdapterTypeFactory<JSONIndividualType, JSONTechnologyAdapter>
			implements ReferenceOwner {

		public JSONIndividualTypeFactory(JSONTechnologyAdapter technologyAdapter) {
			super(technologyAdapter);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Class<JSONIndividualType> getCustomType() {
			return JSONIndividualType.class;
		}

		public JSONIndividualType getIndividualOfType(JSONSchemaType schemaType) {
			return getTechnologyAdapter().getTechnologyContextManager().getIndividualOfType(schemaType);
		}

		@Override
		public JSONIndividualType makeCustomType(String configuration) {
			// JSONSchemaType will become resolvable from a serialized reference when
			// JSONSchemaResource/JSONMetaModelSlot is introduced.
			return null;
		}

		@Override
		public void configureFactory(JSONIndividualType type) {
		}

		@Override
		public void notifyObjectLoaded(FlexoObjectReference<?> reference) {
		}

		@Override
		public void objectCantBeFound(FlexoObjectReference<?> reference) {
		}

		@Override
		public void objectDeleted(FlexoObjectReference<?> reference) {
		}

		@Override
		public void objectSerializationIdChanged(FlexoObjectReference<?> reference) {
		}
	}
}
