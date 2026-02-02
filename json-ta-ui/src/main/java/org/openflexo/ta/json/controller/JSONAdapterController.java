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

package org.openflexo.ta.json.controller;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.gina.utils.InspectorGroup;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.ta.json.JSONTechnologyAdapter;
import org.openflexo.ta.json.fml.JSONNodeRole;
import org.openflexo.ta.json.fml.editionaction.AbstractSelectNode;
import org.openflexo.ta.json.fml.editionaction.AddNode;
import org.openflexo.ta.json.gui.JSONIconLibrary;
import org.openflexo.ta.json.model.JSONNode;
import org.openflexo.ta.json.model.JSONObject;
import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.view.JSONDocumentView;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class JSONAdapterController extends TechnologyAdapterController<JSONTechnologyAdapter> {

	static final Logger logger = Logger.getLogger(JSONAdapterController.class.getPackage().getName());

	private InspectorGroup jsonInspectorGroup;

	@Override
	public Class<JSONTechnologyAdapter> getTechnologyAdapterClass() {
		return JSONTechnologyAdapter.class;
	}

	/**
	 * Initialize inspectors for supplied module using supplied {@link FlexoController}
	 * 
	 * @param controller
	 */
	@Override
	protected void initializeInspectors(FlexoController controller) {

		jsonInspectorGroup = controller.loadInspectorGroup("JSON", getTechnologyAdapter().getLocales(),
				getFMLTechnologyAdapterInspectorGroup());
	}

	/**
	 * Return inspector group for this technology
	 * 
	 * @return
	 */
	@Override
	public InspectorGroup getTechnologyAdapterInspectorGroup() {
		return jsonInspectorGroup;
	}

	@Override
	protected void initializeActions(ControllerActionInitializer actionInitializer) {

		// You can initialize here actions specific to that technology
	}

	/**
	 * Return icon representing underlying technology, required size is 32x32
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyBigIcon() {
		return JSONIconLibrary.JSON_TA_BIG_ICON;
	}

	/**
	 * Return icon representing underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyIcon() {
		return JSONIconLibrary.JSON_TA_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getModelIcon() {
		return JSONIconLibrary.JSON_DOCUMENT_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getMetaModelIcon() {
		return JSONIconLibrary.JSON_DOCUMENT_ICON;
	}

	/**
	 * Return icon representing supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForTechnologyObject(Class<? extends TechnologyObject<?>> objectClass) {
		if (JSONObject.class.isAssignableFrom(objectClass)) {
			return JSONIconLibrary.iconForObject((Class<? extends JSONObject>) objectClass);
		}
		return null;
	}

	/**
	 * Return icon representing supplied pattern property
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForFlexoRole(Class<? extends FlexoRole<?>> patternRoleClass) {
		if (JSONNodeRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForTechnologyObject(JSONNode.class);
		}
		return null;
	}

	/**
	 * Return icon representing supplied edition action
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForEditionAction(Class<? extends EditionAction> editionActionClass) {
		if (AddNode.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForTechnologyObject(JSONNode.class), IconLibrary.DUPLICATE);
		}
		else if (AbstractSelectNode.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForTechnologyObject(JSONNode.class), IconLibrary.IMPORT);
		}
		return super.getIconForEditionAction(editionActionClass);
	}

	@Override
	public boolean isRepresentableInModuleView(TechnologyObject<JSONTechnologyAdapter> object) {
		return object instanceof JSONDocument;
	}

	@Override
	public FlexoObject getRepresentableMasterObject(TechnologyObject<JSONTechnologyAdapter> object) {
		if (object instanceof JSONDocument) {
			return object;
		}
		return null;
	}

	@Override
	public String getWindowTitleforObject(TechnologyObject<JSONTechnologyAdapter> object, FlexoController controller) {
		if (object instanceof JSONDocument) {
			return ((JSONDocument) object).getResource().getName();
		}
		return object.toString();
	}

	@Override
	public ModuleView<?> createModuleViewForMasterObject(TechnologyObject<JSONTechnologyAdapter> object, FlexoController controller,
			FlexoPerspective perspective) {
		if (object instanceof JSONDocument) {
			JSONDocumentView returned = new JSONDocumentView((JSONDocument) object, controller, perspective);
			return returned;
		}
		return new EmptyPanel<>(controller, perspective, object);
	}

}
