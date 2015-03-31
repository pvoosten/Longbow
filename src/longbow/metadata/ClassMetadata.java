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

package longbow.metadata;


import longbow.Metadata;

/**
 * Defines the set of all possible objects of type T. null is always accepted.
 * 
 * @author Philip van Oosten
 * 
 */
public class ClassMetadata implements LeafMetadata {

	private final Class<?> acceptedClass;

	/**
	 * @param clazz
	 *            The class of which all instances and all instances of
	 *            subclasses are allowed
	 */
	public ClassMetadata(final Class<?> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		acceptedClass = clazz;
	}

	public boolean acceptsData(final Object data) {
		return data == null || acceptedClass.isAssignableFrom(data.getClass());
	}

	public boolean acceptsMetadata(final Metadata other) {
		if (other instanceof ClassMetadata) {
			final ClassMetadata otherMetadata = (ClassMetadata) other;
			return acceptedClass.isAssignableFrom(otherMetadata.acceptedClass);
		} else if (other instanceof MedalMetadata) {
			final MedalMetadata mmd = (MedalMetadata) other;
			final Boolean accepted = mmd.isAcceptedBy(this);
			return Boolean.TRUE.equals(accepted);
		}
		return false;
	}

	public void toMedal(final StringBuffer buf) {
		buf.append("TYPE IS " + acceptedClass.getCanonicalName());
	}

}
