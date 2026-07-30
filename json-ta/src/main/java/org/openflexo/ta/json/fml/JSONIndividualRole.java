package org.openflexo.ta.json.fml;

import java.lang.reflect.Type;

import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.type.ProxyType;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.TechnologySpecificType;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstanceModelFactory;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.ta.json.JSONIndividualType;
import org.openflexo.ta.json.JSONTechnologyAdapter;
import org.openflexo.ta.json.JSONTypedModelSlot;
import org.openflexo.ta.json.metamodel.JSONSchemaType;
import org.openflexo.ta.json.model.JSONNode;

/** A typed role referencing a JSON node conforming to a JSON Schema type. */
@ModelEntity
@XMLElement
@ImplementationClass(JSONIndividualRole.JSONIndividualRoleImpl.class)
@FML("JSONIndividualRole")
public interface JSONIndividualRole extends FlexoRole<JSONNode> {

	@PropertyIdentifier(type = JSONSchemaType.class)
	String SCHEMA_TYPE_KEY = "schemaType";

	@Getter(value = SCHEMA_TYPE_KEY, ignoreType = true)
	@FMLAttribute(value = SCHEMA_TYPE_KEY, required = false, description = "<html>JSON Schema type</html>")
	JSONSchemaType getSchemaType();

	@Setter(SCHEMA_TYPE_KEY)
	void setSchemaType(JSONSchemaType type);

	@Override
	JSONTypedModelSlot getModelSlot();

	JSONTechnologyAdapter getJSONTechnologyAdapter();

	abstract class JSONIndividualRoleImpl extends FlexoRoleImpl<JSONNode> implements JSONIndividualRole {

		private Type lastKnownType = null;

		@Override
		public JSONTechnologyAdapter getJSONTechnologyAdapter() {
			return getModelSlot() != null ? getModelSlot().getModelSlotTechnologyAdapter() : null;
		}

		@Override
		public JSONTypedModelSlot getModelSlot() {
			return (JSONTypedModelSlot) super.getModelSlot();
		}

		@Override
		public String getTypeDescription() {
			return JSONNode.class.getSimpleName();
		}

		@Override
		public RoleCloningStrategy defaultCloningStrategy() {
			return RoleCloningStrategy.Reference;
		}

		@Override
		public boolean defaultBehaviourIsToBeDeleted() {
			return false;
		}

		@Override
		public ActorReference<JSONNode> makeActorReference(JSONNode object, FlexoConceptInstance fci) {
			AbstractVirtualModelInstanceModelFactory factory = fci.getFactory();
			JSONNodeActorReference returned = factory.newInstance(JSONNodeActorReference.class);
			returned.setFlexoRole(this);
			returned.setFlexoConceptInstance(fci);
			returned.setModellingElement(object);
			return returned;
		}

		@Override
		public Class<? extends TechnologyAdapter> getRoleTechnologyAdapterClass() {
			return JSONTechnologyAdapter.class;
		}

		@Override
		public void setSchemaType(JSONSchemaType type) {
			JSONSchemaType oldType = getSchemaType();
			performSuperSetter(SCHEMA_TYPE_KEY, type);
			if (requireChange(oldType, type)) {
				setModified(true);
				getPropertyChangeSupport().firePropertyChange(SCHEMA_TYPE_KEY, oldType, type);
			}
		}

		@Override
		public Type getType() {
			Type returned = getSchemaType() != null ? JSONIndividualType.getJSONIndividualOfType(getSchemaType()) : JSONNode.class;
			if (lastKnownType == null || (returned != null && !lastKnownType.equals(returned))) {
				Type oldType = lastKnownType;
				lastKnownType = returned;
				getPropertyChangeSupport().firePropertyChange(BindingVariable.TYPE_PROPERTY, oldType, returned);
			}
			if (getDeclaringCompilationUnit() != null && returned instanceof TechnologySpecificType) {
				return getDeclaringCompilationUnit().normalizeType((TechnologySpecificType<?>) returned);
			}
			return returned;
		}

		@Override
		public void setType(Type type) {
			performSuperSetter(TYPE_KEY, type);
			Type effectiveType = ProxyType.getEffectiveType(type);
			if (effectiveType instanceof JSONIndividualType) {
				setSchemaType(((JSONIndividualType) effectiveType).getSchemaType());
			}
		}
	}
}
