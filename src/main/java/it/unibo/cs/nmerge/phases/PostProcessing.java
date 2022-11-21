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

package it.unibo.cs.nmerge.phases;

import it.unibo.cs.ndiff.common.vdom.reconstruction.Rtree;
import it.unibo.cs.ndiff.metadelta.Operation;
import it.unibo.cs.ndiff.metadelta.TOperation;
import it.unibo.cs.ndiff.metadelta.SOperation;
import it.unibo.cs.nmerge.phases.structures.SplitJoinOperationStruct;

import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/** Class used for a post-processing phase, in which through the analysis of the output of the Merge phase,
 * we try to detect some patterns and eventually markup the split and join operations
 *
 * @author Antonio Cardace
 */
public class PostProcessing{

	public static final String JOIN_ELEMENT = "join";
	public static final String SPLIT_ELEMENT = "split";

	public static final String SPLITID_ATTR = "split-id";
	public static final String JOINID_ATTR = "join-id";
	
	public static final String SPLITTED_VALUE = "splitted";
	public static final String JOINED_VALUE = "joined";
	
	public static final int THRESHOLD = 30;
	
	private static Vector<SplitJoinOperationStruct> splitOp = new Vector<SplitJoinOperationStruct>();
	private static Vector<SplitJoinOperationStruct> joinOp = new Vector<SplitJoinOperationStruct>();
	
	/**
	* Method which looks for patterns of split and join operations
	*
	* @param A	xml Rtree to analyze
	*/
	public static void detectSplitJoinOperation(Rtree A){
		boolean foundSplit,foundJoin;
		int id;
		//get list of nodes marked as ndiff:editing
		NodeList editingNodes = A.DOM.getElementsByTagNameNS(Rtree.NDIFF_NAMESPACE, TOperation.EDITING_VALUE);
		int listLength = 0;
		SplitJoinOperationStruct op = null;
		
		if( editingNodes != null)
			listLength = editingNodes.getLength();
		
		//scroll through the ndiff:editing nodes list
		for( int i=0 ; i < listLength ; i++ ){
				id = i;
				foundSplit = false;
				foundJoin = false;
				Element enode = (Element) editingNodes.item( i );
				Node childNode = enode.getElementsByTagNameNS(Rtree.NDIFF_NAMESPACE, TOperation.TEXT_OPERATION_ELEMENT).item(0);
				Element childElement = (Element) childNode;
				Node parentNode = enode.getParentNode();
				String childText = childNode.getTextContent();
				String previousNodeText = null;
				String nextNodeText = null;
				Element leftSibling = null, rightSibling = null;
					
				if( parentNode.getPreviousSibling() != null )
					if( parentNode.getPreviousSibling().getTextContent() != null ){
						previousNodeText = parentNode.getPreviousSibling().getTextContent();
						leftSibling = (Element) parentNode.getPreviousSibling();
					}
						
				if( parentNode.getNextSibling() != null )
					if( parentNode.getNextSibling().getTextContent() != null ){
						nextNodeText = parentNode.getNextSibling().getTextContent();
						rightSibling = (Element) parentNode.getNextSibling();	
					}
				
				//Detect any SPLIT operation
				if( childElement.getAttribute(Operation.STATUS_ATTR).equals(Operation.DELETED_VALUE) ){
								
					if( nextNodeText != null && !foundSplit )
						if( rightSibling.getAttributeNS(Rtree.NDIFF_NAMESPACE,Operation.STATUS_ATTR).equals(Operation.INSERTED_VALUE) )
							if( childText.indexOf(nextNodeText) > -1 || Math.abs(nextNodeText.trim().compareTo(childText) ) <= childText.length()*THRESHOLD/100 ){
								foundSplit = true;
								op = new SplitJoinOperationStruct( (Node) childElement, (Element) parentNode.getNextSibling(), id );
								splitOp.add( op );
							}
						
					if( previousNodeText != null && !foundSplit )
						if( leftSibling.getAttributeNS(Rtree.NDIFF_NAMESPACE,Operation.STATUS_ATTR).equals(Operation.INSERTED_VALUE) )
							if( childText.indexOf(previousNodeText) > -1 || Math.abs(previousNodeText.trim().compareTo(childText) ) <= childText.length()*THRESHOLD/100){
								foundSplit = true;
								op = new SplitJoinOperationStruct( (Node) childElement, (Element) parentNode.getPreviousSibling(), id );
								splitOp.add( op );
							}
				}
				//detect any JOIN operation
				else if( childElement.getAttribute(Operation.STATUS_ATTR).equals(Operation.INSERTED_VALUE) ){
				
					if( nextNodeText != null && !foundJoin )
						if( rightSibling.getAttributeNS(Rtree.NDIFF_NAMESPACE,Operation.STATUS_ATTR).equals(Operation.DELETED_VALUE) )
							if( childText.indexOf(nextNodeText) > -1 || Math.abs(nextNodeText.trim().compareTo(childText) ) <= childText.length()*THRESHOLD/100 ){
								foundJoin = true;
								op = new SplitJoinOperationStruct( (Node) childElement, (Element) parentNode.getNextSibling(), id);
								joinOp.add( op );
							}
						
					if( previousNodeText != null && !foundJoin )
						if( leftSibling.getAttributeNS(Rtree.NDIFF_NAMESPACE,Operation.STATUS_ATTR).equals(Operation.DELETED_VALUE) )
							if( childText.indexOf(previousNodeText) > -1 || Math.abs(previousNodeText.trim().compareTo(childText) ) <= childText.length()*THRESHOLD/100){
								foundJoin = true;
								op = new SplitJoinOperationStruct( (Node) childElement, (Element) parentNode.getPreviousSibling(), id);
								joinOp.add( op );
							}
				
				}		
		}

		//apply operations
		for( SplitJoinOperationStruct operation : splitOp )
			markupSplitOperation( operation.newNode , operation.splittedNode , operation.id, A );
			
		for( SplitJoinOperationStruct operation : joinOp )
			markupJoinOperation( operation.newNode , operation.splittedNode , operation.id, A);
		
	}
	
	private static void markupSplitOperation(Node newNode, Element splittedNode, Integer id, Rtree A){
		//editing node
		Node editing = newNode.getParentNode();
		//parent of the editing node
		Node parent = editing.getParentNode();
		NodeList changeList = null;
		
		//adopting children
		while( editing.hasChildNodes() )
			parent.insertBefore(editing.getFirstChild(), editing );
		
		//remove editing node
		parent.removeChild(editing);
		
		//markup splittedNode
		splittedNode.setAttributeNS(Rtree.NDIFF_NAMESPACE, Rtree.NDIFF_PREFIX+":"+SPLITID_ATTR, id.toString() );
		splittedNode.setAttributeNS(Rtree.NDIFF_NAMESPACE, Rtree.NDIFF_PREFIX+":"+SOperation.OPERATION_ATTR, SPLIT_ELEMENT);
		
		//markup splitted section
		( (Element) parent).setAttributeNS(Rtree.NDIFF_NAMESPACE, Rtree.NDIFF_PREFIX+":"+Operation.STATUS_ATTR, SPLITTED_VALUE);
		
		changeList = ( (Element) parent).getElementsByTagNameNS( Rtree.NDIFF_NAMESPACE, TOperation.TEXT_OPERATION_ELEMENT );
		
		for(int i=0 ; i< changeList.getLength() ; i++ ){
			A.DOM.renameNode( changeList.item(i), Rtree.NDIFF_NAMESPACE , SPLIT_ELEMENT ); 
			changeList.item(i).setPrefix(Rtree.NDIFF_PREFIX);
			( (Element) changeList.item(i)).setAttribute(SPLITID_ATTR, id.toString() );
		}
		
	}
	
	private static void markupJoinOperation(Node newNode, Element joinedNode, Integer id, Rtree A){
		//editing node
		Node editing = newNode.getParentNode();
		//parent of the editing node
		Node parent = editing.getParentNode();
		NodeList changeList = null;
		
		//adopting children
		while( editing.hasChildNodes() )
			parent.insertBefore(editing.getFirstChild(), editing );
		
		//remove editing node
		parent.removeChild(editing);
		
		//markup parentNode
		( (Element) parent).setAttributeNS(Rtree.NDIFF_NAMESPACE, Rtree.NDIFF_PREFIX+":"+Operation.STATUS_ATTR, JOINED_VALUE);
		
		//markup joinedNode
		joinedNode.setAttributeNS(Rtree.NDIFF_NAMESPACE, Rtree.NDIFF_PREFIX+":"+JOINID_ATTR, id.toString() );
		joinedNode.setAttributeNS(Rtree.NDIFF_NAMESPACE, Rtree.NDIFF_PREFIX+":"+SOperation.OPERATION_ATTR, JOIN_ELEMENT);
		
		//markup joined section
		changeList = ( (Element) parent).getElementsByTagNameNS( Rtree.NDIFF_NAMESPACE, TOperation.TEXT_OPERATION_ELEMENT );
		
		for(int i=0 ; i< changeList.getLength() ; i++ ){
			A.DOM.renameNode( changeList.item(i), Rtree.NDIFF_NAMESPACE , JOIN_ELEMENT ); 
			changeList.item(i).setPrefix(Rtree.NDIFF_PREFIX);
			( (Element) changeList.item(i)).setAttribute(JOINID_ATTR, id.toString() );
		}
		
	}
	
}