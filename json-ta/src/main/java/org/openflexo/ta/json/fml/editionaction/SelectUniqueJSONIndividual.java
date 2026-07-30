package org.openflexo.ta.json.fml.editionaction;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.UniqueFetchRequest;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.ta.json.JSONTypedModelSlot;
import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.model.JSONNode;

/** Fetch request returning the first JSON individual matching a JSON Schema type and optional conditions. */
@ModelEntity
@ImplementationClass(AbstractSelectJSONIndividual.AbstractSelectJSONIndividualImpl.class)
@XMLElement
@FML("SelectUniqueJSONIndividual")
public interface SelectUniqueJSONIndividual
		extends AbstractSelectJSONIndividual<JSONNode>, UniqueFetchRequest<JSONTypedModelSlot, JSONDocument, JSONNode> {

}
