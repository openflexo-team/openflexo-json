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

package org.openflexo.ta.json;

import java.util.logging.Logger;

import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.TechnologySpecificType;
import org.openflexo.foundation.fml.annotations.DeclareModelSlots;
import org.openflexo.foundation.fml.annotations.DeclareResourceFactories;
import org.openflexo.foundation.fml.annotations.DeclareTechnologySpecificTypes;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.SpecificTypeInfo;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.ta.json.JSONIndividualType.JSONIndividualTypeFactory;
import org.openflexo.ta.json.fml.binding.JSONBindingFactory;
import org.openflexo.ta.json.metamodel.JSONSchemaType;
import org.openflexo.ta.json.rm.JSONResourceFactory;
import org.openflexo.ta.json.rm.JSONResourceRepository;

/**
 * Technology adapter exposing JSON documents and stable JSON node objects.
 * 
 * @author sylvain, Chahrazed
 * 
 */
@DeclareModelSlots({ JSONModelSlot.class })
@DeclareTechnologySpecificTypes({ JSONIndividualType.class })
@DeclareResourceFactories({ JSONResourceFactory.class })
public class JSONTechnologyAdapter extends TechnologyAdapter<JSONTechnologyAdapter> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(JSONTechnologyAdapter.class.getPackage().getName());

	private static final JSONBindingFactory BINDING_FACTORY = new JSONBindingFactory();

	@Override
	public String getName() {
		return "JSON technology adapter";
	}

	@Override
	protected String getLocalizationDirectory() {
		return "FlexoLocalization/JSONTechnologyAdapter";
	}

	@Override
	public void ensureAllRepositoriesAreCreated(FlexoResourceCenter<?> rc) {
		super.ensureAllRepositoriesAreCreated(rc);
		getJSONResourceRepository(rc);

	}

	@Override
	public <I> boolean isIgnorable(FlexoResourceCenter<I> resourceCenter, I contents) {
		return false;
	}

	@Override
	public JSONTechnologyContextManager createTechnologyContextManager(FlexoResourceCenterService service) {
		return new JSONTechnologyContextManager(this, service);
	}

	@Override
	public JSONTechnologyContextManager getTechnologyContextManager() {
		return (JSONTechnologyContextManager) super.getTechnologyContextManager();
	}

	@Override
	public JSONBindingFactory getTechnologyAdapterBindingFactory() {
		return BINDING_FACTORY;
	}

	@Override
	public String getIdentifier() {
		return "JSON";
	}

	public JSONResourceFactory getJSONResourceFactory() {
		return getResourceFactory(JSONResourceFactory.class);
	}

	@Override
	public void initTechnologySpecificTypes(TechnologyAdapterService taService) {
		taService.registerTypeClass(JSONIndividualType.class, getJSONIndividualTypeFactory());
	}

	private JSONIndividualTypeFactory jsonIndividualTypeFactory;

	public JSONIndividualTypeFactory getJSONIndividualTypeFactory() {
		if (jsonIndividualTypeFactory == null) {
			jsonIndividualTypeFactory = new JSONIndividualTypeFactory(this);
		}
		return jsonIndividualTypeFactory;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends TechnologySpecificType<JSONTechnologyAdapter>> T instantiateType(
			SpecificTypeInfo<JSONTechnologyAdapter> specificTypeInfo) {
		T returned = null;
		if (specificTypeInfo.getTechnologySpecificTypeClass().equals(JSONIndividualType.class)) {
			if (specificTypeInfo.getParameter(JSONIndividualType.SCHEMA_TYPE) != null) {
				JSONSchemaType schemaType = (JSONSchemaType) specificTypeInfo.getParameter(JSONIndividualType.SCHEMA_TYPE);
				returned = (T) getJSONIndividualTypeFactory().getIndividualOfType(schemaType);
			}
			else {
				returned = (T) JSONIndividualType.UNDEFINED_JSON_INDIVIDUAL_TYPE;
			}
		}
		if (returned != null) {
			returned.registerSpecificTypeInfo(specificTypeInfo);
			return returned;
		}
		return null;
	}

	@Override
	public String serializeType(TechnologySpecificType<JSONTechnologyAdapter> type, FMLCompilationUnit compilationUnit,
			boolean useTypeDefinitions) {
		if (type instanceof JSONIndividualType) {
			JSONIndividualType individualType = (JSONIndividualType) type;
			if (useTypeDefinitions && compilationUnit.getTypeDeclaration(type) != null) {
				return compilationUnit.getTypeDeclaration(type).getAbbrev();
			}
			if (individualType.getSchemaType() != null) {
				JSONSchemaType schemaType = individualType.getSchemaType();
				return "JSONIndividualType(" + JSONIndividualType.SCHEMA_TYPE + "=" + schemaType.getURI() + ")";
			}
			return "JSONIndividualType()";
		}
		return super.serializeType(type, compilationUnit, useTypeDefinitions);
	}

	@SuppressWarnings("unchecked")
	public <I> JSONResourceRepository<I> getJSONResourceRepository(FlexoResourceCenter<I> resourceCenter) {
		JSONResourceRepository<I> returned = resourceCenter.retrieveRepository(JSONResourceRepository.class, this);
		if (returned == null) {
			returned = JSONResourceRepository.instanciateNewRepository(this, resourceCenter);
			resourceCenter.registerRepository(returned, JSONResourceRepository.class, this);
		}
		return returned;
	}

}
