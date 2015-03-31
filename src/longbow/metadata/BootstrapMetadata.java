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
import longbow.Workflow;

/**
 * {@link Metadata} for booleans that is used in {@link Workflow}s used to
 * check {@link Metadata}.
 * 
 * @author Philip van Oosten
 * 
 */
abstract class BootstrapMetadata implements Metadata {

	private static final Metadata booleanMetadata = new BooleanMetadata();

	private static final Metadata dataMetadata = new DataMetadata();

	private static final Metadata metadataMetadata = new MetadataMetadata();

	final static Metadata getBooleanMetadata() {
		return booleanMetadata;
	}

	final static Metadata getDataMetadata() {
		return dataMetadata;
	}

	final static Metadata getMetadataMetadata() {
		return metadataMetadata;
	}

	static class BooleanMetadata extends BootstrapMetadata {

		public boolean acceptsData(final Object data) {
			return data != null && data instanceof Boolean;
		}

		public boolean acceptsMetadata(final Metadata other) {
			return other == this;
		}
	}

	/**
	 * Accepts everything
	 */
	static class DataMetadata extends BootstrapMetadata {

		public boolean acceptsData(final Object data) {
			return true;
		}

		public boolean acceptsMetadata(final Metadata other) {
			return other == this;
		}
	}

	/**
	 * Accepts all {@link Metadata} instances;
	 */
	static class MetadataMetadata extends BootstrapMetadata {

		public boolean acceptsData(final Object data) {
			return data != null && data instanceof Metadata;
		}

		public boolean acceptsMetadata(final Metadata other) {
			return other == this;
		}
	}

}
