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
import it.unibo.cs.ndiff.metadelta.METAdelta;
import it.unibo.cs.ndiff.relation.Fragment;
import it.unibo.cs.ndiff.relation.Interval;
import it.unibo.cs.ndiff.relation.NxN;
import it.unibo.cs.ndiff.relation.Relation;

import java.util.Vector;

/**
 * @author schirinz Deriva dalle informazioni rilevate nell fasi precedenti, un
 *         set di operazioni che rappresentano i cambiamenti da effetuare sul
 *         documento originale per ottenere il documento modificato
 */
public class DeltaDerive extends Phase {

	METAdelta Ndelta = new METAdelta();

	/**
	 * Costruttore
	 * 
	 * @param SearchField
	 *            Campi di ricerca rimasti in NxN
	 * @param Rel
	 *            Relazioni che sono state rilevate tra i nodi dei documenti
	 * @param Ta
	 *            Dtree relativo al documento originale
	 * @param Tb
	 *            Dtree relativo al documento modificato
	 * @param cfg
	 *            Nconfig relativo alla configurazione del Diff
	 */
	public DeltaDerive(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
			Nconfig cfg) {
		super(SearchField, Rel, Ta, Tb, cfg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ndiff.phases.Phase#compute()
	 */
	@Override
	public void compute() throws ComputePhaseException {

		try {

			logger.info("START");

			// Calcolo le operazioni di cancellazione
			Vector<Interval> dom = SF.getIntervalsOnX();
			Interval toProcess;
			for (int i = 0; i < dom.size(); i++) {
				toProcess = dom.get(i);

				for (int k = toProcess.inf; k <= toProcess.sup; k++) {
					if (k + A.getNode(k).getNumChildSubtree() <= toProcess.sup) {
						Ndelta.addDeleteTreeOperation(A.getNode(k));
						k += A.getNode(k).getNumChildSubtree();
					} else
						Ndelta.addDeleteNodeOperation(A.getNode(k));
				}
			}

			// Calcolo le operazioni di inserimento
			Vector<Interval> cod = SF.getIntervalsOnY();
			for (int i = 0; i < cod.size(); i++) {
				toProcess = cod.get(i);

				for (int k = toProcess.inf; k <= toProcess.sup; k++) {
					if (k + B.getNode(k).getNumChildSubtree() <= toProcess.sup) {
						Ndelta.addInsertTreeOperation(B.getNode(k));
						k += B.getNode(k).getNumChildSubtree();
					} else
						Ndelta.addInsertNodeOperation(B.getNode(k));
				}

			}

			// Calcolo le operazioni si spostamento
			Vector<Fragment> tmpFrag = R.getFragments(Relation.MOVE);
			if (tmpFrag != null)
				for (int i = 0; i < tmpFrag.size(); i++) {
					Ndelta.addMoveOperation(
							A.getNode(tmpFrag.get(i).getNnRootA()),
							B.getNode(tmpFrag.get(i).getNnRootB()));
				}

			// Calcolo le operazioni di aggiornamento
			tmpFrag = R.getFragments(Relation.UPDATE);
			if (tmpFrag != null)
				for (int i = 0; i < tmpFrag.size(); i++) {
					Ndelta.merge((A.getNode(tmpFrag.get(i).getNnRootA()))
							.getDeltaLikeness(tmpFrag.get(i).getNnRootB()));
				}

			logger.info("END");
		} catch (Exception e) {
			throw new ComputePhaseException("DeltaDerive");
		}
	}

	/**
	 * Calcola il METAdelta e lo restituisce
	 * 
	 * @return METAdelta calcolato
	 * @throws ComputePhaseException
	 *             Solleva l'eccezione nel caso in cui si hanno problemi durante
	 *             la trasformazione
	 */
	public METAdelta derive() throws ComputePhaseException {
		compute();
		return Ndelta;
	}
}
