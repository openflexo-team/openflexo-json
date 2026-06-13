package org.openflexo.ta.json.fml.editionaction;

import org.openflexo.foundation.fml.editionaction.TechnologySpecificActionDefiningReceiver;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.ta.json.JSONModelSlot;
import org.openflexo.ta.json.model.JSONNode;


@ModelEntity(isAbstract = true)
public interface JSONAction<T extends Object> extends TechnologySpecificActionDefiningReceiver<JSONModelSlot, JSONNode, T>  {

}
