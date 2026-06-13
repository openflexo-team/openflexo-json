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
            return "";
        }
        String parentPointer = ((JSONNode) parent).getJsonPointer();
        String token = key != null ? escape(key) : String.valueOf(index);
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
            String token = childKey != null ? escape(childKey) : String.valueOf(arrayIndex);
            updateSubtree(document, child, pointer + "/" + token, childKey, arrayIndex);
            childIndex++;
        }
    }

    private static String escape(String token) {
        return token.replace("~", "~0").replace("/", "~1");
    }
}
