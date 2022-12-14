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

package it.unibo.cs.ndiff.exceptions;

/**
 * @author Mike Eccezzione sollevata nel caso in cui si ha un problema nello
 *         scrivere il file di output
 */
public class OutputFileException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	String fileName;

	/**
	 * Costruttore
	 * 
	 * @param file
	 *            Nome del file che non si è riusciti a scrivere
	 */
	public OutputFileException(String file) {
		super("File di output " + file + " non valido!");
		fileName = file;

	}

	/**
	 * Ritorna il nome del file
	 * 
	 * @return Nome del file su cui si ha il problema
	 */
	public String getFileName() {
		return fileName;
	}

}
