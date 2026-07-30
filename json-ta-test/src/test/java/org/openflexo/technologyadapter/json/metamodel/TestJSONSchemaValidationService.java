package org.openflexo.technologyadapter.json.metamodel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;
import org.openflexo.ta.json.AbstractJSONTest;
import org.openflexo.ta.json.JSONTechnologyAdapter;
import org.openflexo.ta.json.metamodel.JSONSchema;
import org.openflexo.ta.json.metamodel.JSONSchemaFactory;
import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.model.JSONNode;
import org.openflexo.ta.json.validation.JSONSchemaValidationService;
import org.openflexo.ta.json.validation.JSONValidationReport;

public class TestJSONSchemaValidationService extends AbstractJSONTest {

	@Test
	public void testValidPersonGraphDocument() throws Exception {
		instanciateTestServiceManager(JSONTechnologyAdapter.class);
		JSONSchema schema = loadSchema("/JSONSchema/PersonSchema.schema.json", "PersonGraphSchema");
		JSONDocument document = getJSONDocument("Person.json");

		JSONSchemaValidationService validationService = new JSONSchemaValidationService();
		JSONValidationReport report = validationService.validate(document, schema);

		assertTrue(report.toString(), report.isValid());
	}

	@Test
	public void testInvalidPersonGraphDocumentReportsIssue() throws Exception {
		instanciateTestServiceManager(JSONTechnologyAdapter.class);
		JSONSchema schema = loadSchema("/JSONSchema/PersonSchema.schema.json", "PersonGraphSchema");
		JSONDocument document = getJSONDocument("Person.json");

		JSONNode person = document.getRootNode().getNodeWithKey("nodes").getArrayElementAt(0);
		assertNotNull(person.deleteNode("age"));

		JSONSchemaValidationService validationService = new JSONSchemaValidationService();
		JSONValidationReport report = validationService.validate(document, schema);

		assertFalse(report.toString(), report.isValid());
		assertTrue(report.getIssues().toString(), report.getIssues().get(0).getPath().contains("/nodes/0"));
	}

	private JSONSchema loadSchema(String path, String fallbackURI) throws Exception {
		JSONSchemaFactory factory = new JSONSchemaFactory();
		InputStream input = getClass().getResourceAsStream(path);
		assertNotNull(input);
		try {
			return factory.parse(input, fallbackURI);
		} finally {
			input.close();
		}
	}
}
