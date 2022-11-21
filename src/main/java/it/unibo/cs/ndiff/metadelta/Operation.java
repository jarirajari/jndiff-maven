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

import org.w3c.dom.Node;

/**
 * Abstract class defining operations which make the METADELTA
 * @author Mike 
 *         
 */
public abstract class Operation {

	public static final byte DELETE_TREE = 0;
	public static final byte INSERT_TREE = 1;

	public static final byte DELETE_NODE = 2;
	public static final byte INSERT_NODE = 3;

	public static final byte MOVE_TO = 4;
	public static final byte MOVE_FROM = 5;

	public static final byte CONTEXT_MOVE_TO = 6;
	public static final byte CONTEXT_MOVE_FROM = 7;

	public static final byte INSERT_TEXT = 8;
	public static final byte DELETE_TEXT = 9;

	public static final byte INSERT_ATT = 10;
	public static final byte DELETE_ATT = 11;
	public static final byte CHANGE_VALUE_ATT = 12;
	
	//Defining a standard name for all common attributes
	public static final String NODE_NUMBER_ATTR = "nodenumber";
	public static final String STATUS_ATTR = "status";
	
	//and for common values as well
	public static final String MODIFIED_VALUE = "modified";
	public static final String INSERTED_VALUE = "inserted";
	public static final String DELETED_VALUE = "deleted";

	public byte type; // Operation type
	public Dnode nodeA; // Vnode referring on the document A operation
	public Dnode nodeB; // Vnode referring on the document B operation

	public Node refContent; // Reference to the Dom node 

	/**
	 * Adds the xml operation structure and links it as 
	 * the last child of the root element of the DOMdocument delta
	 * 
	 * @param delta
	 *            DOMdocument relativo al delta, a cui aggiungere l'operazione
	 */
	abstract public void dump(DOMDocument delta);

	/**
	 * Set base values of the operation
	 * 
	 * @param type
	 *            Operation type
	 * @param nodeA
	 *            Original document node on which some operation applied
	 * @param nodeB
	 *            Modified document node on which some operation applied
	 */
	public void setBaseInfo(byte type, Dnode nodeA, Dnode nodeB) {
		this.type = type;
		this.nodeA = nodeA;
		this.nodeB = nodeB;

	}

	/**
	 * Returns a string as to help the Debug phase, regarding information on the operation
	 * 
	 * @return Returns a string representing the operation(Debug purpose)
	 */
	abstract public String show();

}
