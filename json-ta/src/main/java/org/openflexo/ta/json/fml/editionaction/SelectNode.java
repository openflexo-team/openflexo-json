package org.openflexo.ta.json.fml.editionaction;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.FetchRequest;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.ta.json.JSONModelSlot;
import org.openflexo.ta.json.model.JSONDocument;
import org.openflexo.ta.json.model.JSONNode;

import java.util.List;

/**
 *
 *
 * @author Chahrazed
 */

@ModelEntity
@ImplementationClass(SelectNode.AbstractSelectNodeImpl.class)
@XMLElement
@FML("SelectNode")
public interface SelectNode extends AbstractSelectNode<List<JSONNode>>, FetchRequest<JSONModelSlot, JSONDocument, JSONNode> {

}