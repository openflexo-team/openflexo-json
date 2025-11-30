package org.openflexo.ta.json.fml.editionaction;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.fml.validation.BindingIsRequiredAndMustBeValid;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.fml.editionaction.AddClass;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.ta.json.JSONModelSlot;
import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.model.JSONNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ModelEntity
@ImplementationClass(AddNode.AddNodeImpl.class)
@XMLElement
@FML("AddNode")
public interface AddNode extends JSONAction<JSONNode> {

    @PropertyIdentifier(type = DataBinding.class)
    public static final String KEY_KEY = "key";

    @PropertyIdentifier(type = DataBinding.class)
    public static final String CONTENT_KEY = "content";

    @Getter(value = KEY_KEY)
    @XMLAttribute
    @FMLAttribute(value = KEY_KEY, required = false, description = "")

    public DataBinding<String> getKey();

    @Setter(KEY_KEY)
    public void setKey(DataBinding<String> key);

    @Getter(value = CONTENT_KEY)
    @XMLAttribute
    @FMLAttribute(value = CONTENT_KEY, required = false, description = "")
    public DataBinding<String> getContent();

    @Setter(CONTENT_KEY)
    public void setContent(DataBinding<String> content);

    public static abstract class AddNodeImpl extends TechnologySpecificActionDefiningReceiverImpl<JSONModelSlot, JSONDocument, JSONNode>
            implements AddNode {

        private static final Logger logger = Logger.getLogger(AddNode.class.getPackage().getName());

        private DataBinding<String> key;
        private DataBinding<String> content;

        @Override
        public Type getAssignableType() {
            return JSONNode.class;
        }

        @Override
        public JSONNode execute(RunTimeEvaluationContext evaluationContext) {
            String key = null;
            String content = null;
            try {
                key = getKey().getBindingValue(evaluationContext);
                content = getContent().getBindingValue(evaluationContext);
            } catch (TypeMismatchException e) {
                throw new RuntimeException(e);
            } catch (NullReferenceException e) {
                throw new RuntimeException(e);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            JSONDocument resourceData = getReceiver(evaluationContext);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put(key, content);


            ObjectNode root = (ObjectNode) resourceData.getRootNode().getNode();
            root.set(key, node.get(key));
            resourceData.getRootNode().setNode(root);


            return resourceData.getRootNode();

        }

        @Override
        public DataBinding<String> getKey() {
            if (key == null) {
                key = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
                key.setBindingName("key");
            }
            return key;
        }

        @Override
        public void setKey(DataBinding<String> key) {
            if (key != null) {
                key.setOwner(this);
                key.setDeclaredType(String.class);
                key.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
                key.setBindingName("key");
            }
            this.key = key;
        }

        @Override
        public DataBinding<String> getContent() {
            if (content == null) {
                content = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
                content.setBindingName("content");
            }
            return content;
        }

        @Override
        public void setContent(DataBinding<String> content) {
            if (content != null) {
                content.setOwner(this);
                content.setDeclaredType(String.class);
                content.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
                content.setBindingName("content");
            }
            this.content = content;
        }
    }


    @DefineValidationRule
    public static class KeyBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddNode> {
        public KeyBindingIsRequiredAndMustBeValid() {
            super("'Key'_binding_is_required_and_must_be_valid", AddNode.class);
        }

        @Override
        public DataBinding<String> getBinding(AddNode object) {
            return object.getKey();
        }
    }

    @DefineValidationRule
    public static class ContentBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddNode> {
        public ContentBindingIsRequiredAndMustBeValid() {
            super("'content'_binding_is_required_and_must_be_valid", AddNode.class);
        }

        @Override
        public DataBinding<String> getBinding(AddNode object) {
            return object.getContent();
        }
    }
}

