package org.openflexo.ta.json.fml.editionaction;

import java.util.List;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.FetchRequest;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.ta.json.JSONTypedModelSlot;
import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.model.JSONNode;

/** Fetch request returning all JSON individuals matching a JSON Schema type and optional conditions. */
@ModelEntity
@ImplementationClass(AbstractSelectJSONIndividual.AbstractSelectJSONIndividualImpl.class)
@XMLElement
@FML("SelectJSONIndividual")
public interface SelectJSONIndividual
		extends AbstractSelectJSONIndividual<List<JSONNode>>, FetchRequest<JSONTypedModelSlot, JSONDocument, JSONNode> {

}
