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

package it.unibo.cs.nmerge;

import it.unibo.cs.ndiff.common.vdom.DOMDocument;
import it.unibo.cs.ndiff.common.vdom.reconstruction.Rtree;
import it.unibo.cs.ndiff.debug.Debug;
import it.unibo.cs.ndiff.exceptions.InputFileException;
import it.unibo.cs.ndiff.metadelta.Operation;
import it.unibo.cs.ndiff.metadelta.AOperation;
import it.unibo.cs.ndiff.metadelta.SOperation;
import it.unibo.cs.ndiff.metadelta.TOperation;
import it.unibo.cs.nmerge.phases.PostProcessing;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Class implementing document reconstruction 
 * @author Mike 
 * 
 */
public class Nmerge {

	/**
	 * Access method to rebuild document
	 * 
	 * @param URIdocA
	 *            path of the document to which apply delta
	 * @param delta
	 *            DOMdocument of the delta to be applied
	 * @throws InputFileException
	 *             Exception raised in case of error in parsing
	 *             the input
	 */
	public static DOMDocument getMergedDocument(String URIdocA,
			DOMDocument delta) throws InputFileException {

		// Logger logger = Logger.getLogger(Nmerge.class);

		// N.B. This kind of settings must be done in the file
		// log4j.properties !
		// Logger.getLogger("vdom.reconstruction.Rtree").setLevel(Level.ERROR);

		// logger.debug("START MERGING OPERATION");

		// Creating Rtree
		Rtree A;
		try {
			A = new Rtree(URIdocA, Boolean.valueOf(delta.root
					.getAttribute("ltrim")), Boolean.valueOf(delta.root
					.getAttribute("rtrim")), Boolean.valueOf(delta.root
					.getAttribute("collapse")), Boolean.valueOf(delta.root
					.getAttribute("emptynode")), Boolean.valueOf(delta.root
					.getAttribute("commentnode")));
		} catch (InputFileException e) {
			throw e;
		}

		NodeList Op = delta.root.getChildNodes();
		Element tmpOp;

		Debug.recostruction_add_step(A, "", "");

		for (int i = 0; i < Op.getLength(); i++) {
			tmpOp = (Element) Op.item(i);

			if (tmpOp.getNodeName().equals(SOperation.DELETE_ELEMENT)) {
				A.DELETE(Integer.valueOf(tmpOp.getAttribute(Operation.NODE_NUMBER_ATTR)),
						Integer.valueOf(tmpOp.getAttribute(SOperation.NODECOUNT_ATTR)), null);

			}else if( tmpOp.getNodeName().equals(SOperation.MOVE_ELEMENT) ){ 
			
				if( tmpOp.getAttribute(SOperation.OPERATION_ATTR).equals(SOperation.MOVEDTO_VALUE) )
					A.DELETE(Integer.valueOf(tmpOp.getAttribute(Operation.NODE_NUMBER_ATTR)),
						Integer.valueOf(tmpOp.getAttribute(SOperation.NODECOUNT_ATTR)),
						tmpOp.getAttribute(SOperation.MOVEID_ATTR));
				
				else if( tmpOp.getAttribute(SOperation.OPERATION_ATTR).equals(SOperation.MOVEDFROM_VALUE) )
					A.INSERT(Integer.valueOf(tmpOp.getAttribute(Operation.NODE_NUMBER_ATTR)),
						Integer.valueOf(tmpOp.getAttribute(SOperation.AT_ATTR)),
						Integer.valueOf(tmpOp.getAttribute(SOperation.POS_ATTR)),
						Integer.valueOf(tmpOp.getAttribute(SOperation.NODECOUNT_ATTR)),
						tmpOp.getFirstChild(),tmpOp.getAttribute(SOperation.MOVEID_ATTR));
			
			}else if (tmpOp.getNodeName().equals(SOperation.UNWRAP_ELEMENT)) {
				A.UNWRAP(Integer.valueOf(tmpOp.getAttribute(Operation.NODE_NUMBER_ATTR)),
					tmpOp.getAttribute(SOperation.NODE_NAME_ATTR));

			} else if (tmpOp.getNodeName().equals(SOperation.INSERT_ELEMENT)) {
				A.INSERT(Integer.valueOf(tmpOp.getAttribute(Operation.NODE_NUMBER_ATTR)),
						Integer.valueOf(tmpOp.getAttribute(SOperation.AT_ATTR)),
						Integer.valueOf(tmpOp.getAttribute(SOperation.POS_ATTR)),
						Integer.valueOf(tmpOp.getAttribute(SOperation.NODECOUNT_ATTR)),
						tmpOp.getFirstChild(), null);

			} else if (tmpOp.getNodeName().equals(SOperation.WRAP_ELEMENT_DELTA)) {

				A.WRAP(Integer.valueOf(tmpOp.getAttribute(Operation.NODE_NUMBER_ATTR)),
						Integer.valueOf(tmpOp.getAttribute(SOperation.AT_ATTR)),
						Integer.valueOf(tmpOp.getAttribute(SOperation.POS_ATTR)),
						Integer.valueOf(tmpOp.getAttribute(SOperation.CHILDREN_ATTR)),
						tmpOp.getAttribute(SOperation.NODE_NAME_ATTR),
						tmpOp.getFirstChild());

			} else if (tmpOp.getNodeName().equals(TOperation.TEXT_NODE_ELEMENT)) {

				if( tmpOp.getAttribute( TOperation.TEXT_OP_ATTR ).equals( TOperation.DELETE_OP_VALUE ) )
					A.DEL(Integer.valueOf(tmpOp.getAttribute( Operation.NODE_NUMBER_ATTR )),
						Integer.valueOf(tmpOp.getAttribute( TOperation.POS_ATTR )),
						Integer.valueOf(tmpOp.getAttribute( TOperation.LENGTH_ATTR )));
						
				else if( tmpOp.getAttribute( TOperation.TEXT_OP_ATTR ).equals( TOperation.INSERT_OP_VALUE ) )
					A.INS(Integer.valueOf(tmpOp.getAttribute( Operation.NODE_NUMBER_ATTR )),
						Integer.valueOf(tmpOp.getAttribute( TOperation.POS_ATTR )),
						Integer.valueOf(tmpOp.getAttribute( TOperation.LENGTH_ATTR )),
						tmpOp.getFirstChild());

			} else if (tmpOp.getNodeName().equals(AOperation.ELEMENT_NAME) ) {
				if( tmpOp.getAttribute(AOperation.OPERATION_ATTR).equals(AOperation.CHANGE_OPERATION) )
						A.ATTCHANGE(Integer.valueOf(tmpOp.getAttribute(Operation.NODE_NUMBER_ATTR)), 
						tmpOp.getAttribute(AOperation.NAME_ATTR), 
						tmpOp.getAttribute(AOperation.NEWVALUE_ATTR),
						tmpOp.getAttribute(AOperation.OLDVALUE_ATTR),
						tmpOp.getAttribute(AOperation.OPERATION_ATTR));
					
				else if( tmpOp.getAttribute(AOperation.OPERATION_ATTR).equals(AOperation.INSERT_OPERATION) )
						A.ATTINSERT(Integer.valueOf(tmpOp.getAttribute(Operation.NODE_NUMBER_ATTR)), 
						tmpOp.getAttribute(AOperation.NAME_ATTR), 
						tmpOp.getAttribute(AOperation.NEWVALUE_ATTR),
						tmpOp.getAttribute(AOperation.OPERATION_ATTR));
					
				else if( tmpOp.getAttribute(AOperation.OPERATION_ATTR).equals(AOperation.DELETE_OPERATION) )			
						A.ATTDELETE(Integer.valueOf(tmpOp.getAttribute(Operation.NODE_NUMBER_ATTR)), 
						tmpOp.getAttribute(AOperation.NAME_ATTR),
						tmpOp.getAttribute(AOperation.OPERATION_ATTR));
			}
			
			Debug.recostruction_add_step(A, tmpOp.getNodeName(),tmpOp.getAttribute(Operation.NODE_NUMBER_ATTR));
		}
				
		//Post Processing
		PostProcessing.detectSplitJoinOperation(A);
	
		return A;
		
	}

	/**
	 * Constructor
	 */
	public Nmerge() {
	}

}
