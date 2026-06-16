package org.openflexo.ta.json.metamodel;

import java.util.List;

import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;

/** OpenFlexo representation of a JSON Schema document. */
@ModelEntity
@ImplementationClass(JSONSchema.JSONSchemaImpl.class)
public interface JSONSchema {

	String URI_KEY = "uri";
	String TITLE_KEY = "title";
	String ROOT_TYPE_KEY = "rootType";
	String TYPES_KEY = "types";

	@Getter(URI_KEY)
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

	@Getter(value = TYPES_KEY, cardinality = Cardinality.LIST)
	@Embedded
	List<JSONSchemaType> getTypes();

	@Adder(TYPES_KEY)
	void addToTypes(JSONSchemaType type);

	@Remover(TYPES_KEY)
	void removeFromTypes(JSONSchemaType type);

	JSONSchemaType getTypeByURI(String uri);

	JSONSchemaType getTypeByPointer(String pointer);

	JSONSchemaType getTypeByName(String name);

	abstract class JSONSchemaImpl implements JSONSchema {

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
