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
public class NotTransformation implements Transformation {

	public static final String IN_ACCEPT_DATA = "acceptData";

	public static final String IN_ACCEPT_METADATA = "acceptMetadata";

	public static final String IN_IS_METADATA_ACCEPTED = "isMetadataAccepted";

	public static final String OUT_ACCEPT_DATA = BinaryOperatorTransformation.OUT_ACCEPT_DATA;

	public static final String OUT_ACCEPT_METADATA = BinaryOperatorTransformation.OUT_ACCEPT_METADATA;

	public static final String OUT_IS_METADATA_ACCEPTED = BinaryOperatorTransformation.OUT_IS_METADATA_ACCEPTED;

	private static final Metadata metadata = getBooleanMetadata();

	protected Boolean inAcceptData;

	protected Boolean inAcceptMetadata;

	protected Boolean inIsMetadataAccepted;

	protected Boolean isMetadataAccepted;

	protected Boolean acceptMetadata;

	protected Boolean acceptData;

	private DataWrapper inAcceptDataWrapper;

	private DataWrapper inAcceptMetadataWrapper;

	private DataWrapper inIsMetadataAcceptedWrapper;

	private DataWrapper isMetadataAcceptedWrapper;

	private DataWrapper acceptMetadataWrapper;

	private DataWrapper acceptDataWrapper;

	public void addToContext(final ContextEvent event) {
		final TransformationContext context = event.getSource();
		context.addInput(IN_ACCEPT_DATA, metadata);
		context.addInput(IN_ACCEPT_METADATA, metadata);
		context.addInput(IN_IS_METADATA_ACCEPTED, metadata);
		context.addOutput(OUT_IS_METADATA_ACCEPTED, metadata);
		context.addOutput(OUT_ACCEPT_METADATA, metadata);
		context.addOutput(OUT_ACCEPT_DATA, metadata);
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
		inAcceptData = (Boolean) inAcceptDataWrapper.getData();
		inAcceptMetadata = (Boolean) inAcceptMetadataWrapper.getData();
		inIsMetadataAccepted = (Boolean) inIsMetadataAcceptedWrapper.getData();
	}

	public boolean isExecutable() {
		return true;
	}

	public void processData() {
		acceptData = !inAcceptData;
		acceptMetadata = !inAcceptMetadata;
		isMetadataAccepted = !inIsMetadataAccepted;
	}

	public void startExecution(final TransformationContext context) {
		// initialize outputs
		isMetadataAcceptedWrapper = context.getOutputWrapper(OUT_IS_METADATA_ACCEPTED);
		acceptMetadataWrapper = context.getOutputWrapper(OUT_ACCEPT_METADATA);
		acceptDataWrapper = context.getOutputWrapper(OUT_ACCEPT_DATA);
		// initialize inputs
		inAcceptDataWrapper = context.getInputWrapper(IN_ACCEPT_DATA);
		inAcceptMetadataWrapper = context.getInputWrapper(IN_ACCEPT_METADATA);
		inIsMetadataAcceptedWrapper = context.getInputWrapper(IN_IS_METADATA_ACCEPTED);

	}

	public void stopExecution() {
		inAcceptData = null;
		inAcceptDataWrapper = null;
		inAcceptMetadata = null;
		inAcceptMetadataWrapper = null;
		inIsMetadataAccepted = null;
		inIsMetadataAcceptedWrapper = null;
		isMetadataAccepted = null;
		isMetadataAcceptedWrapper = null;
		acceptMetadata = null;
		acceptMetadataWrapper = null;
		acceptData = null;
		acceptDataWrapper = null;
	}

}
