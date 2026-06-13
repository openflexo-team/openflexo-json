/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Excelconnector, a component of the software infrastructure 
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

package org.openflexo.ta.json.fml.editionaction;

import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.ta.json.JSONModelSlot;
import org.openflexo.ta.json.JSONURIProcessor;
import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.model.JSONNode;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 *
 *
 * @author Chahrazed
 */

@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractSelectNode.AbstractSelectNodeImpl.class)
public interface AbstractSelectNode<AT> extends AbstractFetchRequest<JSONModelSlot, JSONDocument, JSONNode, AT> {

	@PropertyIdentifier(type = DataBinding.class)
	String URI_KEY = "uri";

	@Getter(URI_KEY)
	@XMLAttribute
	DataBinding<String> getURI();

	@Setter(URI_KEY)
	void setURI(DataBinding<String> uri);

	public static abstract class AbstractSelectNodeImpl<AT>
			extends AbstractFetchRequestImpl<JSONModelSlot, JSONDocument, JSONNode, AT>implements AbstractSelectNode<AT> {

		private static final Logger logger = Logger.getLogger(AbstractSelectNode.class.getPackage().getName());
		private DataBinding<String> uri;

		@Override
		public Type getFetchedType() {
			return JSONNode.class;
		}

		@Override
		public List<JSONNode> performExecute(RunTimeEvaluationContext evaluationContext) {
			JSONDocument jsonDocument = getReceiver(evaluationContext);
			List<JSONNode> selectedJsonNodes = new ArrayList<>(0);
			String selectedURI = null;

			if (getURI().isSet()) {
				try {
					selectedURI = getURI().getBindingValue(evaluationContext);
				}
				catch (TypeMismatchException | NullReferenceException | ReflectiveOperationException e) {
                    e.printStackTrace();
				}
			}
			if (selectedURI != null) {
				JSONNode selected = JSONURIProcessor.retrieveObjectWithURI(jsonDocument, selectedURI);
				if (selected != null) {
					selectedJsonNodes.add(selected);
				}
			}
			else {
				selectedJsonNodes.addAll(jsonDocument.getRootNode().getChildren());
			}

			return selectedJsonNodes;
		}

		@Override
		public DataBinding<String> getURI() {
			if (uri == null) {
				uri = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				uri.setBindingName(URI_KEY);
			}
			return uri;
		}

		@Override
		public void setURI(DataBinding<String> uri) {
			if (uri != null) {
				uri.setOwner(this);
				uri.setDeclaredType(String.class);
				uri.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				uri.setBindingName(URI_KEY);
			}
			this.uri = uri;
		}
	}
}
