/*****************************************************************************************
 *
 *   This file is part of JNdiff project.
 *
 *   JNdiff is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   JNdiff is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU LESSER GENERAL PUBLIC LICENSE for more details.

 *   You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *   along with JNdiff; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *   
 *****************************************************************************************/

package it.unibo.cs.ndiff.metadelta;

import it.unibo.cs.ndiff.common.vdom.DOMDocument;
import it.unibo.cs.ndiff.common.vdom.diffing.Dnode;

import org.w3c.dom.Element;

/**
 * Wrapper for operations on the document's structure
 * @author schirinz 
 */
public class SOperation extends Operation {

	String IDmove;
	
	//all Elements and Attributes names gathered all here to modularize everything
	public final static String UNWRAP_ELEMENT = "unwrap"; //same for delta
	public final static String WRAP_ELEMENT_DELTA = "wrap";
	public final static String INSERT_ELEMENT = "insert";
	public final static String DELETE_ELEMENT = "delete";
	public final static String MOVE_ELEMENT = "move";
	
	public final static String NODE_NAME_ATTR = "nodename";
	public final static String AT_ATTR = "at";
	public final static String NODECOUNT_ATTR = "nodecount";
	public final static String POS_ATTR = "pos";
	public final static String CHILDREN_ATTR = "children";
	public final static String ID_ATTR = "id";
	public final static String IDREF_ATTR = "idref";
	public final static String OPERATION_ATTR = "op";
	public final static String MOVEID_ATTR = "move";
	
	public final static String WRAP_OP_VALUE = "wrapping";
	public static final String MOVEDTO_VALUE = "movedTo";
	public static final String MOVEDFROM_VALUE = "movedFrom";
	

	/**
	 * Costructor
	 * 
	 * @param type
	 *            operation type
	 * @param indexNodeA
	 *            Node's index of the operation in the A Vtree
	 * @param indexNodeB
	 *            Node's index of the operation in the B Vtree
	 * @param indexFatherB
	 *            Index of the parent node which the operation is referring to
	 */
	/**
	 * Costructor
	 * 
	 * @param type
	 *            Operation Type
	 * @param nodeA
	 *            Original document node 
	 * @param nodeB
	 *            Modified document node
	 */
	public SOperation(byte type, Dnode nodeA, Dnode nodeB) {
		setBaseInfo(type, nodeA, nodeB);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ndiff.metadelta.Operation#dump(vdom.DOMDocument)
	 */
	@Override
	public void dump(DOMDocument Ndelta) {

		Element newOp;

		switch (type) {

		case Operation.INSERT_TREE:
			newOp = Ndelta.DOM.createElement(INSERT_ELEMENT);
			newOp.setAttribute(Operation.NODE_NUMBER_ATTR, nodeB.indexKey.toString());
			newOp.setAttribute(AT_ATTR, nodeB.posFather.toString());
			newOp.setAttribute(POS_ATTR, nodeB.posLikeChild.toString());
			newOp.setAttribute(NODECOUNT_ATTR, nodeB.numChildSubtree.toString());
			// newOp.appendChild(Ndelta.DOM.adoptNode(
			// nodeB.refDomNode.cloneNode(true)));
			newOp.appendChild(Ndelta.DOM.importNode(nodeB.refDomNode, true));
			Ndelta.root.appendChild(newOp);
			break;

		case Operation.INSERT_NODE:
			newOp = Ndelta.DOM.createElement(SOperation.WRAP_ELEMENT_DELTA);
			newOp.setAttribute(Operation.NODE_NUMBER_ATTR, nodeB.indexKey.toString());
			newOp.setAttribute(AT_ATTR, nodeB.posFather.toString());
			newOp.setAttribute(POS_ATTR, nodeB.posLikeChild.toString());
			newOp.setAttribute(NODE_NAME_ATTR, nodeB.refDomNode.getNodeName() );
			newOp.setAttribute(CHILDREN_ATTR, ((Integer) (nodeB.refDomNode
					.getChildNodes().getLength() - nodeB.insOnMe)).toString());
			// newOp.appendChild(Ndelta.DOM.adoptNode(
			// nodeB.refDomNode.cloneNode(false)));
			newOp.appendChild(Ndelta.DOM.importNode(nodeB.refDomNode, false));
			Ndelta.root.appendChild(newOp);
			break;

		case Operation.DELETE_TREE:
			newOp = Ndelta.DOM.createElement(DELETE_ELEMENT);
			newOp.setAttribute(Operation.NODE_NUMBER_ATTR, nodeA.indexKey.toString());
			newOp.setAttribute(NODECOUNT_ATTR,
					((Integer) (nodeA.numChildSubtree + 1)).toString());
			// newOp.appendChild(Ndelta.DOM.adoptNode(
			// nodeA.refDomNode.cloneNode(true)));
			newOp.appendChild(Ndelta.DOM.importNode(nodeA.refDomNode, true));
			Ndelta.root.appendChild(newOp);
			break;

		case Operation.DELETE_NODE:
			newOp = Ndelta.DOM.createElement(UNWRAP_ELEMENT);
			newOp.setAttribute(Operation.NODE_NUMBER_ATTR, nodeA.indexKey.toString());
			newOp.setAttribute(NODE_NAME_ATTR, nodeA.refDomNode.getNodeName() );
			// newOp.appendChild(Ndelta.DOM.adoptNode(
			// nodeA.refDomNode).cloneNode(false));
			newOp.appendChild(Ndelta.DOM.importNode(nodeA.refDomNode, false));
			Ndelta.root.appendChild(newOp);
			break;

		case Operation.MOVE_TO:
			newOp = Ndelta.DOM.createElement(MOVE_ELEMENT);
			newOp.setAttribute(Operation.NODE_NUMBER_ATTR, nodeA.indexKey.toString());
			newOp.setAttribute(NODECOUNT_ATTR,
					((Integer) (nodeA.numChildSubtree + 1)).toString());
			newOp.setAttribute(MOVEID_ATTR, IDmove);
			newOp.setAttribute(OPERATION_ATTR, MOVEDTO_VALUE);
			// newOp.appendChild(Ndelta.DOM.adoptNode(
			// nodeA.refDomNode.cloneNode(true)));
			newOp.appendChild(Ndelta.DOM.importNode(nodeA.refDomNode, true));
			Ndelta.root.appendChild(newOp);
			break;

		/*
		 * The move operation is complex. Theoretically moving something
		 * is equal to deleting a fragment from document A  and add
		 * something to the document B, but if the move operation is combined with an update operation
		 * on the same fragment, the inserted bit, is the A document part but in the position of
		 * B document
		 */
		case Operation.MOVE_FROM:
			newOp = Ndelta.DOM.createElement(MOVE_ELEMENT);
			newOp.setAttribute(Operation.NODE_NUMBER_ATTR, nodeB.indexKey.toString());
			newOp.setAttribute(AT_ATTR, nodeB.posFather.toString());
			newOp.setAttribute(POS_ATTR, nodeB.posLikeChild.toString());
			newOp.setAttribute(NODECOUNT_ATTR, nodeB.numChildSubtree.toString());
			newOp.setAttribute(MOVEID_ATTR, IDmove);
			newOp.setAttribute(OPERATION_ATTR, MOVEDFROM_VALUE);
			// newOp.appendChild(Ndelta.DOM.adoptNode(
			// nodeA.refDomNode.cloneNode(true)));
			newOp.appendChild(Ndelta.DOM.importNode(nodeA.refDomNode, true));
			Ndelta.root.appendChild(newOp);
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ndiff.metadelta.Operation#show()
	 */
	@Override
	public String show() {
		String ret = "";

		switch (type) {

		case Operation.DELETE_TREE:
			ret = "DeleteTree  indexNodeA:" + nodeA.indexKey.toString();
			break;

		case Operation.INSERT_TREE:
			ret = "InsertTree  indexNodeB:" + nodeB.indexKey.toString()
					+ "  IndexFatherB:" + nodeB.posFather.toString();
			break;

		case Operation.DELETE_NODE:
			ret = "DeleteNode  indexNodeA:" + nodeA.indexKey.toString();
			break;

		case Operation.INSERT_NODE:
			ret = "InsertNode  indexNodeB:" + nodeB.indexKey.toString()
					+ "  IndexFatherB:" + nodeB.posFather.toString();
			break;

		case Operation.MOVE_TO:
			ret = "MoveTo   indexNodeA:" + nodeA.indexKey.toString()
					+ "  indexNodeB:" + nodeB.indexKey.toString();
			break;

		case Operation.MOVE_FROM:
			ret = "MoveFrom   indexNodeA:" + nodeA.indexKey.toString()
					+ "  indexNodeB:" + nodeB.indexKey.toString()
					+ "  IndexFatherB:" + nodeB.posFather.toString();
			break;

		}

		return ret;
	}

}
