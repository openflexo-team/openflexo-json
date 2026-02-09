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
 * class test Load simple File JSON
 *
 * @author Chahrazed
 */

@RunWith(OrderedRunner.class)
public class TestLoadJSONSimpleDocuments extends AbstractJSONTest {
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
    public void testSimpleContents() {

        JSONDocument document = getJSONDocument("TestSimple.json");
        System.out.println("TestSimple.json:\n" + document.getContents());

        assertNotNull(document.getRootNode().getNodeWithKey("location"));
        assertNotNull(document.getRootNode().getNodeWithKey("setup"));
        assertTrue(document.getRootNode().getNode().isObject());
        assertEquals(32, Integer.parseInt(document.getRootNode().getNodeWithKey("age").getNode().toString()));

        JSONNode nameNode =
                document.getRootNode().getNodeWithKey("name");

        assertEquals("\"Bob\"", nameNode.getNode().toString());
        assertEquals("Bob", nameNode.getNode().asText());

        // Test the array node

        JSONNode interestNode = document.getRootNode().getNodeWithKey("interests");

        assertTrue(interestNode.getNode().isArray());
        assertNotNull(interestNode);
        assertEquals(interestNode.getNode().size(), interestNode.getChildren().size());

        assertEquals(JsonNodeType.ARRAY,interestNode.getNode().getNodeType());
        assertEquals (3, interestNode.getNode().size());

        // Test the iterator on arrays


        assertEquals("traveling",interestNode.getArrayElementAt(0).getNode().asText()); // verify this one
        assertEquals("Music",interestNode.getArrayElementAt(1).getNode().asText());
        assertEquals("reading",interestNode.getArrayElementAt(2).getNode().asText());



    }
}

