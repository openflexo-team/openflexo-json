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

package org.openflexo.ta.json.rm;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceFactory;
import org.openflexo.foundation.resource.StreamIODelegate;
import org.openflexo.foundation.resource.TechnologySpecificPamelaResourceFactory;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.ta.json.JSONTechnologyAdapter;
import org.openflexo.ta.json.metamodel.JSONSchema;
import org.openflexo.ta.json.metamodel.JSONSchemaDocument;
import org.openflexo.ta.json.metamodel.JSONSchemaFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of {@link FlexoResourceFactory} for {@link JSONSchemaResource}.
 * 
 * @author Chahrazed
 */
public class JSONSchemaResourceFactory
		extends TechnologySpecificPamelaResourceFactory<JSONSchemaResource, JSONSchemaDocument, JSONTechnologyAdapter, JSONSchemaFactory> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(JSONSchemaResourceFactory.class.getPackage().getName());

	public static final String JSON_SCHEMA_FILE_EXTENSION = ".schema.json";
	public static final String JSON_SCHEMAS_FILE_EXTENSION = ".schemas.json";
	private static final String JSON_SCHEMA_ID = "$id";

	public JSONSchemaResourceFactory() throws ModelDefinitionException {
		super(JSONSchemaResource.class);
	}

	@Override
	public JSONSchemaDocument makeEmptyResourceData(JSONSchemaResource resource) {
		return resource.getFactory().newInstance(JSONSchema.class);
	}

	@Override
	public <I> boolean isValidArtefact(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		String name = resourceCenter.retrieveName(serializationArtefact);
		return isJSONSchemaFileName(name) && !name.startsWith("~");
	}

	@Override
	protected <I> JSONSchemaResource initResourceForRetrieving(I serializationArtefact, FlexoResourceCenter<I> resourceCenter)
			throws ModelDefinitionException, IOException {
		JSONSchemaResource returned = super.initResourceForRetrieving(serializationArtefact, resourceCenter);
		String schemaId = readSchemaId(returned);
		if (isAbsoluteURI(schemaId)) {
			returned.setURI(schemaId);
		}
		return returned;
	}

	public static boolean isJSONSchemaFileName(String fileName) {
		return fileName != null
				&& (fileName.endsWith(JSON_SCHEMA_FILE_EXTENSION) || fileName.endsWith(JSON_SCHEMAS_FILE_EXTENSION));
	}

	@Override
	public <I> JSONSchemaResource registerResource(JSONSchemaResource resource, FlexoResourceCenter<I> resourceCenter) {
		super.registerResource(resource, resourceCenter);

		registerResourceInResourceRepository(resource,
				getTechnologyAdapter(resourceCenter.getServiceManager()).getJSONSchemaResourceRepository(resourceCenter));

		return resource;
	}

	@Override
	public JSONSchemaFactory makeModelFactory(JSONSchemaResource resource,
			TechnologyContextManager<JSONTechnologyAdapter> technologyContextManager) throws ModelDefinitionException {
		return new JSONSchemaFactory(resource, technologyContextManager.getServiceManager().getEditingContext());
	}

	private static String readSchemaId(JSONSchemaResource resource) {
		if (resource == null || !(resource.getIODelegate() instanceof StreamIODelegate)) {
			return null;
		}
		StreamIODelegate<?> ioDelegate = (StreamIODelegate<?>) resource.getIODelegate();
		ObjectMapper mapper = resource.getObjectMapper() != null ? resource.getObjectMapper() : new ObjectMapper();
		try (InputStream inputStream = ioDelegate.getInputStream()) {
			JsonNode root = mapper.readTree(inputStream);
			JsonNode id = root != null ? root.get(JSON_SCHEMA_ID) : null;
			return id != null && id.isTextual() ? id.asText() : null;
		} catch (IOException e) {
			logger.warning("Cannot read JSON Schema $id from " + resource.getIODelegate() + ": " + e.getMessage());
			return null;
		}
	}

	private static boolean isAbsoluteURI(String uri) {
		if (uri == null || uri.isEmpty()) {
			return false;
		}
		try {
			return new URI(uri).isAbsolute();
		} catch (URISyntaxException e) {
			return false;
		}
	}
}
