package org.grobid.core.main;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Slava
 */
public class GrobidConstants {
	public static final Charset CHARSET = StandardCharsets.UTF_8;
	
    //a name of a native CRF++ library without an extension
    public static final String CRFPP_NATIVE_LIB_NAME = "libcrfpp";
    public static final String WAPITI_NATIVE_LIB_NAME = "libwapiti";

    public static final String TEST_RESOURCES_PATH = "./src/test/resources/test";
}
