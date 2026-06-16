package org.openflexo.technologyadapter.json.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;
import org.openflexo.ta.json.metamodel.JSONSchema;
import org.openflexo.ta.json.metamodel.JSONSchemaFactory;
import org.openflexo.ta.json.metamodel.JSONSchemaProperty;
import org.openflexo.ta.json.metamodel.JSONSchemaType;
import org.openflexo.ta.json.metamodel.JSONSchemaType.Kind;

public class TestJSONSchemaFactory {

	@Test
	public void testParseSimplePersonSchema() throws Exception {
		JSONSchemaFactory factory = new JSONSchemaFactory();
		InputStream input = getClass().getResourceAsStream("/JSONSchema/PersonSchema.schema.json");
		assertNotNull(input);

		JSONSchema schema;
		try {
			schema = factory.parse(input, "PersonGraphSchema");
		} finally {
			input.close();
		}

		assertEquals("PersonGraphSchema", schema.getURI());
		assertEquals(Kind.OBJECT, schema.getRootType().getKind());
		assertNotNull(schema.getRootType().getProperty("document"));
		assertNotNull(schema.getRootType().getProperty("nodes"));
		assertNotNull(schema.getRootType().getProperty("references"));

		JSONSchemaType person = schema.getTypeByPointer("/definitions/Person");
		assertNotNull(person);
		assertSame(person, schema.getTypeByName("Person"));
		assertEquals("Person", person.getName());
		assertEquals(Kind.OBJECT, person.getKind());
		assertEquals("PersonGraphSchema#/definitions/Person", person.getURI());
		assertEquals(Kind.STRING, person.getProperty("id").getType().getKind());
		assertEquals(Kind.STRING, person.getProperty("type").getType().getKind());
		assertEquals("Person", person.getProperty("type").getType().getConstValue());
		assertEquals(Kind.STRING, person.getProperty("name").getType().getKind());
		assertEquals(Kind.INTEGER, person.getProperty("age").getType().getKind());
		assertSame(person, schema.getTypeByURI("PersonGraphSchema#/definitions/Person"));

		JSONSchemaType company = schema.getTypeByName("Company");
		assertNotNull(company);
		assertEquals(Kind.OBJECT, company.getKind());
		assertEquals("Company", company.getProperty("type").getType().getConstValue());

		JSONSchemaType nodeItem = schema.getRootType().getProperty("nodes").getType().getItemType();
		assertEquals(2, nodeItem.getAlternativeTypes().size());
		assertSame(person, nodeItem.getAlternativeTypes().get(0).getEffectiveType());
		assertSame(company, nodeItem.getAlternativeTypes().get(1).getEffectiveType());
	}

	@Test
	public void testParseDistributedSystemSchema() throws Exception {
		JSONSchemaFactory factory = new JSONSchemaFactory();
		InputStream input = getClass().getResourceAsStream("/JSONSchema/DistributedSystem.schema.json");
		assertNotNull(input);

		JSONSchema schema;
		try {
			schema = factory.parse(input, "http://tuto/schemas/fallback.json");
		} finally {
			input.close();
		}

		assertEquals("http://tuto/schemas/DistributedSystem.schema.json", schema.getURI());
		assertEquals("DistributedSystem", schema.getTitle());
		assertEquals(Kind.OBJECT, schema.getRootType().getKind());
		assertFalse(schema.getRootType().getAdditionalPropertiesAllowed());

		JSONSchemaProperty model = schema.getRootType().getProperty("model");
		assertNotNull(model);
		assertTrue(model.isRequired());
		assertEquals(Kind.OBJECT, model.getType().getKind());
		assertTrue(model.getType().getProperty("name").isRequired());

		JSONSchemaType component = schema.getTypeByPointer("/$defs/component");
		assertNotNull(component);
		assertEquals("Component", component.getName());
		assertEquals(Kind.OBJECT, component.getKind());
		assertTrue(component.getProperty("id").isRequired());
		assertEquals(Kind.INTEGER,
				component.getProperty("resources").getType().getProperty("memoryMB").getType().getKind());

		JSONSchemaType componentReference = schema.getRootType().getProperty("components").getType().getItemType();
		assertEquals(Kind.OBJECT, componentReference.getKind());
		assertSame(component, componentReference.getEffectiveType());
		assertSame(component, schema.getTypeByURI(component.getURI()));
	}

	@Test
	public void testParseSatelliteMassSchema() throws Exception {
		JSONSchemaFactory factory = new JSONSchemaFactory();
		InputStream input = getClass().getResourceAsStream("/JSONSchema/SatelliteMass.schema.json");
		assertNotNull(input);

		JSONSchema schema;
		try {
			schema = factory.parse(input, "SatelliteMassSchema");
		} finally {
			input.close();
		}

		assertEquals("SatelliteMassSchema", schema.getURI());
		assertEquals("SatelliteMass", schema.getTitle());
		assertEquals(Kind.OBJECT, schema.getRootType().getKind());
		assertFalse(schema.getRootType().getAdditionalPropertiesAllowed());
		assertEquals(Kind.NUMBER, schema.getRootType().getProperty("calculated_total_mass_kg").getType().getKind());

		JSONSchemaType component = schema.getTypeByName("Component");
		assertNotNull(component);
		assertEquals("SatelliteMassSchema#/definitions/Component", component.getURI());
		assertEquals(Kind.OBJECT, component.getKind());
		assertFalse(component.getAdditionalPropertiesAllowed());
		assertTrue(component.getProperty("type").isRequired());
		assertTrue(component.getProperty("quantity").isRequired());
		assertTrue(component.getProperty("mass_kg_per_unit").isRequired());
		assertEquals(Kind.STRING, component.getProperty("type").getType().getKind());
		assertEquals(Kind.INTEGER, component.getProperty("quantity").getType().getKind());
		assertEquals(Kind.NUMBER, component.getProperty("mass_kg_per_unit").getType().getKind());

		JSONSchemaType componentItem = schema.getRootType().getProperty("components").getType().getItemType();
		assertSame(component, componentItem.getEffectiveType());
	}
}
