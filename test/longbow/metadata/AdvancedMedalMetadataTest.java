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
import longbow.Metadata;

import org.junit.Test;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class AdvancedMedalMetadataTest {

	@Test
	public void test1() {
		final String former = "class is java.lang.String and not null";
		final String latter = "is not null";
		assertNotAccepts(latter, former);
		assertAccepts(former, latter);
	}

	@Test
	public void test2a() {
		final String former = "class is java.sql.Connection xor not (is not null)";
		final String latter = "class is java.lang.String and not (is not null)";
		assertNotAccepts(former, latter);
	}

	@Test
	public void test2b() {
		final String former = "(class is java.sql.Connection) xor ( not (is not null))";
		final String latter = "(class is java.lang.String) and ( not (is not null))";
		assertAccepts(latter, former);
	}

	@Test
	public void test2c() {
		final String former = "(class is java.sql.Connection) xor (class is java.lang.Number)";
		final String latter = "(class is java.lang.String) and (class is java.lang.Number)";
		assertAccepts(latter, former);
	}

	@Test
	public void test3() {
		final String latter = "class is java.sql.Connection and is not null";
		final String former = "not null and class is java.sql.Connection";
		assertAccepts(former, latter);
		assertAccepts(latter, former);
	}

	@Test
	public void test4() {
		final String input = "type is java.lang.Number";
		final String output = "type is java.lang.Integer";
		assertAccepts(output, input);
		assertNotAccepts(input, output);
	}

	@Test
	public void test5() {
		final String output = "is not null";
		final String input = "is not null";
		assertAccepts(output, input);
	}

	@Test
	public void test6() {
		final String medal = "type is java.lang.Number";
		assertAccepts(medal, medal);
	}

	@Test
	public void testNotNotNull1() {
		final String output = "is not null";
		final String input = "not( is not null)";
		assertNotAccepts(output, input);
	}

	@Test
	public void testNotNotNull2() {
		final String output = "not(is not null)";
		final String input = "is not null";
		assertNotAccepts(output, input);
	}

	@Test
	public void testNotNotNull3() {
		final String medal = "not (is not null)";
		assertAccepts(medal, medal);
	}

	private void assertAccepts(final String output, final String input) throws AssertionError {
		final Metadata formerMetadata = parse(output);
		final Metadata latterMetadata = parse(input);
		assertTrue("\"" + input + "\" does not accept \"" + output + "\"", latterMetadata.acceptsMetadata(formerMetadata));
	}

	private void assertNotAccepts(final String output, final String input) throws AssertionError {
		final Metadata formerMetadata = parse(output);
		final Metadata latterMetadata = parse(input);
		assertFalse("\"" + input + "\" accepts \"" + output + "\"", latterMetadata.acceptsMetadata(formerMetadata));
	}
}
