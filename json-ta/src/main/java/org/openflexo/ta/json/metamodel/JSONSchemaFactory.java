package org.openflexo.ta.json.metamodel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openflexo.foundation.PamelaResourceModelFactory;
import org.openflexo.pamela.PamelaMetaModelLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.EditingContext;
import org.openflexo.pamela.factory.PamelaModelFactory;
import org.openflexo.ta.json.metamodel.JSONSchemaType.Kind;
import org.openflexo.ta.json.rm.JSONSchemaResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Builds the OpenFlexo metamodel representation from a JSON Schema document. */
public class JSONSchemaFactory extends PamelaModelFactory implements PamelaResourceModelFactory<JSONSchemaResource> {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Map<String, JSONSchemaType> typesByPointer = new HashMap<>();
	private final JSONSchemaResource resource;
	private JSONSchema schema;
	private String baseURI;

	public JSONSchemaFactory() throws ModelDefinitionException {
		super(PamelaMetaModelLibrary.retrieveMetaModel(JSONSchema.class));
		this.resource = null;
	}

	public JSONSchemaFactory(JSONSchemaResource resource, EditingContext editingContext) throws ModelDefinitionException {
		super(PamelaMetaModelLibrary.retrieveMetaModel(JSONSchema.class));
		this.resource = resource;
		setEditingContext(editingContext);
	}

	@Override
	public JSONSchemaResource getResource() {
		return resource;
	}

	@Override
	public void startDeserializing() {
	}

	@Override
	public void stopDeserializing() {
	}

	public JSONSchema parse(InputStream inputStream, String fallbackURI) throws IOException {
		return parse(objectMapper.readTree(inputStream), fallbackURI);
	}

	public JSONSchema parse(JsonNode schemaNode, String fallbackURI) {
		typesByPointer.clear();
		schema = newInstance(JSONSchema.class);
		baseURI = text(schemaNode, "$id", fallbackURI);
		schema.setURI(baseURI);
		schema.setTitle(text(schemaNode, "title", null));

		JSONSchemaType rootType = parseType(schemaNode, "", schema.getTitle() != null ? schema.getTitle() : "Root");
		schema.setRootType(rootType);
		parseDefinitions(schemaNode.get("$defs"), "/$defs");
		parseDefinitions(schemaNode.get("definitions"), "/definitions");
		resolveReferences();
		return schema;
	}

	private void parseDefinitions(JsonNode definitions, String pointer) {
		if (definitions != null && definitions.isObject()) {
			Iterator<Map.Entry<String, JsonNode>> fields = definitions.fields();
			while (fields.hasNext()) {
				Map.Entry<String, JsonNode> field = fields.next();
				parseType(field.getValue(), pointer + "/" + escape(field.getKey()), field.getKey());
			}
		}
	}

	private JSONSchemaType parseType(JsonNode node, String pointer, String suggestedName) {
		JSONSchemaType existing = typesByPointer.get(pointer);
		if (existing != null) {
			return existing;
		}

		JSONSchemaType type = newInstance(JSONSchemaType.class);
		type.setName(text(node, "title", suggestedName));
		type.setJsonPointer(pointer);
		type.setURI(toURI(pointer));
		if (node.has("const")) {
			type.setConstValue(node.get("const").asText());
		}
		type.setAdditionalPropertiesAllowed(!node.has("additionalProperties") || node.get("additionalProperties").asBoolean(true));
		typesByPointer.put(pointer, type);
		schema.addToTypes(type);

		JsonNode reference = node.get("$ref");
		if (reference != null && reference.isTextual()) {
			type.setKind(Kind.ANY);
			type.setURI(reference.asText());
			return type;
		}

		type.setKind(readKind(node));
		JsonNode alternatives = node.get("oneOf");
		if (alternatives == null) {
			alternatives = node.get("anyOf");
		}
		if (alternatives != null && alternatives.isArray()) {
			int alternativeIndex = 0;
			for (JsonNode alternative : alternatives) {
				type.addToAlternativeTypes(parseType(alternative, pointer + "/oneOf/" + alternativeIndex, suggestedName + "Alternative"
						+ alternativeIndex));
				alternativeIndex++;
			}
		}
		if (type.getKind() == Kind.OBJECT) {
			Set<String> required = readRequired(node.get("required"));
			JsonNode properties = node.get("properties");
			if (properties != null && properties.isObject()) {
				Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();
				while (fields.hasNext()) {
					Map.Entry<String, JsonNode> field = fields.next();
					String propertyPointer = pointer + "/properties/" + escape(field.getKey());
					JSONSchemaProperty property = newInstance(JSONSchemaProperty.class);
					property.setName(field.getKey());
					property.setRequired(required.contains(field.getKey()));
					property.setType(parseType(field.getValue(), propertyPointer, field.getKey()));
					type.addToProperties(property);
				}
			}
		}
		else if (type.getKind() == Kind.ARRAY && node.has("items") && node.get("items").isObject()) {
			type.setItemType(parseType(node.get("items"), pointer + "/items", suggestedName + "Item"));
		}
		return type;
	}

	private void resolveReferences() {
		for (JSONSchemaType type : schema.getTypes()) {
			if (type.getKind() == Kind.ANY && type.getURI() != null && type.getURI().startsWith("#")) {
				JSONSchemaType referenced = typesByPointer.get(type.getURI().substring(1));
				if (referenced != null && referenced != type) {
					type.setReferencedType(referenced);
					type.setKind(referenced.getKind());
					type.setURI(toURI(type.getJsonPointer()));
				}
			}
		}
	}

	private Kind readKind(JsonNode node) {
		JsonNode declaredType = node.get("type");
		if (declaredType != null && declaredType.isTextual()) {
			try {
				return Kind.valueOf(declaredType.asText().toUpperCase());
			} catch (IllegalArgumentException e) {
				return Kind.ANY;
			}
		}
		if (node.has("properties")) {
			return Kind.OBJECT;
		}
		if (node.has("items")) {
			return Kind.ARRAY;
		}
		return Kind.ANY;
	}

	private Set<String> readRequired(JsonNode requiredNode) {
		Set<String> returned = new HashSet<>();
		if (requiredNode != null && requiredNode.isArray()) {
			for (JsonNode value : requiredNode) {
				if (value.isTextual()) {
					returned.add(value.asText());
				}
			}
		}
		return returned;
	}

	private String toURI(String pointer) {
		return (baseURI != null ? baseURI : "") + "#" + pointer;
	}

	private static String text(JsonNode node, String field, String defaultValue) {
		JsonNode value = node != null ? node.get(field) : null;
		return value != null && value.isTextual() ? value.asText() : defaultValue;
	}

	private static String escape(String token) {
		return token.replace("~", "~0").replace("/", "~1");
	}
}
