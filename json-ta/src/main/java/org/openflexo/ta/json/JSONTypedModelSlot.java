/**
 *
 * Copyright (c) 2018, Openflexo
 *
 * This file is part of OpenflexoTechnologyAdapter, a component of the software infrastructure
 * developed at Openflexo.
 *
 */

package org.openflexo.ta.json;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.annotations.DeclareActorReferences;
import org.openflexo.foundation.fml.annotations.DeclareEditionActions;
import org.openflexo.foundation.fml.annotations.DeclareFetchRequests;
import org.openflexo.foundation.fml.annotations.DeclareFlexoRoles;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.ta.json.fml.JSONNodeActorReference;
import org.openflexo.ta.json.fml.JSONIndividualRole;
import org.openflexo.ta.json.fml.JSONNodeRole;
import org.openflexo.ta.json.fml.editionaction.AddNode;
import org.openflexo.ta.json.fml.editionaction.DeleteNode;
import org.openflexo.ta.json.fml.editionaction.SelectNode;
import org.openflexo.ta.json.fml.editionaction.SelectJSONIndividual;
import org.openflexo.ta.json.fml.editionaction.SelectUniqueJSONIndividual;
import org.openflexo.ta.json.metamodel.JSONSchemaDocument;
import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.model.JSONNode;
import org.openflexo.ta.json.rm.JSONResource;

/** A JSON ModelSlot whose accessed JSON document is interpreted through a JSON Schema document. */
@DeclareFlexoRoles({ JSONNodeRole.class, JSONIndividualRole.class })
@DeclareEditionActions({ AddNode.class, DeleteNode.class })
@DeclareFetchRequests({ SelectNode.class, SelectJSONIndividual.class, SelectUniqueJSONIndividual.class })
@DeclareActorReferences({ JSONNodeActorReference.class })
@ModelEntity
@ImplementationClass(JSONTypedModelSlot.JSONTypedModelSlotImpl.class)
@XMLElement
@FML("JSONTypedModelSlot")
public interface JSONTypedModelSlot extends TypeAwareModelSlot<JSONDocument, JSONSchemaDocument, JSONResource> {

	@PropertyIdentifier(type = JSONSchemaDocument.class)
	String META_MODEL_KEY = "metaModel";

	@Override
	JSONTechnologyAdapter getModelSlotTechnologyAdapter();

	@Getter(value = META_MODEL_KEY, ignoreType = true)
	@FMLAttribute(value = META_MODEL_KEY, required = true)
	JSONSchemaDocument getMetaModel();

	@Setter(META_MODEL_KEY)
	void setMetaModel(JSONSchemaDocument metaModel);

	abstract class JSONTypedModelSlotImpl extends TypeAwareModelSlotImpl<JSONDocument, JSONSchemaDocument, JSONResource>
			implements JSONTypedModelSlot {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(JSONTypedModelSlot.class.getPackage().getName());

		@Override
		public Class<JSONTechnologyAdapter> getTechnologyAdapterClass() {
			return JSONTechnologyAdapter.class;
		}

		@Override
		public JSONTechnologyAdapter getModelSlotTechnologyAdapter() {
			return (JSONTechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}

		@Override
		public String getURIForObject(JSONDocument document, Object object) {
			return object instanceof JSONNode ? JSONURIProcessor.getURIForObject(document, (JSONNode) object) : null;
		}

		@Override
		public Object retrieveObjectWithURI(JSONDocument document, String objectURI) {
			return JSONURIProcessor.retrieveObjectWithURI(document, objectURI);
		}

		@Override
		public boolean isStrictMetaModelling() {
			return false;
		}

		@Override
		public JSONSchemaDocument getMetaModel() {
			if (getMetaModelResource() != null) {
				return getMetaModelResource().getMetaModelData();
			}
			return null;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void setMetaModel(JSONSchemaDocument metaModel) {
			setMetaModelResource(metaModel != null
					? (FlexoMetaModelResource<JSONDocument, JSONSchemaDocument, ?>) metaModel.getResource()
					: null);
		}

		@Override
		public void setMetaModelResource(FlexoMetaModelResource<JSONDocument, JSONSchemaDocument, ?> metaModelResource) {
			super.setMetaModelResource(metaModelResource);
			try {
				if (metaModelResource != null) {
					getPropertyChangeSupport().firePropertyChange(META_MODEL_KEY, null, metaModelResource.getResourceData());
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				e.printStackTrace();
			} catch (FlexoException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void handleRequiredImports(FMLCompilationUnit compilationUnit) {
			super.handleRequiredImports(compilationUnit);
			if (compilationUnit != null && getMetaModel() != null) {
				compilationUnit.ensureResourceImport(getMetaModel(), false);
			}
		}

		@Override
		public JSONResource createProjectSpecificEmptyModel(FlexoResourceCenter<?> rc, String filename, String relativePath, String modelUri,
				FlexoMetaModelResource<JSONDocument, JSONSchemaDocument, ?> metaModelResource) {
			return null;
		}

		@Override
		public JSONResource createSharedEmptyModel(FlexoResourceCenter<?> resourceCenter, String relativePath, String filename,
				String modelUri, FlexoMetaModelResource<JSONDocument, JSONSchemaDocument, ?> metaModelResource) {
			return null;
		}

		@Override
		public Type getType() {
			return JSONDocument.class;
		}

		@Override
		public <PR extends FlexoRole<?>> String defaultFlexoRoleName(Class<PR> roleClass) {
			if (JSONNodeRole.class.isAssignableFrom(roleClass)) {
				return "node";
			}
			if (JSONIndividualRole.class.isAssignableFrom(roleClass)) {
				return "individual";
			}
			return null;
		}

		@Override
		public String getTypeDescription() {
			return "typed json";
		}
	}
}
