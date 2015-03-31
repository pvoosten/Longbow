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
 * A small wrapper around an object. A {@link DataWrapper} is unaware of run or
 * design mode.
 * 
 * Looking at the interface, it seems like a raw {@link Object} could be used
 * instead, but that is not the case.
 * 
 * A data node must delegate data containment to this interface, because at
 * runtime, it must be impossible for transformations to change anything else
 * than the data of a data node. Passing an object reference to transformations
 * can make using new references to point at objects possible only in three
 * cases:
 * <ol>
 * <li>The data node contains the reference directly</li>
 * but the paragraph above argues against that;
 * <li>Transformations handle wrapping the used reference if the reference can
 * change</li>
 * but that would complicate implementing transformations, because a mechanism
 * would be needed to transport wrappers from one transformation to another, but
 * not always.
 * <li>Always wrap references</li>
 * that is what this interface does. Transformation implementations have the
 * freedom to unwrap the reference if they are certain it will not change during
 * runtime.
 * </ol>
 * 
 * At design time it must be possible to set data in data nodes. At design time,
 * {@link Metadata} must be checked, while it must be omitted at runtime. This
 * interface is not necessary to achieve this.
 * 
 * @author Philip van Oosten
 * 
 */
public interface DataWrapper {

	/**
	 * @return the data contained in this wrapper.
	 */
	Object getData();

	/**
	 * Unconditionally sets the data of this
	 * 
	 * @param data
	 */
	void setData(Object data);
}
