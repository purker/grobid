package org.grobid.core.engines.tagging;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.grobid.core.utilities.TokenLabelPair;
import org.junit.Test;

/**
 * Created by lfoppiano on 28/11/16.
 */
public class GenericTaggerUtilsTest {

    @Test
    public void testGetPlainLabel_normalValue() throws Exception {
        assertThat(GenericTaggerUtils.getPlainLabel("<status>"), is("<status>"));
    }

    @Test
    public void testGetPlainLabel_startingValue() throws Exception {
        assertThat(GenericTaggerUtils.getPlainLabel("I-<status>"), is("<status>"));
    }

    @Test
    public void testGetPlainLabel_nullValue() throws Exception {
        assertNull(GenericTaggerUtils.getPlainLabel(null));
    }

    @Test
    public void testIsBeginningOfEntity_true() throws Exception {
        assertTrue(GenericTaggerUtils.isBeginningOfEntity("I-<status>"));
    }

    @Test
    public void testIsBeginningOfEntity_false() throws Exception {
        assertFalse(GenericTaggerUtils.isBeginningOfEntity("<status>"));
    }

    @Test
    public void testIsBeginningOfEntity_false2() throws Exception {
        assertFalse(GenericTaggerUtils.isBeginningOfEntity("<I-status>"));
    }
    
    @Test
    public void getTokensAndLabelsAsPair() throws Exception {
    	String s="1	1	1	1	1	1	1	1	1	1	BLOCKSTART	LINESTART	ALIGNEDLEFT	NEWFONT	HIGHERFONT	0	0	NOCAPS	ALLDIGIT	1	NOPUNCT	0	5	0	I-<section>\n" + 
    			"Introduction	introduction	I	In	Int	Intr	n	on	ion	tion	BLOCKEND	LINEEND	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	INITCAP	NODIGIT	0	NOPUNCT	0	5	0	<section>\n" + 
    			"The	the	T	Th	The	The	e	he	The	The	BLOCKSTART	LINESTART	ALIGNEDLEFT	NEWFONT	LOWERFONT	0	0	INITCAP	NODIGIT	0	NOPUNCT	0	6	0	I-<paragraph>\n" + 
    			"use	use	u	us	use	use	e	se	use	use	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"of	of	o	of	of	of	f	of	of	of	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"clinical	clinical	c	cl	cli	clin	l	al	cal	ical	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"guidelines	guidelines	g	gu	gui	guid	s	es	nes	ines	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"and	and	a	an	and	and	d	nd	and	and	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"treatment	treatment	t	tr	tre	trea	t	nt	ent	ment	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"plans	plans	p	pl	pla	plan	s	ns	ans	lans	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"has	has	h	ha	has	has	s	as	has	has	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"a	a	a	a	a	a	a	a	a	a	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	1	NOPUNCT	0	6	0	<paragraph>";

		List<TokenLabelPair> labelPairs = GenericTaggerUtils.getTokensAndLabelsAsPair(s);
		List<TokenLabelPair> expected = new ArrayList<>();
		expected.add(new TokenLabelPair("1", "I-<section>"));
		expected.add(new TokenLabelPair("Introduction", "<section>"));
		expected.add(new TokenLabelPair("The", "I-<paragraph>"));
		expected.add(new TokenLabelPair("use", "<paragraph>"));
		expected.add(new TokenLabelPair("of", "<paragraph>"));
		expected.add(new TokenLabelPair("clinical", "<paragraph>"));
		expected.add(new TokenLabelPair("guidelines", "<paragraph>"));
		expected.add(new TokenLabelPair("and", "<paragraph>"));
		expected.add(new TokenLabelPair("treatment", "<paragraph>"));
		expected.add(new TokenLabelPair("plans", "<paragraph>"));
		expected.add(new TokenLabelPair("has", "<paragraph>"));
		expected.add(new TokenLabelPair("a", "<paragraph>"));

		assertTrue(expected.equals(labelPairs));
    }

	@Test
    public void testReplaceLabelsOnLabledResult() throws Exception {
    	String s="1	1	1	1	1	1	1	1	1	1	BLOCKSTART	LINESTART	ALIGNEDLEFT	NEWFONT	HIGHERFONT	0	0	NOCAPS	ALLDIGIT	1	NOPUNCT	0	5	0	I-<section>\n" + 
    			"Introduction	introduction	I	In	Int	Intr	n	on	ion	tion	BLOCKEND	LINEEND	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	INITCAP	NODIGIT	0	NOPUNCT	0	5	0	<section>\n" + 
    			"The	the	T	Th	The	The	e	he	The	The	BLOCKSTART	LINESTART	ALIGNEDLEFT	NEWFONT	LOWERFONT	0	0	INITCAP	NODIGIT	0	NOPUNCT	0	6	0	I-<paragraph>\n" + 
    			"use	use	u	us	use	use	e	se	use	use	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"of	of	o	of	of	of	f	of	of	of	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"clinical	clinical	c	cl	cli	clin	l	al	cal	ical	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"guidelines	guidelines	g	gu	gui	guid	s	es	nes	ines	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"and	and	a	an	and	and	d	nd	and	and	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"treatment	treatment	t	tr	tre	trea	t	nt	ent	ment	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"plans	plans	p	pl	pla	plan	s	ns	ans	lans	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"has	has	h	ha	has	has	s	as	has	has	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<paragraph>\n" + 
    			"a	a	a	a	a	a	a	a	a	a	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	1	NOPUNCT	0	6	0	<paragraph>";

		List<TokenLabelPair> replaceWith = new ArrayList<>();
		replaceWith.add(new TokenLabelPair("1", "I-<section>"));
		replaceWith.add(new TokenLabelPair("Introduction", "<section>"));
		replaceWith.add(new TokenLabelPair("The", "I-<section>"));
		replaceWith.add(new TokenLabelPair("use", "<section>"));
		replaceWith.add(new TokenLabelPair("of", "<section>"));
		replaceWith.add(new TokenLabelPair("clinical", "<section>"));
		replaceWith.add(new TokenLabelPair("guidelines", "<section>"));
		replaceWith.add(new TokenLabelPair("and", "<section>"));
		replaceWith.add(new TokenLabelPair("treatment", "<section>"));
		replaceWith.add(new TokenLabelPair("plans", "<section>"));
		replaceWith.add(new TokenLabelPair("has", "<section>"));
		replaceWith.add(new TokenLabelPair("a", "<section>"));

		String expected="1	1	1	1	1	1	1	1	1	1	BLOCKSTART	LINESTART	ALIGNEDLEFT	NEWFONT	HIGHERFONT	0	0	NOCAPS	ALLDIGIT	1	NOPUNCT	0	5	0	I-<section>\n" + 
				"Introduction	introduction	I	In	Int	Intr	n	on	ion	tion	BLOCKEND	LINEEND	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	INITCAP	NODIGIT	0	NOPUNCT	0	5	0	<section>\n" + 
				"The	the	T	Th	The	The	e	he	The	The	BLOCKSTART	LINESTART	ALIGNEDLEFT	NEWFONT	LOWERFONT	0	0	INITCAP	NODIGIT	0	NOPUNCT	0	6	0	I-<section>\n" + 
				"use	use	u	us	use	use	e	se	use	use	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<section>\n" + 
				"of	of	o	of	of	of	f	of	of	of	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<section>\n" + 
				"clinical	clinical	c	cl	cli	clin	l	al	cal	ical	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<section>\n" + 
				"guidelines	guidelines	g	gu	gui	guid	s	es	nes	ines	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<section>\n" + 
				"and	and	a	an	and	and	d	nd	and	and	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<section>\n" + 
				"treatment	treatment	t	tr	tre	trea	t	nt	ent	ment	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<section>\n" + 
				"plans	plans	p	pl	pla	plan	s	ns	ans	lans	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<section>\n" + 
				"has	has	h	ha	has	has	s	as	has	has	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	0	NOPUNCT	0	6	0	<section>\n" + 
				"a	a	a	a	a	a	a	a	a	a	BLOCKIN	LINEIN	ALIGNEDLEFT	SAMEFONT	SAMEFONTSIZE	0	0	NOCAPS	NODIGIT	1	NOPUNCT	0	6	0	<section>";

		String replacedString = GenericTaggerUtils.replaceLabelsOnLabledResult(null, null, s, replaceWith);

		System.out.println(s);
		System.out.println(replacedString);
		assertTrue(StringUtils.removeEnd(expected, "\n").equals(StringUtils.removeEnd(replacedString, "\n")));
    }
}