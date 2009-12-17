package uk.ac.rhul.cs.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.rhul.cs.utils.StringUtils;

public class StringUtilsTest {

	@Test
	public void testIsEmpty() {
		assert(StringUtils.isEmpty(null));
		assert(StringUtils.isEmpty(""));
		assertEquals(false, StringUtils.isEmpty(" "));
		assertEquals(false, StringUtils.isEmpty("bob"));
		assertEquals(false, StringUtils.isEmpty("  bob  "));
	}

	@Test
	public void testIsNotEmpty() {
		assertEquals(false, StringUtils.isNotEmpty(null));
		assertEquals(false, StringUtils.isNotEmpty(""));
		assert(StringUtils.isNotEmpty(" "));
		assert(StringUtils.isNotEmpty("bob"));
		assert(StringUtils.isNotEmpty("  bob  "));
	}

	@Test
	public void testIsWhitespace() {
		assertEquals(false, StringUtils.isWhitespace(null));
		assert(StringUtils.isWhitespace(""));
		assert(StringUtils.isWhitespace("  "));
		assertEquals(false, StringUtils.isWhitespace("abc"));
		assertEquals(false, StringUtils.isEmpty("ab2c"));
		assertEquals(false, StringUtils.isEmpty("ab-c"));
	}

	@Test
	public void testSubstringStringInt() {
		assertEquals(null, StringUtils.substring(null, 2));
		assertEquals("", StringUtils.substring("", 7));
		assertEquals("abc", StringUtils.substring("abc", 0));
		assertEquals("c", StringUtils.substring("abc", 2));
		assertEquals("", StringUtils.substring("abc", 4));
		assertEquals("bc", StringUtils.substring("abc", -2));
		assertEquals("abc", StringUtils.substring("abc", -4));
	}

	@Test
	public void testSubstringStringIntInt() {
		assertEquals(null, StringUtils.substring(null, 2, 4));
		assertEquals("", StringUtils.substring("", 7, -5));
		assertEquals("ab", StringUtils.substring("abc", 0, 2));
		assertEquals("", StringUtils.substring("abc", 2, 0));
		assertEquals("c", StringUtils.substring("abc", 2, 4));
		assertEquals("", StringUtils.substring("abc", 4, 6));
		assertEquals("", StringUtils.substring("abc", 2, 2));
		assertEquals("b", StringUtils.substring("abc", -2, -1));
		assertEquals("ab", StringUtils.substring("abc", -4, 2));
	}
}
