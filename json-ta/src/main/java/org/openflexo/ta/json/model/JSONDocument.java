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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.ta.json.JSONURIProcessor;
import org.openflexo.ta.json.rm.JSONResource;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Represents a JSON document<br>
 * This is the {@link ResourceData} deserialized from a {@link JSONResource}<br>
 * 
 * @author sylvain, Chahrazed
 *
 */
@ModelEntity
@ImplementationClass(value = JSONDocument.JSONDocumentImpl.class)
public interface JSONDocument extends JSONObject, ResourceData<JSONDocument> {

	@PropertyIdentifier(type = JSONNode.class)
	public static final String ROOT_NODE_KEY = "rootNode";

	/**
	 * Return contents as text
	 * 
	 * @return
	 */
	public String getContents();

	/**
	 * Return all {@link JSONNode} defined in this {@link JSONDocument}
	 * 
	 * @return
	 */
	@Getter(value = ROOT_NODE_KEY)
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public JSONNode getRootNode();

	@Setter(ROOT_NODE_KEY)
	public void setRootNode(JSONNode rootNode);

	public void registerNode(JSONNode node);

	public void unregisterNodeAndChildren(JSONNode node);

	public JSONNode getNodeByURI(String uri);

	public void recalculateIndex();

	public void cleanJsonContent();

	@Override
	public JSONResource getResource();

	/**
	 * Default base implementation for {@link JSONDocument}
	 * 
	 * @author sylvain
	 *
	 */
    public static abstract class JSONDocumentImpl extends JSONObjectImpl implements JSONDocument {

        @SuppressWarnings("unused")
        private static final Logger logger = Logger.getLogger(JSONObjectImpl.class.getPackage().getName());

		public static final String ALL_KEY = "All";

		private String contents = null;
		private final Map<String, JSONNode> nodesByURI = new HashMap<>();

		@Override
		public JSONDocument getResourceData() {
			return this;
		}

		@Override
		public JSONDocument getJSONDocument() {
			return this;
		}

		@Override
		public JSONResource getResource() {
			return (JSONResource) performSuperGetter(FLEXO_RESOURCE);
		}

		@Override
		public String toString() {
			return super.toString() + "-" + getResource();
		}

		@Override
		public String getContents() {
			// TODO : implements a pretty print ?
			/*if (contents == null) {
				StringBuffer sb = new StringBuffer();
				for (JSONNode xxLine : getLines()) {
					sb.append(xxLine.getValue() + "\n");
				}
				contents = sb.toString();
			}*/
			if (contents == null) {
				try {
					contents = getResource().getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(getRootNode().getNode());
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return contents;
		}

		private void clearContents() {
			contents = null;
		}

		@Override
		public void cleanJsonContent() {
			clearContents();
		}

		@Override
		public void registerNode(JSONNode node) {
			if (node != null && node.getURI() != null) {
				nodesByURI.put(node.getURI(), node);
				nodesByURI.put(JSONURIProcessor.toURI(this, node.getJsonPointer()), node);
			}
		}

		@Override
		public void unregisterNodeAndChildren(JSONNode node) {
			if (node == null) {
				return;
			}
			for (JSONNode child : node.getChildren()) {
				unregisterNodeAndChildren(child);
			}
			if (node.getURI() != null) {
				nodesByURI.remove(node.getURI());
				nodesByURI.remove(JSONURIProcessor.toURI(this, node.getJsonPointer()));
			}
		}

		@Override
		public JSONNode getNodeByURI(String uri) {
			return uri != null ? nodesByURI.get(uri) : null;
		}

		@Override
		public void recalculateIndex() {
			nodesByURI.clear();
			if (getRootNode() != null) {
				JSONURIProcessor.rebuildIndex(this);
			}
		}

		/*@Override
		public void addToLines(JSONNode aLine) {
			performSuperAdder(LINES_KEY, aLine);
			clearContents();
		}
		
		@Override
		public void removeFromLines(JSONNode aLine) {
			performSuperRemover(LINES_KEY, aLine);
			clearContents();
		}*/

	}

}
