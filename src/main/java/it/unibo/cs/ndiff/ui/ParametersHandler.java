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

import gnu.getopt.Getopt;
import it.unibo.cs.ndiff.core.Ndiff;
import it.unibo.cs.ndiff.exceptions.ParametersException;
import it.unibo.cs.ndiff.ui.i18n.MessageHandler;

import java.io.File;
import java.text.MessageFormat;

import org.apache.log4j.Logger;

/**
 * @author Cesare Jacopo Corzani
 * 
 */

public class ParametersHandler {
	public static final int DIFF = 1;
	public static final int MERGE = 2;
	public static final int DIFF_MERGE = 3;

	public static final int NO_OPERATION = 0;
	private static final MessageHandler messages = new MessageHandler("textUI");

	public static void checkParameters(Parameters params)
			throws ParametersException {

		// Stampo i parametri
		boolean diff = params.isDiff();
		boolean merge = params.isMerge();
		// boolean stdout = params.isStdout();
		boolean xslt = params.isXslt();
		boolean outputXslt = params.isOutputXslt();
		String configPath = params.getConfigPath();

		String originalPath = params.getOriginalPath();
		String modifiedPath = params.getModifiedPath();
		String deltaPath = params.getDeltaPath();
		String markupPath = params.getMarkupPath();
		String xsltPath = params.getXsltPath();
		String xsltOutputPath = params.getXsltOutputPath();

		// Deve essere abilitata almeno una delle due operazioni

		if (!(diff || merge)) {

			throw new ParametersException(
					messages.getString("ParametersHandler.NO_OPERATION"));
		}

		// Esistenza del file di configurazione

		if (configPath != null && !existFile(configPath)) {

			throw new ParametersException(
					MessageFormat.format(messages.getString("ParametersHandler.CONF_NOT_FOUND") ,configPath));

		}

		// I parametri per la trasformazione xsl devono coesistere

		if (xslt && !merge) {
			throw new ParametersException(
					messages.getString("ParametersHandler.XSL_NOT_SUPPORTED"));

		}

		// Esistenza del file xsl ed eventualmente del file di output

		if (xslt && !existFile(xsltPath)) {

			throw new ParametersException(
					MessageFormat.format(messages.getString("ParametersHandler.XSL_NOT_FOUND"),xsltPath));

		}

		// Se viene specificato l'output su file xsl deve esistere anche la
		// cartella

		if (xslt && outputXslt && !(existFileDir(xsltOutputPath))) {

			throw new ParametersException(
					MessageFormat.format(messages.getString("ParametersHandler.CREATE_DIR_ERR"),xsltOutputPath,new File(xsltOutputPath).getParent()));

		}

		// Il file originale deve esistere per forza

		if (!existFile(originalPath)) {
			throw new ParametersException(
					MessageFormat.format(messages.getString("ParametersHandler.FILE_NOT_FOUND"),originalPath));
		}

		// Modalit?? di Diffing

		switch (getOperation(diff, merge)) {
		case DIFF:

			if (!existFile(modifiedPath))
				throw new ParametersException(MessageFormat.format(messages.getString("ParametersHandler.FILE_NOT_FOUND"),modifiedPath));
			// Se deltapath = null allora l'output ?? lo standart output e non il
			// file, questo vale anche per merge e diff merge
			if (deltaPath != null && !existFileDir(deltaPath))
				throw new ParametersException(
						MessageFormat.format(messages.getString("ParametersHandler.DIR_NOT_FOUND")
								, new File(deltaPath).getParent()));

			break;

		case MERGE:

			if (!existFile(deltaPath))
				throw new ParametersException(
						MessageFormat.format(messages.getString("ParametersHandler.FILE_NOT_FOUND"),deltaPath));

			if (markupPath != null && !existFileDir(markupPath))
				throw new ParametersException(
						MessageFormat.format(messages.getString("ParametersHandler.DIR_NOT_FOUND")
								, new File(markupPath).getParent()));
			break;

		case DIFF_MERGE:
			if (!existFile(modifiedPath))
				throw new ParametersException(
						MessageFormat.format(messages.getString("ParametersHandler.FILE_NOT_FOUND")
								, modifiedPath));

			if (markupPath != null && !existFileDir(markupPath))
				throw new ParametersException(
						MessageFormat.format(messages.getString("ParametersHandler.DIR_NOT_FOUND"),
								new File(markupPath).getParent()));

			break;

		}

	}

	private static boolean existFile(String file) {

		return (new File(file).isFile());

	}

	private static boolean existFileDir(String file) {

		return (new File(file).getAbsoluteFile().getParentFile().isDirectory());

	}

	public static int getOperation(boolean diff, boolean merge) {
		if (diff == true) {

			if (merge == true) {

				return DIFF_MERGE;

			} else {
				return DIFF;
			}

		}

		if (merge == true)
			return MERGE;

		return NO_OPERATION;
	}

	public static Parameters getParameters(String args[])
			throws ParametersException {

		Logger logger = Logger.getLogger(ParametersHandler.class);
		Parameters params = new Parameters();

		Getopt g = new Getopt("jndiff", args, "sdmx:o:c:h");
		int c;
		String arg;
		while ((c = g.getopt()) != -1) {
			switch (c) {
			case 'c':
				arg = g.getOptarg();
				params.setConfigPath(arg);
				break;
			case 'd':
				params.setDiff(true);
				break;

			case 'm':

				params.setMerge(true);
				break;
			case 'o':
				arg = g.getOptarg();

				params.setOutputXslt(true);
				params.setXsltPath(arg);

				break;
			case 's':
				params.setStdout(true);
				break;
			case 'x':
				arg = g.getOptarg();
				params.setXsltPath(arg);
				break;
			case 'h':
				throw new ParametersException(getUsage());
			//
			case '?':
			default:

				throw new ParametersException(
						messages.getString("ParametersHandler.INVALID_PARAMETERS"));
			}
		}

		int textParams = g.getOptind();
		int otherParams = args.length - textParams;
		if (otherParams != -1 && (otherParams == 2 || otherParams == 3)) {
			switch (otherParams) {

			case 3:
				if (!params.isMerge()) {
					params.setDeltaPath(args[args.length - 1]);
				} else {

					params.setMarkupPath(args[args.length - 1]);

				}
				if (params.isDiff()) {
					params.setModifiedPath(args[args.length - 2]);
				} else {

					params.setDeltaPath(args[args.length - 2]);

				}
				params.setOriginalPath(args[args.length - 3]);

				break;
			case 2:
				params.setOriginalPath(args[args.length - 2]);

				if (params.isMerge() && !params.isDiff()) {
					params.setDeltaPath(args[args.length - 1]);
				} else {
					params.setModifiedPath(args[args.length - 1]);

				}

				break;

			}

		} else
			throw new ParametersException(
					messages.getString("ParametersHandler.NUM_PARAMS_ERR"));

		// Stampo i parametri
		logger.debug(messages.getString("Config path")
				+ ((params.getConfigPath() == null) ? "null" : params
						.getConfigPath()));
		logger.debug("Diff=" + params.isDiff());
		logger.debug("Merge=" + params.isMerge());
		logger.debug("Stdout=" + params.isStdout());
		logger.debug("Xslt=" + params.isXslt());
		logger.debug("Original file path=" + params.getOriginalPath());
		logger.debug("Modified file path" + params.getModifiedPath());
		logger.debug("Delta file path=" + params.getDeltaPath());
		logger.debug("Markup file path" + params.getMarkupPath());
		logger.debug("Xslt file path=" + params.getXsltPath());
		logger.debug("Xslt output file=" + params.getXsltOutputPath());

		return params;

	}

	/**
	 * 
	 * 
	 * TODO
	 * 
	 * Prints the usage.
	 */
	public static String getUsage() {

		String usage =
			MessageFormat.format(messages.getString("ParametersHandler.USAGE"),Ndiff.getNdiffVersion());

		return usage;
	}

}
