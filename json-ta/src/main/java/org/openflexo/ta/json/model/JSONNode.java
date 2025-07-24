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

package org.openflexo.ta.json.model;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PastingPoint;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a JSON node inside a {@link JSONDocument}
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(value = JSONNode.JSONNodeImpl.class)
@XMLElement
public interface JSONNode extends JSONObject {

	@PropertyIdentifier(type = JSONNode.class, cardinality = Cardinality.LIST)
	public static final String CHILDREN_KEY = "children";
	@PropertyIdentifier(type = JSONDocument.class)
	public static final String DOCUMENT_KEY = "document";
	@PropertyIdentifier(type = JsonNode.class)
	public static final String NODE_KEY = "node";

	/**
	 * Return {@link JSONDocument} where this {@link JSONNode} is defined
	 * 
	 * @return
	 */
	@Override
	@Getter(value = DOCUMENT_KEY)
	public JSONDocument getJSONDocument();

	/**
	 * Sets {@link JSONDocument} where this {@link JSONNode} is defined
	 * 
	 * @param text
	 */
	@Setter(DOCUMENT_KEY)
	public void setJSONDocument(JSONDocument document);

	/**
	 * Return {@link JsonNode} encoding this node
	 * 
	 * @return
	 */
	@Getter(value = NODE_KEY, ignoreType = true)
	public JsonNode getNode();

	/**
	 * Sets {@link JsonNode} encoding this node
	 * 
	 * @param text
	 */
	@Setter(NODE_KEY)
	public void setNode(JsonNode node);

	/**
	 * Return all {@link JSONNode} defined in this {@link JSONDocument}
	 * 
	 * @return
	 */
	@Getter(value = CHILDREN_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public List<JSONNode> getChildren();

	@Adder(CHILDREN_KEY)
	@PastingPoint
	public void addToChildren(JSONNode aNode);

	@Remover(CHILDREN_KEY)
	public void removeFromChildren(JSONNode aNode);

	/**
	 * Default base implementation for {@link JSONNode}
	 * 
	 * @author sylvain
	 *
	 */
	public static abstract class JSONNodeImpl extends XXObjectImpl implements JSONNode {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(JSONNode.class.getPackage().getName());

		public JSONNodeImpl() {

		}

		@Override
		public JSONDocument getResourceData() {
			return getJSONDocument();
		}

	}
}
