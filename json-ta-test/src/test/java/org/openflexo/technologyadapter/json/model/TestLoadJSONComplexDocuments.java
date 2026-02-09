package org.openflexo.technologyadapter.json.model;

import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.ta.json.AbstractJSONTest;
import org.openflexo.ta.json.JSONTechnologyAdapter;
import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.model.JSONNode;
import org.openflexo.ta.json.rm.JSONResource;
import org.openflexo.ta.json.rm.JSONResourceRepository;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.logging.Logger;


/**
 *
 * Copyright (c) 2018, Openflexo
 *
 * This file is part of OpenflexoTechnologyAdapter, a component of the software infrastructure
 * developed at Openflexo.
 *
 *
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either
 * version 1.1 of the License, or any later version ), which is available at
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 *
 * You can redistribute it and/or modify under the terms of either of these licenses
 *
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or
 *          combining it with software containing parts covered by the terms
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. *
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.openflexo.org/license.html for details.
 *
 *
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 *
 */

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.ta.json.AbstractJSONTest;
import org.openflexo.ta.json.JSONTechnologyAdapter;
import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.rm.JSONResource;
import org.openflexo.ta.json.rm.JSONResourceRepository;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

import static org.junit.Assert.*;

/**
 * class test Load complex File JSON
 *
 * @author Chahrazed
 */

@RunWith(OrderedRunner.class)
public class TestLoadJSONComplexDocuments extends AbstractJSONTest {
    protected static final Logger logger = Logger.getLogger(TestLoadJSONDocuments.class.getPackage().getName());

    @Test
    @TestOrder(1)
    public void testInitializeServiceManager() throws Exception {
        instanciateTestServiceManager(JSONTechnologyAdapter.class);
    }

    @Test
    @TestOrder(2)
    public void testJSONLoading() {
        JSONTechnologyAdapter technologicalAdapter = serviceManager.getTechnologyAdapterService()
                .getTechnologyAdapter(JSONTechnologyAdapter.class);

        for (FlexoResourceCenter<?> resourceCenter : serviceManager.getResourceCenterService().getResourceCenters()) {
            JSONResourceRepository<?> jsonRepository = technologicalAdapter.getJSONResourceRepository(resourceCenter);
            assertNotNull(jsonRepository);
            Collection<JSONResource> documents = jsonRepository.getAllResources();
            for (JSONResource jsonResource : documents) {
                try {
                    jsonResource.loadResourceData();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (ResourceLoadingCancelledException e) {
                    e.printStackTrace();
                } catch (FlexoException e) {
                    e.printStackTrace();
                }
                assertNotNull(jsonResource.getLoadedResourceData());
                System.out.println("URI of document: " + jsonResource.getURI());
                System.out.println("ResourceData: " + jsonResource.getLoadedResourceData());
            }
        }
    }

    @Test
    @TestOrder(3)
    public void testComplexContents() {

        // Test the document contents

        JSONDocument document = getJSONDocument("ExempleComplex.json");
        System.out.println("ExempleComplex.json:\n" + document.getContents());
        assertTrue(document.getRootNode().getNode().size() > 1);
        // Test the object node

        JSONNode model = document.getRootNode().getNodeWithKey("model");
        JSONNode name = model.getNodeWithKey("name");
        assertEquals("DistributedSystem", name.getNode().asText());
        assertNotNull(model);
        assertTrue(document.getRootNode().getNode().isObject());
        assertEquals(500, Integer.parseInt(document.getRootNode().getNodeWithKey("object").getNode().toString()));

        // Test the array node

        JSONNode arrayConnectorsNode =
                document.getRootNode().getNodeWithKey("connectors");

        JSONNode componentsNode = document.getRootNode().getNodeWithKey("components");

        assertTrue(arrayConnectorsNode.getNode().isArray());
        assertNotNull(componentsNode);
        assertNotNull(arrayConnectorsNode);
        assertTrue(componentsNode.getNode().isArray());
        assertEquals(componentsNode.getNode().size(), componentsNode.getChildren().size());

        assertEquals(JsonNodeType.ARRAY,arrayConnectorsNode.getNode().getNodeType());
        assertNotNull(arrayConnectorsNode);
        System.out.println("size of array:\n" + arrayConnectorsNode.getChildren().size());
        assertTrue( arrayConnectorsNode.getChildren().size() == 1);
        assertEquals(1, arrayConnectorsNode.getNode().size());
        assertEquals(2, componentsNode.getNode().size());

        // verify that the first element of array not null

        System.out.println("Test get Array Element:\n" + arrayConnectorsNode.getArrayElementAt(0));
        JSONNode element = arrayConnectorsNode.getArrayElementAt(0);
         assertNotNull(element);

        // verify that the 5 element of array null
        assertNull(componentsNode.getArrayElementAt(5));

        // Verify the contents

        JSONNode node = componentsNode.getNodeWithKey("id");
        assertNotNull(node);


        assertEquals("cmp-001", node.getNode().asText());

        // Test the iterator on arrays

        for (JSONNode arrayChild : componentsNode.getChildren()) {
            assertNotNull(arrayChild);
            assertNotNull(arrayChild.getNodeWithKey("id"));
            assertNotNull(arrayChild.getNodeWithKey("name"));
        }



    }
}


