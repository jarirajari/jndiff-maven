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

package it.unibo.cs.ndiff.core;

import it.unibo.cs.ndiff.common.vdom.DOMDocument;

import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

/**
 * 
 * Classe che mantiene i parametri di configurazione del Diff
 * 
 * @author Mike
 */
public class Nconfig {

	Logger logger = Logger.getLogger(getClass().getName());

	// attivazione disattivazione delle varie fasi
	public static final int Normalize = 0;
	public static final int Partition = 1;
	public static final int FindMove = 2;
	public static final int FindUpdate = 3;
	public static final int Propagation = 4;

	// Ordine delle fasi e fasi attivate
	public Vector<Integer> phasesOrder = new Vector<Integer>();
	public HashMap<Integer, HashMap<String, String>> phaseParam = new HashMap<Integer, HashMap<String, String>>();

	/**
	 * Costruttore Se non specificata, viene utilizzata la configurazione di
	 * default
	 */
	public Nconfig() {
		setDefault();
	}

	/**
	 * Imposta l'oggetto con i parametri di configurazione contenuti nel file
	 * xml
	 * 
	 * @param XMLconfig
	 *            Percorso del file di configurazione del diff in formato XML
	 */
	public Nconfig(String XMLconfig) {

		try {
			if (XMLconfig != null) {
				DOMDocument cfg = new DOMDocument(XMLconfig);
				cfg.strongNormalize(true, true, true, true, true);

				// Parsing del file di configurazione
				Node slice;
				for (int i = 0; i < cfg.root.getChildNodes().getLength(); i++) {

					slice = cfg.root.getChildNodes().item(i);

					// Parsing della parte relativa alla normalizzazione
					if (slice.getNodeName().equals("normalize")) {
						for (int k = 0; k < slice.getAttributes().getLength(); k++) {
							addPhaseParam(Normalize, slice.getAttributes()
									.item(k).getNodeName(), slice
									.getAttributes().item(k).getNodeValue());
						}
					}

					if (slice.getNodeName().equals("phases")) {
						parseParamPhases(slice);
					}

				}

				// System.out.println(phasesOrder);
				// System.out.println(phaseParam);

			} else {
				setDefault();
			}
		} catch (Exception e) {
			logger.error("Error to load config file :" + e.getMessage());
			setDefault();
		}

	}

	/**
	 * Aggiunge il parametro per una fase
	 * 
	 * @param phase
	 *            Fase per cui aggiungere il parametro
	 * @param param
	 *            Nome del parametro da aggiungere
	 * @param value
	 *            Valore del parametro da aggiungere
	 */
	public void addPhaseParam(Integer phase, String param, String value) {
		if (phaseParam.get(phase) == null)
			phaseParam.put(phase, new HashMap<String, String>());
		phaseParam.get(phase).put(param, value);
	}

	/**
	 * Ritorna il valore di un parametro relativo ad una fase
	 * 
	 * @param phase
	 *            Fase per cui si vuole ottenere il parametro
	 * @param param
	 *            Parametro di cui si vuole conoscere il valore
	 * @return Valore del parametro richiesto
	 */
	public Boolean getBoolPhaseParam(Integer phase, String param) {
		return Boolean.valueOf(phaseParam.get(phase).get(param));
	}

	/**
	 * Ritorna il valore di un parametro relativo ad una fase
	 * 
	 * @param phase
	 *            Fase per cui si vuole ottenere il parametro
	 * @param param
	 *            Parametro di cui si vuole conoscere il valore
	 * @return Valore del parametro richiesto
	 */
	public Integer getIntPhaseParam(Integer phase, String param) {
		return Integer.valueOf(phaseParam.get(phase).get(param));
	}

	/**
	 * Ritorna il valore di un parametro relativo ad una fase
	 * 
	 * @param phase
	 *            Fase per cui si vuole ottenere il parametro
	 * @param param
	 *            Parametro di cui si vuole conoscere il valore
	 * @return Valore del parametro richiesto
	 */
	public String getPhaseParam(Integer phase, String param) {
		return phaseParam.get(phase).get(param);
	}

	/**
	 * Fa il parsing della parte di file relativa alle fasi e alla loro
	 * configurazione
	 * 
	 * @param slice
	 *            Fetta di file di configurazione da parsare per prelevare i
	 *            parametri
	 */
	private void parseParamPhases(Node slice) {

		Node phase;

		for (int i = 0; i < slice.getChildNodes().getLength(); i++) {

			phase = slice.getChildNodes().item(i);

			// Parsing parte relativa alla phase FindMove
			if (phase.getNodeName().equals("FindMove")) {
				phasesOrder.add(FindMove);
				for (int k = 0; k < phase.getAttributes().getLength(); k++) {
					addPhaseParam(FindMove, phase.getAttributes().item(k)
							.getNodeName(), phase.getAttributes().item(k)
							.getNodeValue());
				}
			}

			// Parsing parte relativa alla phase FindUpdate
			else if (phase.getNodeName().equals("FindUpdate")) {
				phasesOrder.add(FindUpdate);
				for (int k = 0; k < phase.getAttributes().getLength(); k++) {
					addPhaseParam(FindUpdate, phase.getAttributes().item(k)
							.getNodeName(), phase.getAttributes().item(k)
							.getNodeValue());
				}
			}

			// Parsing parte relativa alla phase Propagation
			else if (phase.getNodeName().equals("Propagation")) {
				phasesOrder.add(Propagation);
				for (int k = 0; k < phase.getAttributes().getLength(); k++) {
					addPhaseParam(Propagation, phase.getAttributes().item(k)
							.getNodeName(), phase.getAttributes().item(k)
							.getNodeValue());
				}
			}
		}
	}

	/**
	 * Imposta la configurazione di default
	 */
	private void setDefault() {
		logger.info("Loading Default Config");

		addPhaseParam(Normalize, "ltrim", "true");
		addPhaseParam(Normalize, "rtrim", "true");
		addPhaseParam(Normalize, "collapse", "true");
		addPhaseParam(Normalize, "emptynode", "true");
		addPhaseParam(Normalize, "commentnode", "true");

		phasesOrder.add(FindUpdate);
		addPhaseParam(FindUpdate, "level", "50");
		phasesOrder.add(FindMove);
		addPhaseParam(FindMove, "range", "20");
		addPhaseParam(FindMove, "minweight", "10");
		phasesOrder.add(Propagation);
		addPhaseParam(Propagation, "attsimilarity", "50");
		addPhaseParam(Propagation, "forcematch", "false");
	}
}
