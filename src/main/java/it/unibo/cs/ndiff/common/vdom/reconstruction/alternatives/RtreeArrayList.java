/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.cs.ndiff.common.vdom.reconstruction.alternatives;

import it.unibo.cs.ndiff.exceptions.InputFileException;

import java.util.ArrayList;

/**
 * 
 * Alternativa alla classe Rtree, usa degli ArrayList al posto dei Vector Per
 * ora è utilizzata solo nei test delle performance
 * 
 */
public class RtreeArrayList extends
		it.unibo.cs.ndiff.common.vdom.reconstruction.Rtree {

	public ArrayList<Integer> editingNode = new ArrayList<Integer>();

	public RtreeArrayList(String fileXML, boolean ltrim, boolean rtrim,
			boolean collapse, boolean emptynode, boolean commentnode)
			throws InputFileException {
		super(fileXML, ltrim, rtrim, collapse, emptynode, commentnode);

	}

}
