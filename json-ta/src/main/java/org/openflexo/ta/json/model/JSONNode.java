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
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a JSON node inside a {@link JSONDocument}
 *
 * @author sylvain, Chahrazed
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
    @PropertyIdentifier(type = String.class)
    public static final String URI_KEY = "uri";
    @PropertyIdentifier(type = String.class)
    public static final String JSON_POINTER_KEY = "jsonPointer";
    @PropertyIdentifier(type = String.class)
    public static final String KEY_KEY = "key";
    @PropertyIdentifier(type = Integer.class)
    public static final String INDEX_KEY = "index";
    @PropertyIdentifier(type = JSONNode.class)
    public static final String PARENT_KEY = "parent";

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
    @Getter(value = CHILDREN_KEY, cardinality = Cardinality.LIST, inverse = PARENT_KEY)
    @XMLElement
    @Embedded
    @CloningStrategy(StrategyType.CLONE)
    public List<JSONNode> getChildren();

    @Adder(CHILDREN_KEY)
    @PastingPoint
    public void addToChildren(JSONNode aNode);

    @Remover(CHILDREN_KEY)
    public void removeFromChildren(JSONNode aNode);

    @Getter(URI_KEY)
    @XMLAttribute
    public String getURI();

    @Setter(URI_KEY)
    public void setURI(String uri);

    @Getter(JSON_POINTER_KEY)
    @XMLAttribute
    public String getJsonPointer();

    @Setter(JSON_POINTER_KEY)
    public void setJsonPointer(String pointer);

    @Getter(KEY_KEY)
    @XMLAttribute
    public String getKey();

    @Setter(KEY_KEY)
    public void setKey(String key);

    @Getter(INDEX_KEY)
    @XMLAttribute
    public Integer getIndex();

    @Setter(INDEX_KEY)
    public void setIndex(Integer index);

    @Getter(value = PARENT_KEY, inverse = CHILDREN_KEY)
    public JSONNode getParent();

    @Setter(PARENT_KEY)
    public void setParent(JSONNode parent);

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
        @SuppressWarnings("unchecked")
        public List<JSONNode> getChildren() {
            return (List<JSONNode>) performSuperGetter(CHILDREN_KEY);
        }

        @Override
        public JSONNode getNodeWithKey(String key) {
            if (key == null || key.isEmpty()) return null;

            for (JSONNode child : getChildren()) {
                if (key.equals(child.getKey())) {
                    return child;
                }
            }

            for (JSONNode child : getChildren()) {
                JSONNode found = child.getNodeWithKey(key);
                if (found != null) {
                    return found;
                }
            }
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

            createNode(key, value);
        }

        @Override
        public JSONNode getArrayElementAt(int index) {
            JsonNode n = getNode();
            if (n == null || !n.isArray()) {
                throw new IllegalStateException("Not an array node");
            }

            if (index < 0 || index >= getChildren().size()) {
                return null;
            }
            return getChildren().get(index);
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
            JSONNode previous = directChildWithKey(key);
            if (previous != null) {
                getJSONDocument().unregisterNodeAndChildren(previous);
                removeFromChildren(previous);
            }
            objectNode.set(key, jsonValue);

            JSONNode created = getFactory().makeJSONNode(jsonValue, this, key, null, true);
            addToChildren(created);
            changed();

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

            JSONNode previous = directChildWithKey(key);
            if (previous != null) {
                getJSONDocument().unregisterNodeAndChildren(previous);
                removeFromChildren(previous);
            }
            JSONNode created = getFactory().makeJSONNode(objectValue, this, key, null, true);
            addToChildren(created);
            changed();

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

            JSONNode previous = directChildWithKey(key);
            if (previous != null) {
                getJSONDocument().unregisterNodeAndChildren(previous);
                removeFromChildren(previous);
            }
            JSONNode created = getFactory().makeJSONNode(arrayValue, this, key, null, true);
            addToChildren(created);
            changed();

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

            JSONNode created = getFactory().makeJSONNode(jsonValue, this, null, array.size() - 1, true);
            addToChildren(created);
            changed();

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

            JSONNode removed = getChildren().get(index);
            array.remove(index);
            getJSONDocument().unregisterNodeAndChildren(removed);
            removeFromChildren(removed);
            changed();
        }

        @Override
        public JSONNode deleteNode(String key) {

            if (!(getNode() instanceof ObjectNode)) {
                throw new IllegalStateException("Cannot delete the node");
            }
            JSONNode nodeToDelete = directChildWithKey(key);
            if (nodeToDelete == null) {
                return this;
            }
            ((ObjectNode) getNode()).remove(key);
            getJSONDocument().unregisterNodeAndChildren(nodeToDelete);
            removeFromChildren(nodeToDelete);
            changed();
            return this;
        }

        private JSONNode directChildWithKey(String key) {
            for (JSONNode child : getChildren()) {
                if (key.equals(child.getKey())) {
                    return child;
                }
            }
            return null;
        }

        private void changed() {
            getJSONDocument().cleanJsonContent();
            getJSONDocument().recalculateIndex();
        }


    }


}
