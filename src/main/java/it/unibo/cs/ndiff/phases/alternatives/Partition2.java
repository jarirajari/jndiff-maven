/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.cs.ndiff.phases.alternatives;

import it.unibo.cs.ndiff.common.vdom.diffing.Dnode;
import it.unibo.cs.ndiff.common.vdom.diffing.Dtree;
import it.unibo.cs.ndiff.core.Nconfig;
import it.unibo.cs.ndiff.phases.Partition;
import it.unibo.cs.ndiff.phases.common.Match;
import it.unibo.cs.ndiff.relation.Interval;
import it.unibo.cs.ndiff.relation.NxN;
import it.unibo.cs.ndiff.relation.Relation;

/**
 * 
 * @author yac
 */
public class Partition2 extends Partition {

	public Partition2(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
			Nconfig cfg) {

		super(SearchField, Rel, Ta, Tb, cfg);
	}

	/**
	 * Parte dall'inizio degli intervalli e va avanti finche' il contenuto dei
	 * nodi e uguale, si ferma quando arriva alla fine degli intervalli
	 * 
	 * @param intA
	 *            Intervallo di ricerca sul documento A
	 * @param intB
	 *            Intervallo di ricerca sul documento B
	 * @return Il match trovato tra i due intervalli
	 */

	@Override
	protected Match explorer(Interval intA, Interval intB) {
		Match m = new Match(0, 0);

		while (true) {
			int limitA = intA.inf + m.length;
			int limitB = intA.inf + m.length;
			Dnode nodeA = A.getNode(intA.inf + m.length);
			Dnode nodeB = B.getNode(intB.inf + m.length);

			if ((limitA <= intA.sup)
					&& (limitA + nodeA.getNumChildSubtree() <= intA.sup)
					&& (limitB <= intB.sup)
					&& (limitB + nodeB.getNumChildSubtree() <= intB.sup)
					&& ((nodeA.getHashTree()).equals(nodeB.getHashTree()))) {

				m.weight += nodeA.getWeight();
				m.length += nodeB.getNumChildSubtree() + 1;
			} else
				break;

		}

		// Tolgo ultimo incremento del while
		m.length--;
		return m;
	}

}
