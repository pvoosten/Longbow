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


import java.io.Reader;
import java.io.StringReader;

import longbow.LongbowException;
import longbow.Metadata;
import longbow.Workflow;
import longbow.transformations.Getter;
import longbow.transformations.Setter;

/**
 * 
 * Medal is a tiny little scripting language that can be used to define
 * {@link Metadata}.
 * 
 * The way the syntax is defined (in Medal.jj, which can be parsed using JavaCC)
 * resembles SQL:
 * <ul>
 * <li>descriptive language</li>
 * <li>case insensitive,</li>
 * <li>ignore whitespace</li>
 * and
 * <li>fairly easy to read.</li>
 * 
 * @author Philip van Oosten
 * 
 */
public class MedalMetadata implements Metadata {

	private static final LeafMetadata acceptAll = new AcceptMetadata(true);

	private static final LeafMetadata notNull = new NotNullMetadata();

	private static final LeafMetadata acceptNone = new AcceptMetadata(false);

	Setter metadataSetter;

	private Getter isMetadataAcceptedGetter;

	@SuppressWarnings("unused")
	private Workflow checker;

	private Setter dataSetter;

	private Getter acceptDataGetter;

	private Getter acceptMetadataGetter;

	/**
	 * Default constructor
	 */
	protected MedalMetadata() {
		// avoid direct instantiation. Static factory methods should be used instead.
		checker = null;
	}

	public boolean acceptsData(final Object data) {
		dataSetter.inject(data);
		return (Boolean) acceptDataGetter.extract();
	}

	public boolean acceptsMetadata(final Metadata other) {
		if (other == this) {
			return true;
		}
		if (!metadataSetter.inject(other)) {
			assert other == null;
			return false;
		}
		return (Boolean) acceptMetadataGetter.extract();
	}

	public Boolean isAcceptedBy(final Metadata metadata) {
		metadataSetter.inject(metadata);
		return !(Boolean) isMetadataAcceptedGetter.extract();
	}

	/**
	 * Returns a Medal representation of this {@link Metadata}.
	 */
	@Override
	public final String toString() {
		final StringBuffer buf = new StringBuffer();
		return buf.toString();
	}

	public static final LeafMetadata acceptAll() {
		return acceptAll;
	}

	public static final LeafMetadata acceptNone() {
		return acceptNone;
	}

	public static final LeafMetadata notNull() {
		return notNull;
	}

	/**
	 * Parses a Medal script and returns a {@link Metadata} object.
	 * 
	 * @param medalScript
	 *            The medal script that defines the requested {@link Metadata}.
	 * @return The parsed {@link MedalMetadata}
	 */
	public static final MedalMetadata parse(final Reader medalScript) {
		final Medal medal = new Medal(medalScript);
		try {
			medal.Parse();
		} catch (final ParseException e) {
			throw new LongbowException("Couldn't parse Medal", e);
		}
		final MedalMetadata metadata = new MedalMetadata();
		metadata.checker = medal.getChecker();
		metadata.dataSetter = medal.getDataSetter();
		metadata.metadataSetter = medal.getMetadataSetter();
		metadata.acceptDataGetter = medal.getAcceptDataGetter();
		metadata.acceptMetadataGetter = medal.getAcceptsMetadataGetter();
		metadata.isMetadataAcceptedGetter = medal.getIsAcceptedGetter();
		return metadata;
	}

	/**
	 * Parses a medal script provides as a {@link String}
	 * 
	 * @param medal
	 *            the Medal script.
	 * @return The {@link Metadata} represented by the medal script.
	 */
	public static final MedalMetadata parse(final String medal) {
		return parse(new StringReader(medal));
	}

	public static LeafMetadata subClassOf(final Class<?> clazz) {
		return new ClassMetadata(clazz);
	}
}
