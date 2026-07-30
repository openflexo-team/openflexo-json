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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.resource.PamelaResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.ta.json.metamodel.JSONSchema;
import org.openflexo.ta.json.metamodel.JSONSchemaDocument;
import org.openflexo.ta.json.metamodel.JSONSchemaType;
import org.openflexo.ta.json.metamodel.JSONSchemaFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Default implementation for a resource storing a {@link JSONSchema}.
 * 
 * @author Chahrazed
 */
public abstract class JSONSchemaResourceImpl extends PamelaResourceImpl<JSONSchemaDocument, JSONSchemaFactory> implements JSONSchemaResource {

	private static final Logger logger = Logger.getLogger(JSONSchemaResourceImpl.class.getPackage().getName());

	private ObjectMapper mapper = null;

	@Override
	public JSONSchema getJSONSchema() {
		JSONSchemaDocument document = getJSONSchemaDocument();
		return document instanceof JSONSchema ? (JSONSchema) document : null;
	}

	@Override
	public JSONSchemaDocument getJSONSchemaDocument() {
		try {
			return getResourceData();
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (FlexoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONSchemaDocument getMetaModelData() {
		return getJSONSchemaDocument();
	}

	@Override
	public ObjectMapper getObjectMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper();
		}
		return mapper;
	}

	@Override
	protected JSONSchemaDocument performLoad() throws IOException, Exception {
		if (getFlexoIOStreamDelegate() == null) {
			throw new IOFlexoException("Cannot load JSON schema with this IO/delegate: " + getIODelegate());
		}

		notifyResourceWillLoad();

		JSONSchemaDocument returned;
		try {
			returned = getFactory().parse(getFlexoIOStreamDelegate().getInputStream(), getURI());
			getInputStream().close();
		} catch (IOException e) {
			throw new IOFlexoException(e);
		}

		if (returned == null) {
			logger.warning("Cannot retrieve JSON schema from serialization artifact " + getIODelegate());
			return null;
		}

		notifyResourceLoaded();
		return returned;
	}

	@Override
	public Class<JSONSchemaDocument> getResourceDataClass() {
		return JSONSchemaDocument.class;
	}

	@Override
	public FlexoObject findObject(String objectIdentifier, String userIdentifier) {
		JSONSchemaDocument schemaDocument = getJSONSchemaDocument();
		if (schemaDocument != null && objectIdentifier != null) {
			JSONSchemaType type = schemaDocument.getTypeByName(objectIdentifier);
			if (type == null) {
				type = schemaDocument.getTypeByPointer(objectIdentifier);
			}
			if (type == null) {
				type = schemaDocument.getTypeByURI(objectIdentifier);
			}
			if (type != null) {
				return type;
			}
		}
		return super.findObject(objectIdentifier, userIdentifier);
	}

	@Override
	protected void performSave(boolean clearIsModified) throws SaveResourceException {
		throw new SaveResourceException(getIODelegate(), new UnsupportedOperationException("JSON schema resources are read-only"));
	}
}
