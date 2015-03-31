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

package longbow.test.checker;


import static junit.framework.Assert.*;
import static longbow.test.checker.Checker.*;

import java.util.Arrays;

import org.junit.Test;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class CheckerTest {

	@Test
	public void testAnd1() {
		assertChecker(true, NULL.and(NULL));
	}

	@Test
	public void testAnd2() {
		assertChecker(false, NULL.and(FALSE));
	}

	@Test
	public void testAnd3() {
		assertChecker(false, FALSE.and(NULL));
	}

	@Test
	public void testAnd4() {
		assertChecker(false, FALSE.and(FALSE));
	}

	@Test
	public void testAndToString() {
		assertEquals("(FALSE) AND (FALSE)", FALSE.and(FALSE).toString());
	}

	@Test
	public void testFalse() {
		assertChecker(false, FALSE);
	}

	@Test
	public void testFalseToString() {
		assertEquals("FALSE", Checker.FALSE.toString());
	}

	@Test
	public void testIsAFalse() {
		final Checker checker = not(isA(Integer.class));
		assertTrue(checker.check("foo"));
	}

	@Test
	public void testIsAToString() {
		final Checker checker = isA(String.class);
		assertEquals("IS A java.lang.String", checker.toString());
	}

	@Test
	public void testIsATrue() {
		final Checker checker = isA(Integer.class);
		assertTrue(checker.check(Integer.valueOf(5)));
	}

	@Test
	public void testNand1() {
		assertChecker(false, NULL.nand(NULL));
	}

	@Test
	public void testNand2() {
		assertChecker(true, FALSE.nand(NULL));
	}

	@Test
	public void testNand3() {
		assertChecker(true, NULL.nand(FALSE));
	}

	@Test
	public void testNand4() {
		assertChecker(true, FALSE.nand(FALSE));
	}

	@Test
	public void testNandToString() {
		assertEquals("(FALSE) NAND (FALSE)", Checker.FALSE.nand(Checker.FALSE).toString());
	}

	@Test
	public void testNor1() {
		assertChecker(true, FALSE.nor(FALSE));
	}

	@Test
	public void testNor2() {
		assertChecker(false, FALSE.nor(NULL));
	}

	@Test
	public void testNor3() {
		assertFalse(Checker.NULL.nor(Checker.FALSE).doCheck(null));
	}

	@Test
	public void testNor4() {
		assertFalse(Checker.NULL.nor(Checker.NULL).doCheck(null));
	}

	@Test
	public void testNorToString() {
		assertEquals("(FALSE) NOR (FALSE)", Checker.FALSE.nor(Checker.FALSE).toString());
	}

	@Test
	public void testNotToString() {
		assertEquals("NOT (FALSE)", Checker.not(Checker.FALSE).toString());
	}

	@Test
	public void testNull() {
		assertTrue(Checker.NULL.doCheck(null));
	}

	@Test
	public void testNullCheck() {
		assertTrue(Checker.NULL.check(null));
	}

	@Test
	public void testNullToString() {
		assertEquals("TRUE", Checker.NULL.toString());
	}

	@Test
	public void testOr1() {
		assertChecker(false, FALSE.or(FALSE));
	}

	@Test
	public void testOr2() {
		assertChecker(true, NULL.or(FALSE));
	}

	@Test
	public void testOr3() {
		assertChecker(true, FALSE.or(NULL));
	}

	@Test
	public void testOr4() {
		assertChecker(true, NULL.or(NULL));
	}

	@Test
	public void testOrToString() {
		assertEquals("(FALSE) OR (FALSE)", Checker.FALSE.or(Checker.FALSE).toString());
	}

	@Test
	public void testValueEqualsFalse() {
		final String s = "foe";
		final Checker checker = not(valueEquals(s));
		assertTrue(checker.toString(), checker.check("ble"));
	}

	@Test
	public void testValueEqualsTrue() {
		final String s = "ble";
		final Checker checker = Checker.valueEquals(s);
		assertTrue(checker.toString(), checker.check("ble"));
	}

	@Test
	public void testValuesEqualFalse() {
		final Checker checker = not(valuesEqual(1, "foo", "bar", 4));
		assertTrue(checker.toString(), checker.check(Arrays.asList(new Object[] { 1, "bar", "foo", 4 })));
	}

	@Test
	public void testValuesEqualTrue() {
		final Checker checker = valuesEqual(1, "foo", "bar", 4);
		assertTrue(checker.toString(), checker.check(Arrays.asList(new Object[] { 1, "foo", "bar", 4 })));
	}

	@Test
	public void testXor1() {
		assertChecker(true, FALSE.xor(NULL));
	}

	@Test
	public void testXor2() {
		assertChecker(true, NULL.xor(FALSE));
	}

	@Test
	public void testXor3() {
		assertChecker(false, FALSE.xor(FALSE));
	}

	@Test
	public void testXor4() {
		assertChecker(false, NULL.xor(NULL));
	}

	@Test
	public void testXorToString() {
		assertEquals("(FALSE) XOR (FALSE)", Checker.FALSE.xor(Checker.FALSE).toString());
	}

	@Test
	public void valueEqualsToString() {
		final Checker checker = valueEquals("ble");
		assertEquals("equals (ble)", checker.toString());
	}

	private void assertChecker(final boolean value, final Checker checker) throws AssertionError {
		if (value) {
			assertTrue(checker.toString(), checker.check(null));
		} else {
			assertTrue(checker.toString(), Checker.not(checker).check(null));
		}
	}

}
