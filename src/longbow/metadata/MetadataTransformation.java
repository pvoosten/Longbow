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


import static longbow.metadata.BootstrapMetadata.*;
import longbow.ContextEvent;
import longbow.DataWrapper;
import longbow.Metadata;
import longbow.Transformation;
import longbow.TransformationContext;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class MetadataTransformation implements Transformation {

	public static final String IN_DATA = "data";

	public static final String IN_METADATA = "metadata";

	public static final String OUT_IS_METADATA_ACCEPTED = BinaryOperatorTransformation.OUT_IS_METADATA_ACCEPTED;

	public static final String OUT_ACCEPT_METADATA = BinaryOperatorTransformation.OUT_ACCEPT_METADATA;

	public static final String OUT_ACCEPT_DATA = BinaryOperatorTransformation.OUT_ACCEPT_DATA;

	private static final Metadata dataMetadata = getDataMetadata();

	private static final Metadata metadataMetadata = getMetadataMetadata();

	private static final Metadata isMetadataAcceptedMetadata = getBooleanMetadata();

	private static final Metadata acceptMetadataMetadata = getBooleanMetadata();

	private static final Metadata acceptDataMetadata = getBooleanMetadata();

	private DataWrapper dataWrapper;

	private Object data;

	private DataWrapper metadataWrapper;

	private Metadata metadata;

	private DataWrapper isMetadataAcceptedWrapper;

	private Boolean isMetadataAccepted;

	private DataWrapper acceptMetadataWrapper;

	private Boolean acceptMetadata;

	private DataWrapper acceptDataWrapper;

	private Boolean acceptData;

	private final Metadata thisMetadata;

	public MetadataTransformation(final Metadata metadata) {
		thisMetadata = metadata;
	}

	public void addToContext(final ContextEvent event) {
		final TransformationContext context = event.getSource();
		context.addInput(IN_DATA, dataMetadata);
		context.addInput(IN_METADATA, metadataMetadata);
		context.addOutput(OUT_IS_METADATA_ACCEPTED, isMetadataAcceptedMetadata);
		context.addOutput(OUT_ACCEPT_METADATA, acceptMetadataMetadata);
		context.addOutput(OUT_ACCEPT_DATA, acceptDataMetadata);
	}

	public void contextChange(final ContextEvent event) {
	}

	public void contextRemoved(final ContextEvent event) {
	}

	public void exportData() {
		isMetadataAcceptedWrapper.setData(isMetadataAccepted);
		acceptMetadataWrapper.setData(acceptMetadata);
		acceptDataWrapper.setData(acceptData);
	}

	public void importData() {
		data = dataWrapper.getData();
		metadata = (Metadata) metadataWrapper.getData();
	}

	public boolean isExecutable() {
		return true;
	}

	public void processData() {
		acceptData = thisMetadata.acceptsData(data);
		acceptMetadata = thisMetadata.acceptsMetadata(metadata);
		if (metadata instanceof MedalMetadata) {
			final MedalMetadata mm = (MedalMetadata) metadata;
			isMetadataAccepted = !mm.isAcceptedBy(thisMetadata);
		} else {
			isMetadataAccepted = !metadata.acceptsMetadata(thisMetadata);
		}
	}

	public void startExecution(final TransformationContext context) {
		// initialize outputs
		isMetadataAcceptedWrapper = context.getOutputWrapper(OUT_IS_METADATA_ACCEPTED);
		acceptMetadataWrapper = context.getOutputWrapper(OUT_ACCEPT_METADATA);
		acceptDataWrapper = context.getOutputWrapper(OUT_ACCEPT_DATA);
		// initialize inputs
		dataWrapper = context.getInputWrapper(IN_DATA);
		metadataWrapper = context.getInputWrapper(IN_METADATA);
	}

	public void stopExecution() {
		data = null;
		dataWrapper = null;
		metadata = null;
		metadataWrapper = null;
		isMetadataAccepted = null;
		isMetadataAcceptedWrapper = null;
		acceptMetadata = null;
		acceptMetadataWrapper = null;
		acceptData = null;
		acceptDataWrapper = null;
	}
}
