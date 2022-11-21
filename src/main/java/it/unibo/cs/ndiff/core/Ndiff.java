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
import it.unibo.cs.ndiff.common.vdom.diffing.Dtree;
import it.unibo.cs.ndiff.core.alternatives.ThreadedBuildDtree;
import it.unibo.cs.ndiff.debug.Debug;
import it.unibo.cs.ndiff.exceptions.ComputePhaseException;
import it.unibo.cs.ndiff.exceptions.InputFileException;
import it.unibo.cs.ndiff.exceptions.OutputFileException;
import it.unibo.cs.ndiff.metadelta.METAdelta;
import it.unibo.cs.ndiff.phases.DeltaDerive;
import it.unibo.cs.ndiff.phases.FindMove;
import it.unibo.cs.ndiff.phases.FindUpdate;
import it.unibo.cs.ndiff.phases.Partition;
import it.unibo.cs.ndiff.phases.Propagation;
import it.unibo.cs.ndiff.relation.Field;
import it.unibo.cs.ndiff.relation.NxN;
import it.unibo.cs.ndiff.relation.Relation;
import it.unibo.cs.ndiff.ui.ParametersHandler;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

/**
 * Classe principale per il calcolo del Diff
 * 
 * Per la procedura di diffing è necessario richiamare il metodo factory come
 * segue
 * 
 * DOMDocument document =
 * Ndiff.getDOMDocument("File originale.xml","File modificato.xml",(Nconfig)
 * <file di configurazione>);
 * 
 * Così facendo viene generato un documento (document) con le differenze tra i
 * file dati.
 * 
 * Successivamente si può scrivere il documento generato su stream o su file.
 * 
 * Scrittura su standard output: document.writeToStream(System.out);
 * 
 * Scrittura su file sia come stringa:
 * 
 * document.writeToFile("output.xml");
 * 
 * Che come oggetto File:
 * 
 * document.writeToFile(new File("output.xml"));
 * 
 * @author schirinz
 * @author Cesare Jacopo Corzani
 */
public class Ndiff {

	public static DOMDocument getDOMDocument(String URIdocA, String URIdocB)
			throws InputFileException, ComputePhaseException {
		Nconfig config = new Nconfig();
		return getDOMDocument(URIdocA, URIdocB, config);
	}

	/**
	 * Restituisce la versione di Jndiff scritta nel manifest
	 * 
	 * @return Stringa contentente la versione di jndiff
	 */
	
public static String getNdiffVersion(){
	
	String version = ParametersHandler.class.getPackage().getImplementationVersion();
	return version != null ? version : "custom-" + Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();
	
}
	
	/**
	 * Calcola il diff tra i due documenti e restituisce il DOMDocument
	 * corrispondente al delta. N.B. La costruzione degli alberi DTree non sono
	 * in parallelo
	 * 
	 * @param URIdocA
	 *            Percorso del documento originale da confrontare
	 * @param URIdocB
	 *            Percorso del documento modificato da confrontare
	 * @param config
	 *            Configurazione di ndiff
	 * @return DOMdocument relativo al delta ottenuto
	 */
	public static DOMDocument getDOMDocument(String URIdocA, String URIdocB,
			Nconfig config) throws InputFileException, ComputePhaseException {

		return getDOMDocument(URIdocA, URIdocB, config, false);

	}

	/**
	 * Calcola il diff tra i due documenti e restituisce il DOMDocument
	 * corrispondente al delta.
	 * E' possibile richiamare il metodo senza il parametro config in modo che 
	 * venga usata la configurazione di default 
	 * 
	 * @param URIdocA
	 *            Percorso del documento originale da confrontare
	 * @param URIdocB
	 *            Percorso del documento modificato da confrontare
	 * @param config
	 *            Configurazione di ndiff
	 * @param enableThread
	 *            Costruisce i due alberi di DTree in parallelo
	 * 
	 * @return DOMdocument relativo al delta ottenuto
	 */
	public static DOMDocument getDOMDocument(String URIdocA, String URIdocB,
			Nconfig config, boolean enableThread) throws InputFileException,
			ComputePhaseException {

		Dtree a;
		Dtree b;
		NxN SearchField;
		Relation R;
		METAdelta Delta;
	
		// Inizializza le strutture dati

		Logger.getLogger("vdom.diffing.Dtree").setLevel(Level.ERROR);
		Logger.getLogger("ndiff.TXTdiff").setLevel(Level.ERROR);
		Logger.getLogger("ndiff.relation.NxN").setLevel(Level.ERROR);
		Logger.getLogger("ndiff.phases.Partition").setLevel(Level.ERROR);
		Logger.getLogger("ndiff.phases.FindMove").setLevel(Level.ERROR);
		Logger.getLogger("ndiff.phases.FindUpdate").setLevel(Level.ERROR);
		Logger.getLogger("ndiff.phases.Propagation").setLevel(Level.ERROR);
		Logger.getLogger("ndiff.phases.DeltaDerive").setLevel(Level.ERROR);

		// logger.info("START");

		// Parametri di Nconfig
		boolean ltrim = config.getBoolPhaseParam(Nconfig.Normalize, "ltrim");
		boolean rtrim = config.getBoolPhaseParam(Nconfig.Normalize, "rtrim");
		boolean collapse = config.getBoolPhaseParam(Nconfig.Normalize,
				"collapse");
		boolean emptynode = config.getBoolPhaseParam(Nconfig.Normalize,
				"emptynode");
		boolean commentNode = config.getBoolPhaseParam(Nconfig.Normalize,
				"commentnode");

		// Costruzione delle struttura dati necessarie al confronto, con o senza
		// l'ultilizzo di thread

		if (enableThread) {

			Dtree[] tempTree = (new ThreadedBuildDtree()).getDTree(URIdocA,
					URIdocB, ltrim, rtrim, collapse, emptynode, commentNode);

			a = tempTree[0];
			b = tempTree[1];

		} else {

			a = new Dtree(URIdocA, ltrim, rtrim, collapse, emptynode,
					commentNode);

			b = new Dtree(URIdocB, ltrim, rtrim, collapse, emptynode,
					commentNode);

		}

		SearchField = new NxN(a.count() - 1, b.count() - 1);
		R = new Relation();

		Debug.diffing_normalize(a, b, R, SearchField);

		// La radice deve essere uguale per forza
		SearchField.subPoint(0, 0, Field.LOCALITY, Field.LOCALITY,
				Field.LOCALITY, Field.LOCALITY);
		R.addFragment(0, 0, 1, Relation.EQUAL);
		a.getNode(0).inRel = Relation.EQUAL;
		b.getNode(0).inRel = Relation.EQUAL;

		// Calcolo delle varie fasi
		new Partition(SearchField, R, a, b, config).compute();
		Debug.diffing_partition(a, b, R, SearchField);

		for (int i = 0; i < config.phasesOrder.size(); i++) {

			switch (config.phasesOrder.get(i)) {

			case Nconfig.FindMove:
				new FindMove(SearchField, R, a, b, config).compute();
				Debug.diffing_findmove(a, b, R, SearchField);
				break;

			case Nconfig.FindUpdate:
				new FindUpdate(SearchField, R, a, b, config).compute();
				Debug.diffing_findupdate(a, b, R, SearchField);
				break;

			case Nconfig.Propagation:
				new Propagation(SearchField, R, a, b, config).compute();
				Debug.diffing_propagation(a, b, R, SearchField);
				break;
			}
		}

		// Derivazione del delta
		Delta = new DeltaDerive(SearchField, R, a, b, config).derive();

		/*
		 * Debug.status(SearchField, "../debug/SearchField.html");
		 * Debug.status(R, "../debug/Relation.html"); Debug.status(Delta,
		 * "../debug/Mdelta.html");
		 */

		return Delta.transformToXML(config);
	}

	Logger logger = Logger.getLogger(getClass());

	Nconfig config = null;

	protected DOMDocument document = null;

	/**
	 * Costruttore
	 */
	public Ndiff() {
		this.config = new Nconfig();
	}

	/**
	 * Costruttore
	 * 
	 * @param configPath
	 *            File XML relativo alla configurazione dell'algoritmo
	 */
	public Ndiff(String configPath) {
		this.config = new Nconfig(configPath);

	}

	/**
	 * Calcola il diff tra docA e docB e salva il delta in URIdelta Non mantiene
	 * in memoria l'albero DOM generato Mantenuto per retrocompatibilita
	 * 
	 * @param URIdocA
	 *            Percorso del documento originale da confrontare
	 * @param URIdocB
	 *            Percorso del documento modificato da confrontare
	 * @param URIdelta
	 *            Percorso in cui salvare il file XML relativo al delta
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void diff(String URIdocA, String URIdocB, String URIdelta)
			throws InputFileException, OutputFileException,
			ComputePhaseException {
		getDOMDocument(URIdocA, URIdocB, config).writeToFile(URIdelta);
	}

}