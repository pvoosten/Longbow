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

package longbow.core;


import longbow.DataNode;
import longbow.Input;
import longbow.Metadata;
import longbow.TransformationContext;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class DefaultInput extends DefaultPort implements Input {

	private DataNode connection;

	public DefaultInput(final TransformationContext context, final Metadata metadata) {
		super(context, metadata);
	}

	public DataNode getConnection() {
		return connection;
	}

	@Override
	public boolean isConnected() {
		return connection != null;
	}

	public void setConnection(final DataNode datanode) {
		if (datanode == null) {
			connection.inputDisconnected();
		} else {
			datanode.inputConnected();
		}
		connection = datanode;
	}
}
