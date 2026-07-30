package org.openflexo.ta.json.validation;

import java.util.HashSet;
import java.util.Set;

import org.openflexo.ta.json.metamodel.JSONSchemaDocument;
import org.openflexo.ta.json.metamodel.JSONSchemaProperty;
import org.openflexo.ta.json.metamodel.JSONSchemaType;
import org.openflexo.ta.json.metamodel.JSONSchemaType.Kind;
import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.model.JSONNode;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * OpenFlexo validation facade for JSON Schema.
 * <p>
 * The public API is intentionally independent from the concrete validator library. The current implementation validates the subset parsed
 * in {@link org.openflexo.ta.json.metamodel.JSONSchemaFactory}; an external validator can later replace the internals without changing
 * callers.
 */
public class JSONSchemaValidationService {

	public JSONValidationReport validate(JSONDocument document, JSONSchemaDocument schemaDocument) {
		JSONValidationReport report = new JSONValidationReport();
		if (document == null) {
			report.addError("#", "document", "No JSON document supplied");
			return report;
		}
		if (schemaDocument == null) {
			report.addError("#", "schema", "No JSON schema document supplied");
			return report;
		}
		if (schemaDocument.getRootType() == null) {
			report.addError("#", "schema", "JSON schema has no root type");
			return report;
		}
		validateNode(document.getRootNode(), schemaDocument.getRootType().getEffectiveType(), "#", report);
		return report;
	}

	public boolean isValid(JSONDocument document, JSONSchemaDocument schemaDocument) {
		return validate(document, schemaDocument).isValid();
	}

	private void validateNode(JSONNode node, JSONSchemaType schemaType, String path, JSONValidationReport report) {
		if (schemaType == null || node == null || node.getNode() == null) {
			return;
		}
		JSONSchemaType effectiveType = schemaType.getEffectiveType();
		if (matchesOneAlternative(node, effectiveType, path, report)) {
			return;
		}
		if (!matchesKind(node.getNode(), effectiveType.getKind())) {
			report.addError(path, "type", "Expected " + effectiveType.getKind() + " but found " + node.getNode().getNodeType());
			return;
		}
		if (effectiveType.getConstValue() != null && !effectiveType.getConstValue().equals(node.getNode().asText())) {
			report.addError(path, "const", "Expected constant value " + effectiveType.getConstValue());
			return;
		}
		if (effectiveType.getKind() == Kind.OBJECT) {
			validateObject(node, effectiveType, path, report);
		}
		else if (effectiveType.getKind() == Kind.ARRAY) {
			validateArray(node, effectiveType, path, report);
		}
	}

	private boolean matchesOneAlternative(JSONNode node, JSONSchemaType schemaType, String path, JSONValidationReport report) {
		if (schemaType.getAlternativeTypes().isEmpty()) {
			return false;
		}
		for (JSONSchemaType alternative : schemaType.getAlternativeTypes()) {
			JSONValidationReport alternativeReport = new JSONValidationReport();
			validateNode(node, alternative.getEffectiveType(), path, alternativeReport);
			if (alternativeReport.isValid()) {
				return true;
			}
		}
		report.addError(path, "oneOf", "Node does not match any declared JSON Schema alternative");
		return true;
	}

	private void validateObject(JSONNode node, JSONSchemaType schemaType, String path, JSONValidationReport report) {
		Set<String> declaredProperties = new HashSet<>();
		for (JSONSchemaProperty property : schemaType.getProperties()) {
			declaredProperties.add(property.getName());
			JSONNode child = directChildWithKey(node, property.getName());
			if (child == null) {
				if (property.isRequired()) {
					report.addError(append(path, property.getName()), "required", "Required property is missing");
				}
			}
			else {
				validateNode(child, property.getType(), append(path, property.getName()), report);
			}
		}
		if (!schemaType.getAdditionalPropertiesAllowed()) {
			for (JSONNode child : node.getChildren()) {
				if (child.getKey() != null && !declaredProperties.contains(child.getKey())) {
					report.addError(append(path, child.getKey()), "additionalProperties", "Additional property is not allowed");
				}
			}
		}
	}

	private void validateArray(JSONNode node, JSONSchemaType schemaType, String path, JSONValidationReport report) {
		if (schemaType.getItemType() == null) {
			return;
		}
		for (JSONNode child : node.getChildren()) {
			validateNode(child, schemaType.getItemType(), append(path, String.valueOf(child.getIndex())), report);
		}
	}

	private static boolean matchesKind(JsonNode node, Kind kind) {
		if (kind == null || kind == Kind.ANY) {
			return true;
		}
		switch (kind) {
			case OBJECT:
				return node.isObject();
			case ARRAY:
				return node.isArray();
			case STRING:
				return node.isTextual();
			case INTEGER:
				return node.isIntegralNumber();
			case NUMBER:
				return node.isNumber();
			case BOOLEAN:
				return node.isBoolean();
			case NULL:
				return node.isNull();
			case ANY:
			default:
				return true;
		}
	}

	private static JSONNode directChildWithKey(JSONNode node, String key) {
		for (JSONNode child : node.getChildren()) {
			if (key.equals(child.getKey())) {
				return child;
			}
		}
		return null;
	}

	private static String append(String path, String token) {
		String escaped = token.replace("~", "~0").replace("/", "~1");
		return "#".equals(path) ? "#/" + escaped : path + "/" + escaped;
	}
}
