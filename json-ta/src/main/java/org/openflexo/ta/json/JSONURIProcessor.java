package org.openflexo.ta.json;

import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.model.JSONNode;
import org.openflexo.ta.json.model.JSONObject;


/**
 *
 * Maps stable OpenFlexo JSON wrappers to JSON Pointer based URIs.
 *
 * @author Chahrazed
 */

public final class JSONURIProcessor {

    private JSONURIProcessor() {
    }

    public static String computePointer(JSONObject parent, String key, Integer index) {
        if (!(parent instanceof JSONNode)) {
            if (key != null || index != null) {
                throw new IllegalArgumentException("The JSON root cannot have a key or an index");
            }
            return "";
        }

        JSONNode parentNode = (JSONNode) parent;
        String token;
        if (parentNode.getNode().isObject()) {
            if (key == null || index != null) {
                throw new IllegalArgumentException("A child of a JSON object must have a key and no index");
            }
            token = escape(key);
        }
        else if (parentNode.getNode().isArray()) {
            if (key != null || index == null || index < 0) {
                throw new IllegalArgumentException("A child of a JSON array must have a non-negative index and no key");
            }
            token = String.valueOf(index);
        }
        else {
            throw new IllegalArgumentException("A primitive JSON node cannot have children");
        }

        String parentPointer = parentNode.getJsonPointer();
        return (parentPointer != null ? parentPointer : "") + "/" + token;
    }

    public static String toURI(JSONDocument document, String pointer) {
        return "#" + (pointer != null ? pointer : "");
    }

    public static String getURIForObject(JSONDocument document, JSONNode node) {
        if (node == null) {
            return null;
        }
        if (node.getURI() == null) {
            node.setURI(toURI(document, node.getJsonPointer()));
            document.registerNode(node);
        }
        return node.getURI();
    }

    public static JSONNode retrieveObjectWithURI(JSONDocument document, String uri) {
        if (document == null || uri == null) {
            return null;
        }
        JSONNode returned = document.getNodeByURI(uri);
        if (returned == null && uri.startsWith("#")) {
            returned = document.getNodeByURI(toURI(document, uri.substring(1)));
        }
        return returned;
    }

    public static void rebuildIndex(JSONDocument document) {
        updateSubtree(document, document.getRootNode(), "", null, null);
    }

    private static void updateSubtree(JSONDocument document, JSONNode node, String pointer, String key, Integer index) {
        node.setJSONDocument(document);
        node.setKey(key);
        node.setIndex(index);
        node.setJsonPointer(pointer);
        node.setURI(toURI(document, pointer));
        document.registerNode(node);

        int childIndex = 0;
        for (JSONNode child : node.getChildren()) {
            child.setParent(node);
            String childKey = node.getNode().isObject() ? child.getKey() : null;
            Integer arrayIndex = node.getNode().isArray() ? childIndex : null;
            String childPointer = computePointer(node, childKey, arrayIndex);
            updateSubtree(document, child, childPointer, childKey, arrayIndex);
            childIndex++;
        }
    }

    private static String escape(String token) {
        return token.replace("~", "~0").replace("/", "~1");
    }
}
