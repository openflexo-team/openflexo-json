/**
 *
 * Copyright (c) 2018, Openflexo
 * <p>
 * This file is part of OpenflexoTechnologyAdapter, a component of the software infrastructure
 * developed at Openflexo.
 * <p>
 * <p>
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either
 * version 1.1 of the License, or any later version ), which is available at
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * <p>
 * You can redistribute it and/or modify under the terms of either of these licenses
 * <p>
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 * <p>
 * Additional permission under GNU GPL version 3 section 7
 * <p>
 * If you modify this Program, or any covered work, by linking or
 * combining it with software containing parts covered by the terms
 * of EPL 1.0, the licensors of this Program grant you additional permission
 * to convey the resulting work. *
 * <p>
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * <p>
 * See http://www.openflexo.org/license.html for details.
 * <p>
 * <p>
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 *
 */

package org.openflexo.ta.json.model;

import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
     * @param document
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
     * @param node
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

    public JSONNode createNode(String key, Object value);

    public JSONNode createObjectNode(String key);

    public JSONNode createArrayNode(String key);

    public JSONNode deleteNode(String key);

    public JsonNode getContent();

    public JSONNode getNodeWithKey(String key);

    public void setNodeValue(String key, Object value);

    public JSONNode addArrayElement(Object value);

    public void removeArrayElement(int index);

    JSONNode getArrayElementAt(int index);

    /**
     * Default base implementation for {@link JSONNode}
     *
     * @author sylvain
     *
     */
    public static abstract class JSONNodeImpl extends JSONObjectImpl implements JSONNode {

        @SuppressWarnings("unused")
        private static final Logger logger = Logger.getLogger(JSONNode.class.getPackage().getName());

        public JSONNodeImpl() {

        }

        @Override
        public JSONDocument getResourceData() {
            return getJSONDocument();
        }

        @Override
        public List<JSONNode> getChildren() {
            JsonNode node = getNode();

            if (node == null) {
                return java.util.Collections.emptyList();
            }

            List<JSONNode> listChildren = new java.util.ArrayList<>();

            // children are array elements
            if (node.isArray()) {
                for (JsonNode element : node) {
                    listChildren.add(getFactory().makeJSONNode(element, this, false));
                }
                return listChildren;
            }

            // children are values
            if (node.isObject()) {
                node.fields().forEachRemaining(entry -> {
                    listChildren.add(getFactory().makeJSONNode(entry.getValue(), this, false));
                });
                return listChildren;
            }

            // Primitive → no children
            return java.util.Collections.emptyList();
        }

        @Override
        public JSONNode getNodeWithKey(String key) {
            if (key == null || key.isEmpty()) return null;

            JsonNode current = getNode();
            if (current != null && current.has(key)) {
                JSONNode valueNode = getFactory().newInstance(JSONNode.class);
                valueNode.setJSONDocument(getJSONDocument());
                valueNode.setNode(current.get(key));
                return valueNode;
            }

            for (JSONNode child : getChildren()) {
                JSONNode found = child.getNodeWithKey(key);
                if (found != null) {
                    return found;
                }
            }

            JsonNode jacksonNode = getNode().at("");

            return null;
        }

        @Override
        public JsonNode getContent() {
            return getNode();
        }

        @Override
        public void setNodeValue(String key, Object value) {
            JsonNode current = getNode();
            if (!(current instanceof ObjectNode)) {
                throw new IllegalStateException(
                        "Cannot set a key on a non-object JSON node: " +
                                (current != null ? current.getNodeType() : "null")
                );
            }

            ObjectMapper mapper = getJSONDocument()
                    .getResource()
                    .getObjectMapper();

            JsonNode jsonValue = mapper.valueToTree(value);
            ((ObjectNode) current).set(key, jsonValue);
        }

        @Override
        public JSONNode getArrayElementAt(int index) {
            JsonNode n = getNode();
            if (!n.isArray()) {
                throw new IllegalStateException("Not an array node");
            }

            JsonNode element = n.get(index);
            if (element == null) {
                return null;
            }

            return getFactory().makeJSONNode(element, this, false);
        }


        /**
         * Creates a new JSON node with the given key and content, inserts it into the
         * document's root object, and returns the corresponding {@link JSONNode}.
         * @param key      the JSON field name to create
         * @param content  the textual content associated with the given key
         *
         * @return the newly created {@link JSONNode} representing the key/value pair
         */
        @Override
        public JSONNode createNode(String key, Object content) {
            JsonNode current = getNode();

            if (!(current instanceof ObjectNode)) {
                throw new IllegalStateException(
                        "Cannot add a keyed node to a non-object JSON node: " +
                                (current != null ? current.getNodeType() : "null")
                );
            }

            ObjectMapper mapper = getJSONDocument()
                    .getResource()
                    .getObjectMapper();

            JsonNode jsonValue = mapper.valueToTree(content);

            ObjectNode objectNode = (ObjectNode) current;
            objectNode.set(key, jsonValue);

            JSONNode created = getFactory().makeJSONNode(jsonValue, this, false);
            addToChildren(created);

            return created;
        }

        @Override
        public JSONNode createObjectNode(String key) {
            JsonNode current = getNode();
            if (!(current instanceof ObjectNode)) {
                throw new IllegalStateException(
                        "Cannot add an object node to a non-object JSON node: " +
                                (current != null ? current.getNodeType() : "null")
                );
            }

            ObjectMapper mapper = getJSONDocument()
                    .getResource()
                    .getObjectMapper();

            ObjectNode objectValue = mapper.createObjectNode();

            ObjectNode parent = (ObjectNode) current;
            parent.set(key, objectValue);

            JSONNode created = getFactory().makeJSONNode(objectValue, this, false);
            addToChildren(created);

            return created;
        }

        @Override
        public JSONNode createArrayNode(String key) {
            JsonNode current = getNode();
            if (!(current instanceof ObjectNode)) {
                throw new IllegalStateException(
                        "Cannot add an array node to a non-object JSON node: " +
                                (current != null ? current.getNodeType() : "null")
                );
            }

            ObjectMapper mapper = getJSONDocument()
                    .getResource()
                    .getObjectMapper();

            ArrayNode arrayValue = mapper.createArrayNode();

            ObjectNode parent = (ObjectNode) current;
            parent.set(key, arrayValue);

            JSONNode created = getFactory().makeJSONNode(arrayValue, this, false);
            addToChildren(created);

            return created;
        }

        @Override
        public JSONNode addArrayElement(Object value) {
            JsonNode current = getNode();
            if (!(current instanceof ArrayNode)) {
                throw new IllegalStateException(
                        "Cannot add element to a non-array JSON node: " +
                                (current != null ? current.getNodeType() : "null")
                );
            }

            ObjectMapper mapper = getJSONDocument()
                    .getResource()
                    .getObjectMapper();

            JsonNode jsonValue = mapper.valueToTree(value);

            ArrayNode array = (ArrayNode) current;
            array.add(jsonValue);

            JSONNode created = getFactory().makeJSONNode(jsonValue, this, false);
            addToChildren(created);

            return created;
        }

        @Override
        public void removeArrayElement(int index) {
            JsonNode current = getNode();
            if (!(current instanceof ArrayNode)) {
                throw new IllegalStateException(
                        "Cannot remove element from a non-array JSON node: " +
                                (current != null ? current.getNodeType() : "null")
                );
            }

            ArrayNode array = (ArrayNode) current;

            if (index < 0 || index >= array.size()) {
                throw new IndexOutOfBoundsException(
                        "Index " + index + " out of bounds for array of size " + array.size()
                );
            }

            JsonNode removed = array.get(index);
            array.remove(index);

            getChildren().removeIf(child ->
                    child.getNode().equals(removed)
            );
        }

        @Override
        public JSONNode deleteNode(String key) {

            JSONDocument document = getJSONDocument();

            JSONNode nodeToDelete = this.getNodeWithKey(key);

            ObjectNode root = (ObjectNode) document.getRootNode().getNode();
            root.remove(key);


            JSONNode rootNode = document.getRootNode();
            rootNode.setNode(root);


            for (JSONNode child : rootNode.getChildren()) {
                if (nodeToDelete.getNode().equals(child.getNode())) {
                    rootNode.getChildren().remove(child);
                    break;
                }
            }

            return rootNode;
        }


    }


}
