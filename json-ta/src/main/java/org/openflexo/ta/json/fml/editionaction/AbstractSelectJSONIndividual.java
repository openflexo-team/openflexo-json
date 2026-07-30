package org.openflexo.ta.json.fml.editionaction;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.ProxyType;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.ta.json.JSONIndividualType;
import org.openflexo.ta.json.JSONTypedModelSlot;
import org.openflexo.ta.json.JSONURIProcessor;
import org.openflexo.ta.json.metamodel.JSONSchemaType;
import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.model.JSONNode;

/** Selects JSON nodes whose content conforms to a JSON Schema type. */
@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractSelectJSONIndividual.AbstractSelectJSONIndividualImpl.class)
public interface AbstractSelectJSONIndividual<AT> extends AbstractFetchRequest<JSONTypedModelSlot, JSONDocument, JSONNode, AT> {

	@PropertyIdentifier(type = JSONSchemaType.class)
	String SCHEMA_TYPE_KEY = "schemaType";

	@PropertyIdentifier(type = DataBinding.class)
	String URI_KEY = "uri";

	@Getter(value = SCHEMA_TYPE_KEY, ignoreType = true)
	@FMLAttribute(value = SCHEMA_TYPE_KEY, required = false, description = "<html>JSON Schema type used to filter individuals</html>")
	JSONSchemaType getSchemaType();

	@Setter(SCHEMA_TYPE_KEY)
	void setSchemaType(JSONSchemaType type);

	@Getter(URI_KEY)
	@XMLAttribute
	DataBinding<String> getURI();

	@Setter(URI_KEY)
	void setURI(DataBinding<String> uri);

	abstract class AbstractSelectJSONIndividualImpl<AT>
			extends AbstractFetchRequestImpl<JSONTypedModelSlot, JSONDocument, JSONNode, AT>
			implements AbstractSelectJSONIndividual<AT> {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(AbstractSelectJSONIndividual.class.getPackage().getName());

		private DataBinding<String> uri;

		@Override
		public Type getFetchedType() {
			Type returned = (Type) performSuperGetter(FETCHED_TYPE_KEY);
			if (returned != null) {
				return returned;
			}
			if (getSchemaType() != null) {
				return JSONIndividualType.getJSONIndividualOfType(getSchemaType());
			}
			return JSONNode.class;
		}

		@Override
		public void setFetchedType(Type type) {
			performSuperSetter(FETCHED_TYPE_KEY, type);
			Type effectiveType = ProxyType.getEffectiveType(type);
			if (effectiveType instanceof JSONIndividualType) {
				setSchemaType(((JSONIndividualType) effectiveType).getSchemaType());
			}
		}

		@Override
		public List<JSONNode> performExecute(RunTimeEvaluationContext evaluationContext) {
			JSONDocument jsonDocument = getReceiver(evaluationContext);
			List<JSONNode> candidates = new ArrayList<>();

			if (jsonDocument == null || jsonDocument.getRootNode() == null) {
				return candidates;
			}

			String selectedURI = evaluateURI(evaluationContext);
			if (selectedURI != null) {
				JSONNode selected = JSONURIProcessor.retrieveObjectWithURI(jsonDocument, selectedURI);
				if (selected != null) {
					candidates.add(selected);
				}
			}
			else {
				collect(jsonDocument.getRootNode(), candidates);
			}

			List<JSONNode> typedCandidates = new ArrayList<>();
			for (JSONNode candidate : candidates) {
				if (matchesFetchedType(candidate)) {
					typedCandidates.add(candidate);
				}
			}
			return filterWithConditions(typedCandidates, evaluationContext);
		}

		private String evaluateURI(RunTimeEvaluationContext evaluationContext) {
			if (getURI().isSet()) {
				try {
					return getURI().getBindingValue(evaluationContext);
				}
				catch (TypeMismatchException | NullReferenceException | ReflectiveOperationException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		private void collect(JSONNode node, List<JSONNode> nodes) {
			if (node == null) {
				return;
			}
			nodes.add(node);
			for (JSONNode child : node.getChildren()) {
				collect(child, nodes);
			}
		}

		private boolean matchesFetchedType(JSONNode node) {
			Type fetchedType = ProxyType.getEffectiveType(getFetchedType());
			if (fetchedType instanceof JSONIndividualType) {
				return ((JSONIndividualType) fetchedType).isOfType(node, false);
			}
			return TypeUtils.isOfType(node, fetchedType);
		}

		@Override
		public DataBinding<String> getURI() {
			if (uri == null) {
				uri = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				uri.setBindingName(URI_KEY);
			}
			return uri;
		}

		@Override
		public void setURI(DataBinding<String> uri) {
			if (uri != null) {
				uri.setOwner(this);
				uri.setDeclaredType(String.class);
				uri.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				uri.setBindingName(URI_KEY);
			}
			this.uri = uri;
		}
	}
}
