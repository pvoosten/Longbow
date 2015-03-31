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
abstract class BinaryOperatorTransformation implements Transformation {

	public static final String IN_FORMER_ACCEPT_DATA = "formerAcceptData";

	public static final String IN_FORMER_ACCEPT_METADATA = "formerAcceptMetadata";

	public static final String IN_FORMER_IS_METADATA_ACCEPTED = "formerIsMetadataAccepted";

	public static final String IN_LATTER_IS_METADATA_ACCEPTED = "latterIsMetadataAccepted";

	public static final String IN_LATTER_ACCEPT_METADATA = "latterAcceptMetadata";

	public static final String IN_LATTER_ACCEPT_DATA = "latterAcceptData";

	public static final String OUT_ACCEPT_METADATA = "acceptMetadata";

	public static final String OUT_IS_METADATA_ACCEPTED = "isMetadataAccepted";

	public static final String OUT_ACCEPT_DATA = "acceptData";

	private static final Metadata formerAcceptDataMetadata = getBooleanMetadata();

	private static final Metadata latterIsMetadataAcceptedMetadata = getBooleanMetadata();

	private static final Metadata formerAcceptMetadataMetadata = getBooleanMetadata();

	private static final Metadata latterAcceptMetadataMetadata = getBooleanMetadata();

	private static final Metadata latterAcceptDataMetadata = getBooleanMetadata();

	private static final Metadata formerIsMetadataAcceptedMetadata = getBooleanMetadata();

	private static final Metadata isMetadataAcceptedMetadata = getBooleanMetadata();

	private static final Metadata acceptMetadataMetadata = getBooleanMetadata();

	private static final Metadata acceptDataMetadata = getBooleanMetadata();

	protected Boolean formerAcceptData;

	protected Boolean formerAcceptMetadata;

	protected Boolean formerIsMetadataAccepted;

	protected Boolean latterIsMetadataAccepted;

	protected Boolean latterAcceptMetadata;

	protected Boolean latterAcceptData;

	protected Boolean isMetadataAccepted;

	protected Boolean acceptMetadata;

	protected Boolean acceptData;

	private DataWrapper formerAcceptDataWrapper;

	private DataWrapper latterIsMetadataAcceptedWrapper;

	private DataWrapper formerAcceptMetadataWrapper;

	private DataWrapper latterAcceptMetadataWrapper;

	private DataWrapper latterAcceptDataWrapper;

	private DataWrapper formerIsMetadataAcceptedWrapper;

	private DataWrapper isMetadataAcceptedWrapper;

	private DataWrapper acceptMetadataWrapper;

	private DataWrapper acceptDataWrapper;

	public void addToContext(final ContextEvent event) {
		final TransformationContext context = event.getSource();
		context.addInput(IN_FORMER_ACCEPT_DATA, formerAcceptDataMetadata);
		context.addInput(IN_LATTER_IS_METADATA_ACCEPTED, latterIsMetadataAcceptedMetadata);
		context.addInput(IN_FORMER_ACCEPT_METADATA, formerAcceptMetadataMetadata);
		context.addInput(IN_LATTER_ACCEPT_METADATA, latterAcceptMetadataMetadata);
		context.addInput(IN_LATTER_ACCEPT_DATA, latterAcceptDataMetadata);
		context.addInput(IN_FORMER_IS_METADATA_ACCEPTED, formerIsMetadataAcceptedMetadata);
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
		formerAcceptData = (Boolean) formerAcceptDataWrapper.getData();
		latterIsMetadataAccepted = (Boolean) latterIsMetadataAcceptedWrapper.getData();
		formerAcceptMetadata = (Boolean) formerAcceptMetadataWrapper.getData();
		latterAcceptMetadata = (Boolean) latterAcceptMetadataWrapper.getData();
		latterAcceptData = (Boolean) latterAcceptDataWrapper.getData();
		formerIsMetadataAccepted = (Boolean) formerIsMetadataAcceptedWrapper.getData();
	}

	public boolean isExecutable() {
		return true;
	}

	abstract public void processData();

	public void startExecution(final TransformationContext context) {
		// initialize outputs
		isMetadataAcceptedWrapper = context.getOutputWrapper(OUT_IS_METADATA_ACCEPTED);
		acceptMetadataWrapper = context.getOutputWrapper(OUT_ACCEPT_METADATA);
		acceptDataWrapper = context.getOutputWrapper(OUT_ACCEPT_DATA);
		// initialize inputs
		formerAcceptDataWrapper = context.getInputWrapper(IN_FORMER_ACCEPT_DATA);
		latterIsMetadataAcceptedWrapper = context.getInputWrapper(IN_LATTER_IS_METADATA_ACCEPTED);
		formerAcceptMetadataWrapper = context.getInputWrapper(IN_FORMER_ACCEPT_METADATA);
		latterAcceptMetadataWrapper = context.getInputWrapper(IN_LATTER_ACCEPT_METADATA);
		latterAcceptDataWrapper = context.getInputWrapper(IN_LATTER_ACCEPT_DATA);
		formerIsMetadataAcceptedWrapper = context.getInputWrapper(IN_FORMER_IS_METADATA_ACCEPTED);

	}

	public void stopExecution() {
		formerAcceptData = null;
		formerAcceptDataWrapper = null;
		latterIsMetadataAccepted = null;
		latterIsMetadataAcceptedWrapper = null;
		formerAcceptMetadata = null;
		formerAcceptMetadataWrapper = null;
		latterAcceptMetadata = null;
		latterAcceptMetadataWrapper = null;
		latterAcceptData = null;
		latterAcceptDataWrapper = null;
		formerIsMetadataAccepted = null;
		formerIsMetadataAcceptedWrapper = null;
		isMetadataAccepted = null;
		isMetadataAcceptedWrapper = null;
		acceptMetadata = null;
		acceptMetadataWrapper = null;
		acceptData = null;
		acceptDataWrapper = null;
	}

}
