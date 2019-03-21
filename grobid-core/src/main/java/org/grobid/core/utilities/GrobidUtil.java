package org.grobid.core.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.grobid.core.document.Document;
import org.grobid.core.layout.Block;
import org.grobid.core.layout.LayoutToken;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.TypePermission;

public class GrobidUtil {

	public static void writeBlocksToFile(List<Block> blocks, File file) throws IOException {
		StringBuffer sb = new StringBuffer();
		int x = 0;
		for (Block block : blocks) {
			sb.append(" ---------------- \n");
			sb.append("| Block" + (x++) + " " + block.getStartToken() + "-" + block.getEndToken() + " |\n");
			sb.append(" ---------------- \n");
			sb.append("\"");
			sb.append(block.getText());
			sb.append("\"\n");
		}
		FileUtils.writeStringToFile(file, sb.toString(), StandardCharsets.UTF_8);
		System.out.println("blocks written");
	}

	public static void writeLayoutTokensAsTextToFile(List<LayoutToken> tokenizations, File file) throws IOException {
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8");) {
			for (LayoutToken token : tokenizations) {
				writer.write("\"" + token.getText().replaceAll("\n", "<br>") + "\"\n");
			}
		}
		System.out.println("tokens written");
	}

	public static void writeLayoutTokensAsTextToFileWithOutWhitespaceAndNewline(List<LayoutToken> tokenizations,
			File file) throws IOException {
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8");) {
			for (LayoutToken token : tokenizations) {
				if (!TextUtilities.split_delimiters.contains(token.getText())) {
					writer.write(token.getText() + "\n");
				}
			}
		}
	}

	public static void writeLayoutTokensAsTextToFile(List<Block> blocks, List<LayoutToken> tokenizations, File file)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8");) {
			for (Block blo : blocks) {
				int tokens = blo.getStartToken();
				int tokene = blo.getEndToken();
				for (int i = tokens; i < tokene; i++) {
					writer.write("| Token:" + i + " | \"" + tokenizations.get(i).getText().replaceAll("\n", "<br>")
							+ "\"\n");
				}
			}

		}
		System.out.println("tokens written");

	}

	public static void checkTokenAndBlockTextEqual(List<Block> blocks, List<LayoutToken> tokenizations, File file)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8");) {
			int x = 0;
			for (Block blo : blocks) {
				int tokens = blo.getStartToken();
				int tokene = blo.getEndToken();
				StringBuffer sb = new StringBuffer();
				for (int i = tokens; i < tokene; i++) {
					sb.append(tokenizations.get(i).getText());
				}
				String tokenString = sb.toString();
				writer.write("| Block:" + (x++) + " | \n"
						+ tokenString.equals(blo.getText().substring(0, blo.getText().length() - 2)) + "\n"
						+ tokenString + "\n" + blo.getText() + "\n");
			}

		}
		System.out.println("check written");

	}

	public static void writeLabelPairs(List<TokenLabelPair> labels, File file) {
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8");) {
			for (TokenLabelPair pair : labels) {
				if (pair == null) {
					writer.write("null");
				} else {
					String token = pair.getToken();
					String label = pair.getLabel();
					writer.write(token + "\t" + label + "\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("labelpairs written: " + file.getAbsolutePath());
	}

	public static void writeObjectToFile(Object object, File file) throws FileNotFoundException {
		XStream xStream = getXStream();
		xStream.toXML(object, new FileOutputStream(file));
	}

	private static XStream getXStream() {
		XStream xStream = new XStream(new DomDriver(StandardCharsets.UTF_8.name()));
		xStream.setMode(XStream.SINGLE_NODE_XPATH_ABSOLUTE_REFERENCES);

		// allowed classes, otherwise com.thoughtworks.xstream.security.ForbiddenClassException
		XStream.setupDefaultSecurity(xStream);
		xStream.addPermission(new TypePermission() {
			@Override
			public boolean allows(Class type) {
				return true;
			}
		});
		return xStream;
	}
}
