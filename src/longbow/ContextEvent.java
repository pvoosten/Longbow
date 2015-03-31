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


import java.util.EventObject;

/**
 * A {@link ContextEvent} can only occur at {@link Mode#DESIGN} time. It signals
 * that a designer has performed an action that caused the surroundings of a
 * {@link TransformationContext} to change.
 * 
 * @see TransformationContext
 * @see ContextListener
 * 
 * @author Philip van Oosten
 * 
 */
public class ContextEvent extends EventObject {

	private static final long serialVersionUID = -6440418454505425928L;

	/**
	 * @param source
	 */
	public ContextEvent(final TransformationContext source) {
		super(source);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TransformationContext getSource() {
		return (TransformationContext) super.getSource();
	}

}
