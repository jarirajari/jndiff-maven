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

import it.unibo.cs.ndiff.debug.Debug;
import it.unibo.cs.ndiff.exceptions.ParametersException;

import org.apache.log4j.Logger;

/**
 * 
 * Versione del main riscritta con il supporto getOpt e la gestione degli stream
 * 
 * @author Cesare Jacopo Corzani
 * 
 */
public class Main {

	static Logger logger = Logger.getLogger(Main.class);

	public static void main(String args[]) {
		boolean debugEnabled = Debug.flag;

		if (args.length == 0) {
			System.err.print(ParametersHandler.getUsage());
			System.exit(1);

		}

		Parameters params = null;
		try {
			params = ParametersHandler.getParameters(args);

			ParametersHandler.checkParameters(params);
		} catch (ParametersException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

		if (debugEnabled) Debug.start();

		try {
			OperationsHandler.doOperation(params);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			System.out.println();
		}

		if (debugEnabled) Debug.close();
	}
}