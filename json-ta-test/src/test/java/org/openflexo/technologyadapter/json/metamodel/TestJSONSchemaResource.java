package org.openflexo.technologyadapter.json.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.ta.json.AbstractJSONTest;
import org.openflexo.ta.json.JSONTechnologyAdapter;
import org.openflexo.ta.json.metamodel.JSONSchemaDocument;
import org.openflexo.ta.json.metamodel.JSONSchemaType.Kind;
import org.openflexo.ta.json.rm.JSONResourceRepository;
import org.openflexo.ta.json.rm.JSONSchemaResource;
import org.openflexo.ta.json.rm.JSONSchemaResourceRepository;

public class TestJSONSchemaResource extends AbstractJSONTest {

	@Test
	public void testLoadJSONSchemaResource() throws Exception {
		instanciateTestServiceManager(JSONTechnologyAdapter.class);

		JSONTechnologyAdapter technologicalAdapter = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(JSONTechnologyAdapter.class);
		FlexoResourceCenter<?> resourceCenter = serviceManager.getResourceCenterService()
				.getFlexoResourceCenter("http://www.openflexo.org/test/json");

		assertNotNull(technologicalAdapter);
		assertNotNull(resourceCenter);

		JSONSchemaResourceRepository<?> schemaRepository = technologicalAdapter.getJSONSchemaResourceRepository(resourceCenter);
		assertNotNull(schemaRepository);

		String schemaURI = resourceCenter.getDefaultBaseURI() + "/JSON/PersonGraph.schema.json";
		JSONSchemaResource schemaResource = (JSONSchemaResource) serviceManager.getResourceManager().getResource(schemaURI, null,
				JSONSchemaDocument.class);
		assertNotNull(schemaResource);

		JSONSchemaDocument schema = loadSchema(schemaResource);
		assertEquals("PersonGraphSchema", schema.getURI());
		assertEquals(Kind.OBJECT, schema.getRootType().getKind());
		assertNotNull(schema.getTypeByName("Person"));
		assertEquals(Kind.INTEGER, schema.getTypeByName("Person").getProperty("age").getType().getKind());
		assertEquals(schema.getTypeByName("Person"), schemaResource.findObject("Person", null));
		assertEquals(schema.getTypeByName("Company"), schemaResource.findObject("Company", null));
		assertTrue(schemaRepository.getAllResources().contains(schemaResource));

		JSONResourceRepository<?> jsonRepository = technologicalAdapter.getJSONResourceRepository(resourceCenter);
		assertTrue(!jsonRepository.getAllResources().contains(schemaResource));
	}

	private JSONSchemaDocument loadSchema(JSONSchemaResource schemaResource) {
		try {
			return schemaResource.getResourceData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
		} catch (FlexoException e) {
			e.printStackTrace();
		}
		return null;
	}
}
