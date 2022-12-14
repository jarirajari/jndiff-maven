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

package it.unibo.cs.ndiff.phases;

import it.unibo.cs.ndiff.common.vdom.diffing.Dtree;
import it.unibo.cs.ndiff.core.Nconfig;
import it.unibo.cs.ndiff.exceptions.ComputePhaseException;
import it.unibo.cs.ndiff.relation.NxN;
import it.unibo.cs.ndiff.relation.Relation;

import org.apache.log4j.Logger;

/**
 * @author Mike Una fase prende in input i riferimenti a: SF : SearchField -
 *         Spazio in cui deve operare la fase R : Relazione tra i nodi presenti
 *         A : Dtree sul documento originale B : Dtree dul documento modificato
 *         cfg: Configurazione di Ndiff
 * 
 *         La fase cambia le varie componenti aggiungendo le infomrazioni che
 *         rileva
 */
public abstract class Phase {

	protected NxN SF;
	protected Relation R;
	protected Dtree A;
	protected Dtree B;
	protected Nconfig cfg;
	protected Logger logger;

	/**
	 * Setta le variabili necessarie alle fasi
	 * 
	 * @param SearchField
	 *            - Campo di ricerca
	 * @param Rel
	 *            - Relazione sui nodi
	 * @param Ta
	 *            - Dtree A
	 * @param Tb
	 *            - Dtree B
	 * @param cfg
	 *            - Configurazione
	 */
	Phase(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb, Nconfig config) {
		SF = SearchField;
		R = Rel;
		A = Ta;
		B = Tb;
		cfg = config;
		logger = Logger.getLogger(getClass().getName());
	}

	/**
	 * Esegue le operazioni della fase
	 * 
	 * @throws ComputePhaseException
	 *             Solleva l'eccezione nel caso si abbia un'errore durante la
	 *             fase
	 */
	abstract void compute() throws ComputePhaseException;

}
