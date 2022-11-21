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
import it.unibo.cs.ndiff.relation.Field;
import it.unibo.cs.ndiff.relation.Interval;
import it.unibo.cs.ndiff.relation.NxN;
import it.unibo.cs.ndiff.relation.Relation;

/**
 * @author Mike Fase che implementa la ricerca degli spostamenti all'interno dei
 *         documenti
 * 
 */
public class FindMove extends Phase {

	Integer range;
	Integer minweight;

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
	public FindMove(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
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
			// Calcolo valore di distanza relativo al file d configurazione
			range = (A.numNode + B.numNode) / 2;
			range = (range * cfg.getIntPhaseParam(Nconfig.FindMove, "range")) / 100;
			minweight = cfg.getIntPhaseParam(Nconfig.FindMove, "minweight");
			logger.info("START with range:" + range + " minweight:" + minweight);

			Interval findA = new Interval(1, 0);
			Interval findB = new Interval(1, 0);

			Field processField;
			SF.StartFieldProcess(Field.NO);
			while ((processField = SF.nextField()) != null) {

				logger.info("FindMove(Call)   \t X:" + processField.xRef.show()
						+ " \t Y:" + processField.yRef.show());

				findA.set(1, 0);
				findB.set(1, 0);

				FindMaxMove(processField.xRef, processField.yRef, findA, findB);

				logger.info("FindMove(Return)\t X:" + findA.show() + "\t Y:"
						+ findB.show());

				if (findA.size() > 0) {
					// Togli i nodi dallo spazio di ricerca
					SF.subField(findA, findB, Field.NO, Field.NO, Field.NO,
							Field.NO);
					// Inserisci i nodi nella struttura Relation
					R.addFragment(findA, findB, A.getNode(findA.inf).weight,
							Relation.MOVE);

					// Cambia appartenenza dei nodi
					for (int i = findA.inf; i <= findA.sup; i++)
						A.getNode(i).inRel = Relation.MOVE;
					for (int i = findB.inf; i <= findB.sup; i++)
						B.getNode(i).inRel = Relation.MOVE;
				}
			}
			logger.info("END");
		} catch (Exception e) {
			throw new ComputePhaseException("FindMove");
		}
	}

	/**
	 * Cerca eventuali sottoalberi spostati nello spazio definito da intA e intB
	 * 
	 * @param intA
	 *            Intervallo di ricerca sul documento Originale
	 * @param intB
	 *            Intervallo di ricerca sul documento Modificato
	 * @param findA
	 *            Match trovato sul documento originale
	 * @param findB
	 *            Match trovato sul documento modificato
	 */
	private void FindMaxMove(Interval intA, Interval intB, Interval findA,
			Interval findB) {

		for (int i = intA.inf; i <= intA.sup; i++)
			for (int j = intB.inf; j <= intB.sup; j++) {

				if ((Math.abs(i - j) <= range)
						&& (A.getNode(i).weight >= minweight)
						&& A.getNode(i).getHashTree()
								.equals(B.getNode(j).getHashTree())
						&& (i + A.getNode(i).getNumChildSubtree() <= intA.sup)
						&& (j + B.getNode(j).getNumChildSubtree() <= intB.sup)) {

					if (A.getNode(i).getNumChildSubtree() > findA.size()) {
						findA.set(i, i + A.getNode(i).getNumChildSubtree());
						findB.set(j, j + B.getNode(j).getNumChildSubtree());
					}
				}

			}
	}

}
