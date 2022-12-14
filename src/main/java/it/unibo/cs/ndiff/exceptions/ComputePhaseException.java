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
 * @author Mike Eccezione sollevata nel caso in cui ci sono dei problemi nel
 *         calcolo di una fase
 */
public class ComputePhaseException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	/**
	 * Costruttore
	 * 
	 * @param phase
	 *            Nome della fase in cui si ha l'errore
	 */
	public ComputePhaseException(String phase) {
		super("Errore durante il calcolo della fase " + phase);
	}

}
