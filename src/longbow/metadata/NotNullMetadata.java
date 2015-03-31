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
 * Accepts any object that is not null.
 * 
 * @author Philip van Oosten
 * 
 */
public class NotNullMetadata implements LeafMetadata {

	public boolean acceptsData(final Object data) {
		return data != null;
	}

	public boolean acceptsMetadata(final Metadata other) {
		final boolean accept;
		if (other instanceof NotNullMetadata) {
			accept = true;
		} else if (other instanceof MedalMetadata) {
			final MedalMetadata mmd = (MedalMetadata) other;
			final Boolean accepted = mmd.isAcceptedBy(this);
			accept = Boolean.TRUE.equals(accepted);
		} else {
			accept = other.acceptsData(null);
		}
		return accept;
	}

	public void toMedal(final StringBuffer buf) {
		buf.append("NOT NULL");
	}

}
