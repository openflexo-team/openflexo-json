package org.openflexo.ta.json.fml.editionaction;

import org.openflexo.foundation.fml.rt.error.FMLNullReferenceError;
import org.openflexo.foundation.fml.rt.error.FMLEvaluation;
import org.openflexo.foundation.fml.rt.error.FMLExecutionException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.fml.validation.BindingIsRequiredAndMustBeValid;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.ta.json.JSONModelSlot;
import org.openflexo.ta.json.model.JSONNode;


/**
 *
 * Delete a field from the JSON object supplied as receiver.
 *
 * @author Chahrazed
 */

@ModelEntity
@ImplementationClass(DeleteNode.DeleteNodeImpl.class)
@FML("DeleteNode")
public interface DeleteNode extends JSONAction<JSONNode> {

    @PropertyIdentifier(type = DataBinding.class)
    String KEY_KEY = "key";

    @Getter(KEY_KEY)
    @FMLAttribute(value = KEY_KEY, required = false, description = "JSON field name to delete")
    DataBinding<String> getKey();

    @Setter(KEY_KEY)
    void setKey(DataBinding<String> key);

    /**
     * Implementation of DeleteNode.
     */
    abstract class DeleteNodeImpl extends
            TechnologySpecificActionDefiningReceiverImpl<JSONModelSlot, JSONNode, JSONNode>
            implements DeleteNode {

        private static final Logger logger = Logger.getLogger(DeleteNode.class.getPackage().getName());

        private DataBinding<String> key;

        @Override
        public Type getAssignableType() {
            return JSONNode.class;
        }

        @Override
        public JSONNode execute(RunTimeEvaluationContext evaluationContext) throws FMLExecutionException {
            String keyToDelete = FMLEvaluation.evaluate(getKey(), evaluationContext, this);
            JSONNode receiver = getReceiver(evaluationContext);
            if (receiver == null) {
                throw new FMLNullReferenceError("DeleteNode: receiver evaluated to null");
            }
            return receiver.deleteNode(keyToDelete);
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
    }

    /**
     * Validation: key binding must be valid.
     */
    @DefineValidationRule
    class KeyBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<DeleteNode> {
        public KeyBindingIsRequiredAndMustBeValid() {
            super("'key'_binding_is_required_and_must_be_valid", DeleteNode.class);
        }

        @Override
        public DataBinding<String> getBinding(DeleteNode object) {
            return object.getKey();
        }
    }
}
