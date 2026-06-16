package org.openflexo.technologyadapter.json.metamodel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;
import org.openflexo.ta.json.AbstractJSONTest;
import org.openflexo.ta.json.JSONIndividualType;
import org.openflexo.ta.json.JSONTechnologyAdapter;
import org.openflexo.ta.json.metamodel.JSONSchema;
import org.openflexo.ta.json.metamodel.JSONSchemaFactory;
import org.openflexo.ta.json.metamodel.JSONSchemaType;
import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.model.JSONNode;

public class TestJSONIndividualType extends AbstractJSONTest {

	@Test
	public void testPersonAndCompanyIndividualTypes() throws Exception {
		instanciateTestServiceManager(JSONTechnologyAdapter.class);
		JSONSchema schema = loadSchema("/JSONSchema/PersonSchema.schema.json", "PersonGraphSchema");
		JSONDocument document = getJSONDocument("Person.json");

		JSONSchemaType personSchemaType = schema.getTypeByName("Person");
		JSONSchemaType companySchemaType = schema.getTypeByName("Company");
		assertNotNull(personSchemaType);
		assertNotNull(companySchemaType);

		JSONIndividualType personType = new JSONIndividualType(personSchemaType);
		JSONIndividualType companyType = new JSONIndividualType(companySchemaType);

		JSONNode firstNode = document.getRootNode().getNodeWithKey("nodes").getArrayElementAt(0);
		JSONNode secondNode = document.getRootNode().getNodeWithKey("nodes").getArrayElementAt(1);

		assertTrue(personType.isOfType(firstNode, false));
		assertFalse(personType.isOfType(secondNode, false));
		assertTrue(companyType.isOfType(secondNode, false));
		assertFalse(companyType.isOfType(firstNode, false));
	}

	@Test
	public void testSatelliteComponentIndividualType() throws Exception {
		instanciateTestServiceManager(JSONTechnologyAdapter.class);
		JSONSchema schema = loadSchema("/JSONSchema/SatelliteMass.schema.json", "SatelliteMassSchema");
		JSONDocument document = getJSONDocument("SatelliteMass.json");

		JSONSchemaType componentSchemaType = schema.getTypeByName("Component");
		assertNotNull(componentSchemaType);

		JSONIndividualType componentType = new JSONIndividualType(componentSchemaType);
		JSONNode components = document.getRootNode().getNodeWithKey("components");

		assertTrue(componentType.isOfType(components.getArrayElementAt(0), false));
		assertTrue(componentType.isOfType(components.getArrayElementAt(5), false));
		assertFalse(componentType.isOfType(document.getRootNode().getNodeWithKey("calculated_total_mass_kg"), false));
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
