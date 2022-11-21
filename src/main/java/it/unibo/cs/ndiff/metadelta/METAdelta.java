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
import it.unibo.cs.ndiff.core.Nconfig;

import java.util.Vector;

/**
 * @author schirinz Contiene le informazioni che verranno trasformate nel delta
 */
public class METAdelta {

	public final static String NAMESPACE = "http://diff.cs.unibo.it/jndiff";

	// Vettori che rappresentano i tre blocchi di operazioni
	public Vector<Operation> deleteOps = new Vector<Operation>(); // Operazioni
	// di
	// cancellazione
	public Vector<Operation> insertOps = new Vector<Operation>(); // Operazioni
	// di
	// inseirmento
	public Vector<Operation> updateOps = new Vector<Operation>(); // Operazioni

	// di
	// aggiornamento

	/**
	 * Op. di cambio del valore di un attributo
	 *
	 * @param nodeA
	 *            Nodo nel documento originale su cui devono essere modificati
	 *            gli attributi
	 * @param nodeB
	 *            Nodo nel documento modificato con gli attributi modificati
	 * @param attName
	 *            Nome dell'attributo da modificare
	 * @param newValue
	 *            Nuono valore dell'attributo modificato
	 * @param oldValue
	 *            Vecchio valore dell'attributo modificato
	 */
	public void addChangeValueAttOperation(Dnode nodeA, Dnode nodeB,
			String attName, String newValue, String oldValue) {
		updateOps.add(new AOperation(Operation.CHANGE_VALUE_ATT, nodeA, nodeB,
				attName, newValue, oldValue));
	}

	/**
	 * Op. di cancellazione di un attributo
	 *
	 * @param nodeA
	 *            Nodo nel documento originale su cui devono essere modificati
	 *            gli attributi
	 * @param nodeB
	 *            Nodo nel documento modificato con gli attributi modificati
	 * @param attName
	 *            Nome dell'attributo da rimuovere
	 * @param attValue
	 *            Valore dell'attributo che viene rimosso
	 */
	public void addDeleteAttOperation(Dnode nodeA, Dnode nodeB, String attName,
			String attValue) {
		updateOps.add(new AOperation(Operation.DELETE_ATT, nodeA, nodeB,
				attName, null, attValue));
	}

	/**
	 * Op. di cancellazione di un singolo nodo
	 *
	 * @param nodeA
	 *            node che deve essere rimosso dal documento originale
	 */
	public void addDeleteNodeOperation(Dnode nodeA) {
		deleteOps.add(new SOperation(Operation.DELETE_NODE, nodeA, null));
	}

	/**
	 * Op. di cancellazine di testo
	 *
	 * @param nodeA
	 *            Nodo di testo relativo al documento originale su cui deve
	 *            essere effettuata la modifica al contenuto
	 * @param nodeB
	 *            Nodo di testo relativo al documento modificato su cui deve
	 *            essere effettuata la modifica al contenuto
	 * @param pStart
	 *            Offset di partenza per la cancellazione del testo
	 * @param length
	 *            Lunghezza del testo che deve essere rimosso
	 */
	public void addDeleteTextOperation(Dnode nodeA, Dnode nodeB, int pStart,
			int length) {
		updateOps.add(new TOperation(Operation.DELETE_TEXT, nodeA, nodeB,
				pStart, length));
	}

	/**
	 * Op. di cancellazione di un sottoalbero
	 *
	 * @param nodeA
	 *            Nodo relativo alla radice del sottoalbero che deve essere
	 *            rimosso
	 */
	public void addDeleteTreeOperation(Dnode nodeA) {
		deleteOps.add(new SOperation(Operation.DELETE_TREE, nodeA, null));
	}

	/**
	 * Op. di spostamento di contesto di un nodo
	 *
	 * @param indexNodeA
	 *            Indice in VtreeA della radice del sottoalbero spostato
	 * @param indexNodeB
	 *            Indice in VtreeB della radice del sottoalbero spostato
	 * @param indexFatherB
	 *            Indice in VtreeB del nodo padre in VtreeB del sottoalbero
	 *            spostato *
	 */
	/*
	 * public void addContextMoveOperation(Dnode nodeA, Dnode nodeB){
	 * deleteOps.add( new SOperation(Operation.CONTEXT_MOVE_TO,nodeA,nodeB) );
	 * insertOps.add( new SOperation(Operation.CONTEXT_MOVE_FROM,nodeA,nodeB) );
	 * }
	 */

	/**
	 * Op. di inserimento di un attributo
	 *
	 * @param nodeA
	 *            Nodo nel documento originale su cui devono essere modificati
	 *            gli attributi
	 * @param nodeB
	 *            Nodo nel documento modificato con gli attributi modificati
	 * @param attName
	 *            Nome dell'attributo da inserire
	 * @param attValue
	 *            Valore dell'attributo da inserire
	 */
	public void addInsertAttOperation(Dnode nodeA, Dnode nodeB, String attName,
			String attValue) {
		updateOps.add(new AOperation(Operation.INSERT_ATT, nodeA, nodeB,
				attName, attValue, null));
	}

	/**
	 * Op. di inserimento singolo nodo
	 *
	 * @param nodeB
	 *            Nodo relativo al documento B che deve essere inserito
	 */
	public void addInsertNodeOperation(Dnode nodeB) {
		insertOps.add(new SOperation(Operation.INSERT_NODE, null, nodeB));
		(nodeB.getOwnerVtree().getNode(nodeB.posFather)).insOnMe++;
	}

	/**
	 * Op. di inserimento di testo all'interno di un nodo testuale
	 *
	 * @param nodeA
	 *            Nodo di testo relativo al documento originale su cui deve
	 *            essere effettuata la modifica al contenuto
	 * @param nodeB
	 *            Nodo di testo relativo al documento modificato su cui deve
	 *            essere effettuata la modifica al contenuto
	 * @param pStart
	 *            Offset di partenza per l'inserimento del testo
	 * @param length
	 *            Lunghezza del testo che deve essere inserito
	 */
	public void addInsertTextOperation(Dnode nodeA, Dnode nodeB, int pStart,
			int length) {
		updateOps.add(new TOperation(Operation.INSERT_TEXT, nodeA, nodeB,
				pStart, length));
	}

	/**
	 * Op. di inserimento sottoalbero
	 *
	 * @param nodeB
	 *            Nodo relativo alla radice del sottoalbero da inserire nel
	 *            documento B
	 */
	public void addInsertTreeOperation(Dnode nodeB) {
		insertOps.add(new SOperation(Operation.INSERT_TREE, null, nodeB));
		(nodeB.getOwnerVtree().getNode(nodeB.posFather)).insOnMe++;
	}

	/**
	 * Op. di spostamento di un sottoalbero
	 *
	 * @param nodeA
	 *            nodo che rappresenta la radice del sottoalbero da spostare nel
	 *            documento originale
	 * @param nodeB
	 *            nodo che rappresenta la radice del sottoalbero da spostare nel
	 *            documento modificato
	 */
	public void addMoveOperation(Dnode nodeA, Dnode nodeB) {

		boolean find = false;

		// Creo operazione e la inserisco nella giusta posizione fra le
		// cancellazioni
		SOperation tmpOp = new SOperation(Operation.MOVE_TO, nodeA, nodeB);
		tmpOp.IDmove = nodeA.indexKey + "::" + nodeB.indexKey;
		for (int i = 0; i < deleteOps.size() && !find; i++) {
			if (deleteOps.get(i).nodeA.indexKey > nodeA.indexKey) {
				deleteOps.insertElementAt(tmpOp, i);
				find = true;
			}
		}
		if (!find)
			deleteOps.add(tmpOp);

		// Creo operazione e la inserisco nella giusta posizione fra gli
		// inserimenti
		find = false;
		tmpOp = new SOperation(Operation.MOVE_FROM, nodeA, nodeB);
		tmpOp.IDmove = nodeA.indexKey + "::" + nodeB.indexKey;
		for (int i = 0; i < insertOps.size() && !find; i++) {

			if (insertOps.get(i).nodeB.indexKey > nodeB.indexKey) {
				insertOps.insertElementAt(tmpOp, i);
				find = true;
			}
		}
		if (!find)
			insertOps.add(tmpOp);
		(nodeB.getOwnerVtree().getNode(nodeB.posFather)).insOnMe++;
	}

	/**
	 * Aggiunge le operazioni presenti nel METAdelta passato
	 *
	 * @param M
	 *            METAdelta di cui si vogliono aggiungere le operazioni
	 */
	public void merge(METAdelta M) {
		for (int k = 0; k < M.insertOps.size(); k++)
			insertOps.add(M.insertOps.get(k));

		for (int k = 0; k < M.deleteOps.size(); k++)
			deleteOps.add(M.deleteOps.get(k));

		for (int k = 0; k < M.updateOps.size(); k++)
			updateOps.add(M.updateOps.get(k));
	}

	/**
	 * Trasforma le informazioni contenute nel META delta in un DOMDocument
	 *
	 * @return DOMDocument corrispondente alle operazioni presenti nel METAdelta
	 */
	public DOMDocument transformToXML(Nconfig cfg) {

		DOMDocument Ndelta = new DOMDocument(NAMESPACE, "ndiff");

		Ndelta.root.setAttribute("ltrim",
				cfg.getPhaseParam(Nconfig.Normalize, "ltrim"));
		Ndelta.root.setAttribute("rtrim",
				cfg.getPhaseParam(Nconfig.Normalize, "rtrim"));
		Ndelta.root.setAttribute("collapse",
				cfg.getPhaseParam(Nconfig.Normalize, "collapse"));
		Ndelta.root.setAttribute("emptynode",
				cfg.getPhaseParam(Nconfig.Normalize, "emptynode"));
		Ndelta.root.setAttribute("commentnode",
				cfg.getPhaseParam(Nconfig.Normalize, "commentnode"));

		for (int i = 0; i < deleteOps.size(); i++) {
			deleteOps.get(i).dump(Ndelta);
		}

		for (int i = 0; i < insertOps.size(); i++) {
			insertOps.get(i).dump(Ndelta);
		}

		for (int i = 0; i < updateOps.size(); i++) {
			updateOps.get(i).dump(Ndelta);
		}

		Ndelta.DOM.normalizeDocument();

		return Ndelta;
	}

}
