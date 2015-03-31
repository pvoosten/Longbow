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


import static junit.framework.Assert.*;
import static longbow.metadata.MedalMetadata.*;
import longbow.LongbowException;
import longbow.Metadata;
import longbow.TransformationContext;
import longbow.Workflow;
import longbow.core.DefaultWorkflow;
import longbow.transformations.Getter;
import longbow.transformations.Setter;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class MedalMetadataTest {

	private Metadata mockMetadata;

	private Workflow workflow;

	@Before
	public void setUp() {
		mockMetadata = MedalMetadata.acceptAll(); //EasyMock.createMock(Metadata.class);
		workflow = new DefaultWorkflow();
	}

	@Test
	public void testAcceptAll() throws Exception {
		assertBooleanOperation(MedalMetadata.parse("accept all"), true);
	}

	@Test
	public void testAcceptAllMedal() throws Exception {
		assertBooleanOperation("allow all", true);
		assertBooleanOperation("\t\n\r  \tallow\tall\n\r", true);
	}

	@Test
	public void testAcceptNone() throws Exception {
		assertBooleanOperation(MedalMetadata.parse("accept none"), false);
	}

	@Test
	public void testAcceptNoneMedal() throws Exception {
		assertBooleanOperation("disallow all", false);
	}

	@Test
	public void testAndMedal0() throws Exception {
		assertBooleanOperation(parse("disallow all and allow none"), false);
	}

	@Test
	public void testAndMedal1() throws Exception {
		assertBooleanOperation(parse("allow none and allow all "), false);
	}

	@Test
	public void testAndMedal2() throws Exception {
		assertBooleanOperation(parse("disallow none and disallow all"), false);
	}

	@Test
	public void testAndMedal3() throws Exception {
		assertBooleanOperation(parse("disallow none and allow all"), true);
	}

	@Test
	public void testBootstrapMetadata() {
		final Metadata d = BootstrapMetadata.getDataMetadata();
		final Metadata md = BootstrapMetadata.getMetadataMetadata();
		final Metadata b = BootstrapMetadata.getBooleanMetadata();

		assertTrue(b.acceptsMetadata(b));
		assertTrue(md.acceptsMetadata(md));
		assertTrue(d.acceptsMetadata(d));

		assertTrue(d.acceptsData(null));
		assertTrue(d.acceptsData(new Object()));

		assertFalse(md.acceptsData(null));
		assertFalse(md.acceptsData(new Object()));
		assertTrue(md.acceptsData(EasyMock.createNiceMock(Metadata.class)));

		assertFalse(b.acceptsData(null));
		assertFalse(b.acceptsData(new Object()));
		assertTrue(b.acceptsData(true));
	}

	@Test(expected = LongbowException.class)
	public void testEmptyMedalSource() {
		parse("  \t  \n\n\r");
	}

	@Test
	public void testEqualObject() {
		final Object o1 = new Object();
		final Object o2 = new Object();
		assertFalse(o1.equals(o2));
		assertFalse(o2.equals(o1));
		assertNotSame(o1, o2);
	}

	@Test
	public void testMedalAndBrackets() throws Exception {
		assertBooleanOperation("(allow all) and (allow all)", true);
	}

	@Test
	public void testMedalAndNonStatic0() throws Exception {
		assertBooleanOperation(parse("allow none and disallow all"), false);
	}

	@Test
	public void testMedalAndNonStatic2() throws Exception {
		assertBooleanOperation(parse("allow all and allow none"), false);
	}

	@Test
	public void testMedalAndNonStatic3() throws Exception {
		assertBooleanOperation(parse("allow all and allow all"), true);
	}

	@Test
	public void testMedalMetadata() {
		final Metadata former = MedalMetadata.parse("type is java.lang.Integer or not null");
		final Metadata latter = MedalMetadata.parse("type is java.lang.Integer and not null");

		assertFalse(latter.acceptsMetadata(former));
		assertTrue(former.acceptsMetadata(latter));
	}

	@Test
	public void testMedalNandBrackets() throws Exception {
		assertBooleanOperation("(allow none) nand (allow all)", true);
	}

	@Test
	public void testMedalNorBrackets() throws Exception {
		assertBooleanOperation("(allow none) nor (allow none)", true);
	}

	@Test
	public void testMedalNotBrackets() throws Exception {
		assertBooleanOperation("not (allow all)", false);
	}

	@Test
	public void testMedalOrBrackets() throws Exception {
		assertBooleanOperation("(allow none) or (allow all)", true);
	}

	@Test
	public void testMetadataTransformation() {
		final MetadataTransformation t = new MetadataTransformation(new ClassMetadata(String.class));
		final TransformationContext ctxT = workflow.add(t);
		final Setter dataSetter = new Setter(BootstrapMetadata.getDataMetadata());
		final Setter metadataSetter = new Setter(BootstrapMetadata.getMetadataMetadata());
		metadataSetter.inject(BootstrapMetadata.getMetadataMetadata());
		final TransformationContext ctxDataSetter = workflow.add(dataSetter);
		final TransformationContext ctxMetadataSetter = workflow.add(metadataSetter);
		assertTrue("Can't connect ctxDataSetter", workflow.connect(ctxDataSetter, Setter.OUT_DATA, ctxT, MetadataTransformation.IN_DATA));
		assertTrue("Can't connect ctxMetadataSetter", workflow.connect(ctxMetadataSetter, Setter.OUT_DATA, ctxT, MetadataTransformation.IN_METADATA));
		final Getter dataGetter = new Getter(BootstrapMetadata.getBooleanMetadata());
		final Getter acceptsGetter = new Getter(BootstrapMetadata.getBooleanMetadata());
		final Getter isAcceptedGetter = new Getter(BootstrapMetadata.getBooleanMetadata());
		final TransformationContext ctxDataGetter = workflow.add(dataGetter);
		final TransformationContext ctxAcceptsGetter = workflow.add(acceptsGetter);
		final TransformationContext ctxIsAcceptedGetter = workflow.add(isAcceptedGetter);
		assertTrue(workflow.connect(ctxT, MetadataTransformation.OUT_ACCEPT_DATA, ctxDataGetter, Getter.IN_DATA));
		assertTrue(workflow.connect(ctxT, MetadataTransformation.OUT_ACCEPT_METADATA, ctxAcceptsGetter, Getter.IN_DATA));
		assertTrue(workflow.connect(ctxT, MetadataTransformation.OUT_IS_METADATA_ACCEPTED, ctxIsAcceptedGetter, Getter.IN_DATA));

		assertTrue("Can't execute workflow", workflow.execute());

		assertTrue("Can't inject data", dataSetter.inject("hello"));
		Boolean b = (Boolean) dataGetter.extract();
		assertNotNull("Boolean value must be not null", b);
		assertTrue(b);
		dataSetter.inject(new Object());
		b = (Boolean) dataGetter.extract();
		assertNotNull("Boolean value must be not null", b);
		assertFalse(b);

		metadataSetter.inject(new ClassMetadata(Number.class));
		assertFalse((Boolean) acceptsGetter.extract());
		metadataSetter.inject(new ClassMetadata(String.class));
		assertTrue((Boolean) acceptsGetter.extract());
	}

	@Test
	public void testNANDMedal0() throws Exception {
		assertBooleanOperation("disallow all nand disallow all", true);
	}

	@Test
	public void testNANDMedal1() throws Exception {
		assertBooleanOperation("allow none nand allow all", true);
	}

	@Test
	public void testNANDMedal2() throws Exception {
		assertBooleanOperation("allow all nand allow none", true);
	}

	@Test
	public void testNANDMedal3() throws Exception {
		assertBooleanOperation("allow all nand allow all", false);
	}

	@Test
	public void testNORMedal0() throws Exception {
		assertBooleanOperation("allow none nor allow none", true);
	}

	@Test
	public void testNORMedal1() throws Exception {
		assertBooleanOperation("DISALLOW aLL nor ALLow AlL", false);
	}

	@Test
	public void testNORMedal3() throws Exception {
		assertBooleanOperation("allow all nor disallow none", false);
	}

	@Test
	public void testNotAllMedal() throws Exception {
		assertBooleanOperation("not allow all", false);
	}

	@Test
	public void testNotNoneMedal() {
		assertBooleanOperation("not disallow all", true);
	}

	@Test
	public void testOrMedal0() throws Exception {
		assertBooleanOperation("disallow all or disallow all", false);
	}

	@Test
	public void testOrMedal1() throws Exception {
		assertBooleanOperation("allow none or disallow none", true);
	}

	@Test
	public void testOrMedal2() throws Exception {
		assertBooleanOperation("allow all or allow none", true);
	}

	@Test
	public void testOrMedal3() throws Exception {
		assertBooleanOperation("allow all or allow all", true);
	}

	@Test
	public void testToString() {
		fail();
	}

	@Test
	public void testXOrMedal0() throws Exception {
		assertBooleanOperation("disallow all xor disallow all", false);
	}

	@Test
	public void testXOrMedal1() throws Exception {
		assertBooleanOperation("allow none xor disallow none", true);
	}

	@Test
	public void testXOrMedal2() throws Exception {
		assertBooleanOperation("allow all xor allow none", true);
	}

	@Test
	public void testXOrMedal3() throws Exception {
		assertBooleanOperation("allow all xor allow all", false);
	}

	private void assertBooleanOperation(final MedalMetadata processed, final boolean expected) throws AssertionError {
		final boolean acceptData = processed.acceptsData(new Object());
		final boolean acceptMetadata = processed.acceptsMetadata(mockMetadata);
		if (expected) {
			assertTrue(acceptData);
		} else {
			assertFalse(acceptData);
		}
		if (expected) {
			assertTrue(acceptMetadata);
		} else {
			assertFalse(acceptMetadata);
		}
	}

	private void assertBooleanOperation(final String medalScript, final boolean expected) {
		assertBooleanOperation(parse(medalScript), expected);
	}

	private void assertToString(final Metadata metadata, final String expected) throws AssertionError {
		assertEquals("toString() method does not return correct value", expected, metadata.toString());
	}

}
