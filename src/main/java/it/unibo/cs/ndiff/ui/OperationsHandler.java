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
package it.unibo.cs.ndiff.ui;

import it.unibo.cs.ndiff.common.vdom.DOMDocument;
import it.unibo.cs.ndiff.core.Nconfig;
import it.unibo.cs.ndiff.core.Ndiff;
import it.unibo.cs.ndiff.exceptions.ComputePhaseException;
import it.unibo.cs.ndiff.exceptions.InputFileException;
import it.unibo.cs.ndiff.exceptions.OutputFileException;
import it.unibo.cs.nmerge.Nmerge;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.apache.log4j.Logger;

public class OperationsHandler {
	private static Logger logger = Logger.getLogger(OperationsHandler.class);
	/**
	 * @param params
	 * @throws InputFileException
	 * @throws ComputePhaseException
	 * @throws OutputFileException
	 */
	private static void doDiff(Parameters params) throws InputFileException,
			ComputePhaseException, OutputFileException {
		DOMDocument document;
		document = Ndiff.getDOMDocument(params.getOriginalPath(),
				params.getModifiedPath(), new Nconfig(params.getConfigPath()));

		// Output su Standard output
		if (params.getDeltaPath() == null) {
			document.writeToStream(System.out);
		} else {
			// Se è abilitato lo standard output Stampa anche su
			// stdout...
			if (params.isStdout()) {

				document.writeToStream(System.out);

			}
			// output su file
			document.writeToFile(params.getDeltaPath());
		}
	}

	/**
	 * @param params
	 * @throws InputFileException
	 * @throws FileNotFoundException
	 * @throws OutputFileException
	 */
	private static void doMerge(Parameters params, boolean diff)
			throws InputFileException, FileNotFoundException,
			OutputFileException {
		DOMDocument document, mergedDoc = null;
		document = Nmerge.getMergedDocument(
				params.getOriginalPath(),
				new DOMDocument(diff ? params.getModifiedPath() : params
						.getDeltaPath()));

		if (diff)
			mergedDoc = Nmerge.getMergedDocument(params.getOriginalPath(),
					document);

		// Output su Standard output
		if (params.getMarkupPath() == null) {

			// Se vengono dati 2 parametri in input ovvero <Original> <Ndelta>
			if (params.isXslt()) {
				if (params.isOutputXslt()) {

					// Scrive l'output del merge su stdout e salva la
					// trasformazione xsl su file dato
					(diff ? mergedDoc : document).writeToStream(System.out);
					(diff ? mergedDoc : document).applyXSLT(
							params.getXsltPath(),
							new PrintStream(params.getXsltOutputPath()));

				} else {

					// Scrive la trasformazione direttamente su stdout senza
					// salvare nessun file
					(diff ? mergedDoc : document).applyXSLT(
							params.getXsltPath(), System.out);

				}

			} else {

				// Con 2 parametri dati è implicito il -s
				(diff ? mergedDoc : document).writeToStream(System.out);

			}

		} else {

			// Se vengono dati tutti e 3 i parametri in input <Original>
			// <Ndelta> <Nmarkup>

			// Se è abilitato lo standard output Stampa anche su
			// stdout...

			if (params.isXslt()) {
				if (params.isOutputXslt()) {

					// Merge su file , xslt solo su file

					(diff ? mergedDoc : document).writeToFile(params
							.getMarkupPath());
					(diff ? mergedDoc : document).applyXSLT(
							params.getXsltPath(),
							new PrintStream(params.getXsltOutputPath()));

					// -s ouput anche su stdout
					if (params.isStdout()) {
						(diff ? mergedDoc : document).writeToStream(System.out);
					}

				} else {
					// output su file
					(diff ? mergedDoc : document).writeToFile(params
							.getMarkupPath());

					// -s output anche su stdout
					if (params.isStdout()) {
						(diff ? mergedDoc : document).writeToStream(System.out);
					}
				}

			} else {

				if (params.isStdout()) {

					(diff ? mergedDoc : document).writeToStream(System.out);
				}

				(diff ? mergedDoc : document).writeToStream(new PrintStream(
						params.getMarkupPath()));

			}

		}
	}

	public static void doOperation(Parameters params)
			throws InputFileException, OutputFileException,
			ComputePhaseException, FileNotFoundException {


		try {
			switch (ParametersHandler.getOperation(params.isDiff(),
					params.isMerge())) {

			case ParametersHandler.DIFF:
				doDiff(params);
				break;

			case ParametersHandler.MERGE:
				doMerge(params, false);
				break;

			case ParametersHandler.DIFF_MERGE:
				doMerge(params, true);
				break;

			}
		} catch (InputFileException | OutputFileException | ComputePhaseException | FileNotFoundException e) {
			logger.fatal(e.getMessage(), e);
			throw e;
		}
	}
}
