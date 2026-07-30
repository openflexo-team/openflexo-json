package org.openflexo.ta.json.metamodel;

import java.util.List;

import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.ta.json.JSONTechnologyAdapter;
import org.openflexo.ta.json.rm.JSONSchemaResource;

/** OpenFlexo representation of a JSON Schema document used as a JSON metamodel. */
@ModelEntity(isAbstract = true)
public interface JSONSchemaDocument extends FlexoMetaModel<JSONSchemaDocument>, TechnologyObject<JSONTechnologyAdapter> {

	String URI_KEY = "uri";
	String TITLE_KEY = "title";
	String ROOT_TYPE_KEY = "rootType";
	String TYPES_KEY = "types";

	@Getter(URI_KEY)
	@Override
	String getURI();

	@Setter(URI_KEY)
	void setURI(String uri);

	@Getter(TITLE_KEY)
	String getTitle();

	@Setter(TITLE_KEY)
	void setTitle(String title);

	@Getter(ROOT_TYPE_KEY)
	JSONSchemaType getRootType();

	@Setter(ROOT_TYPE_KEY)
	void setRootType(JSONSchemaType rootType);

	@Getter(value = TYPES_KEY, cardinality = Cardinality.LIST, inverse = JSONSchemaType.SCHEMA_DOCUMENT_KEY)
	@Embedded
	List<JSONSchemaType> getTypes();

	@Adder(TYPES_KEY)
	void addToTypes(JSONSchemaType type);

	@Remover(TYPES_KEY)
	void removeFromTypes(JSONSchemaType type);

	JSONSchemaType getTypeByURI(String uri);

	JSONSchemaType getTypeByPointer(String pointer);

	JSONSchemaType getTypeByName(String name);

	@Override
	JSONSchemaResource getResource();

	@Override
	@Setter(FLEXO_RESOURCE)
	void setResource(FlexoResource<JSONSchemaDocument> resource);

	abstract class JSONSchemaDocumentImpl extends FlexoObjectImpl implements JSONSchemaDocument {

		public JSONSchemaDocument getResourceData() {
			return this;
		}

		@Override
		public JSONTechnologyAdapter getTechnologyAdapter() {
			if (getResource() != null) {
				return getResource().getTechnologyAdapter();
			}
			return null;
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public void setIsReadOnly(boolean b) {
			// JSON Schema resources are read-only for now.
		}

		@Override
		public Object getObject(String objectURI) {
			JSONSchemaType type = getTypeByURI(objectURI);
			if (type != null) {
				return type;
			}
			type = getTypeByPointer(objectURI);
			if (type != null) {
				return type;
			}
			return getTypeByName(objectURI);
		}

		@Override
		public JSONSchemaType getTypeByURI(String uri) {
			if (uri != null) {
				for (JSONSchemaType type : getTypes()) {
					if (uri.equals(type.getURI())) {
						return type;
					}
				}
			}
			return null;
		}

		@Override
		public JSONSchemaType getTypeByPointer(String pointer) {
			if (pointer != null) {
				for (JSONSchemaType type : getTypes()) {
					if (pointer.equals(type.getJsonPointer())) {
						return type;
					}
				}
			}
			return null;
		}

		@Override
		public JSONSchemaType getTypeByName(String name) {
			if (name != null) {
				for (JSONSchemaType type : getTypes()) {
					if (name.equals(type.getName())) {
						return type;
					}
				}
			}
			return null;
		}
	}
}
