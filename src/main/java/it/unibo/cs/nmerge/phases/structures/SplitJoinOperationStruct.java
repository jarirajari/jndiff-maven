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

package it.unibo.cs.nmerge.phases.structures;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**Structure class as to help the postprocessing phase
*
* @author Antonio Cardace
*/
public class SplitJoinOperationStruct{
	
	public Node newNode;
	public Element splittedNode;
	public Integer id;
	
	
	//Constructor
	public SplitJoinOperationStruct(Node newNode, Element splittedNode, int id){
		this.newNode = newNode;
		this.splittedNode = splittedNode;
		this.id = id;
	}
	
}