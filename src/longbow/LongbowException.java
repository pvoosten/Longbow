/*
 * Copyright 2008 Philip van Oosten (Mentoring Systems BVBA)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */

package longbow;


/**
 * Thrown if anything goes wrong in Longbow
 * 
 * @author Philip van Oosten
 * 
 */
public class LongbowException extends RuntimeException {

	private static final long serialVersionUID = 6393647022167700000L;

	/**
	 * @param msg
	 *            The error message
	 */
	public LongbowException(final String msg) {
		super(msg);
	}

	/**
	 * 
	 * @param msg
	 *            The error message
	 * @param cause
	 *            The caught exception
	 */
	public LongbowException(final String msg, final Throwable cause) {
		super(msg, cause);

	}

	/**
	 * 
	 * @param cause
	 *            The caught exception
	 */
	public LongbowException(final Throwable cause) {
		super(cause);
	}

}
