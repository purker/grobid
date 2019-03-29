package org.grobid.core.tokenization;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.grobid.core.layout.LayoutToken;
import org.grobid.core.layout.LayoutTokenLine;
import org.grobid.core.utilities.XStreamUtil;
import org.junit.BeforeClass;
import org.junit.Test;

public class LayoutTokenLineTest {
	protected static List<LayoutToken> list;

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println(new File("src/test/resources/LayoutTokenLine.xml").exists());
		list = (List<LayoutToken>) XStreamUtil.convertFromXML(new File("src/test/resources/LayoutTokenLine.xml"), Object.class);
	}

	@Test
	public void testExclusion_notPresent_shouldReturnTrue() throws Exception {
		LayoutTokenLine line = new LayoutTokenLine(list);

		System.out.println(line.getText());
		assertEquals("line not equal", "1. Hu, B., Raidl, G.R.: An evolutionary algorithm with solution archives and bounding", line.getText());
	}
}
