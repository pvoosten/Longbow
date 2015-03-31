/*
 * Copyright 2007 Philip van Oosten (Mentoring Systems BVBA)
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
 * 
 * @author Philip van Oosten
 * 
 */
public interface Metadata {

	/**
	 * @param data
	 * @return Whether {@code data} is accepted by this
	 */
	boolean acceptsData(Object data);

	/**
	 * @param other
	 * @return whether all possible data that can be accepted by {@code other}
	 *         can also be accepted by this.
	 */
	boolean acceptsMetadata(Metadata other);

}
