package longbow.metadata;


import static junit.framework.Assert.*;
import longbow.Metadata;

import org.junit.Before;
import org.junit.Test;

public class ClassMetadataTest {

	Metadata wide, narrow, other;

	Metadata wideMedal, narrowMedal, otherMedal;

	@Test
	public void acceptSame() {
		assertTrue(wide.acceptsMetadata(wide));
		assertTrue(narrow.acceptsMetadata(narrow));
		assertTrue(other.acceptsMetadata(other));
	}

	@Before
	public void setup() {
		wide = new ClassMetadata(Number.class);
		narrow = new ClassMetadata(Integer.class);
		other = new ClassMetadata(String.class);
		wideMedal = MedalMetadata.parse("class is java.lang.Number");
		narrowMedal = MedalMetadata.parse("class is java.lang.Integer");
		otherMedal = MedalMetadata.parse("class is java.lang.String");
	}

	@Test
	public void testAcceptClassMedal() {
		assertTrue(wide.acceptsMetadata(narrowMedal));
	}

	@Test
	public void testAcceptClassMetadata() {
		assertTrue(wide.acceptsMetadata(narrow));
	}

	@Test
	public void testAcceptMedalClass() {
		assertTrue(wideMedal.acceptsMetadata(narrow));
	}

	@Test
	public void testAcceptSameClassMedal() {
		assertTrue(wide.acceptsMetadata(wideMedal));
		assertTrue(narrow.acceptsMetadata(narrowMedal));
	}

	@Test
	public void testAcceptSameMedalClass() {
		assertTrue(wideMedal.acceptsMetadata(wide));
		assertTrue(narrowMedal.acceptsMetadata(narrow));
	}

	@Test
	public void testNotAcceptClassMedal() {
		assertFalse(narrow.acceptsMetadata(wideMedal));
	}

	@Test
	public void testNotAcceptClassMetadata() {
		assertFalse(narrow.acceptsMetadata(wide));
	}

	@Test
	public void testNotAcceptMedalClass() {
		assertFalse(narrowMedal.acceptsMetadata(wide));
	}

	@Test
	public void testOtherClassMedal() {
		assertFalse(wide.acceptsMetadata(otherMedal));
	}

	@Test
	public void testOtherMedalClass() {
		assertFalse(otherMedal.acceptsMetadata(wide));
	}

}
