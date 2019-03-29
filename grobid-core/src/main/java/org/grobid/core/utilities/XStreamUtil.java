package org.grobid.core.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.SunUnsafeReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.TypePermission;

public class XStreamUtil
{
	public static void convertToXml(Object object, File file, PrintStream out, boolean exitOnError)
	{
		try
		{
			convertToXml(object, new FileOutputStream(file), out, exitOnError);
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}

	}

	public static void convertToXml(Object object, OutputStream stream, PrintStream out, boolean exitOnError)
	{
		try
		{
			XStream xStream = getXStream();
			xStream.toXML(object, stream);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	private static XStream getXStream()
	{
		XStream xStream = new XStream(new SunUnsafeReflectionProvider(new FieldDictionary()), new DomDriver(StandardCharsets.UTF_8.name()));

		xStream.setMode(XStream.SINGLE_NODE_XPATH_ABSOLUTE_REFERENCES);


		// allowed classes, otherwise com.thoughtworks.xstream.security.ForbiddenClassException
		XStream.setupDefaultSecurity(xStream);
		xStream.addPermission(new TypePermission()
		{
			@Override
			public boolean allows(Class type)
			{
				return type.getPackage().getName().startsWith("org.grobid");
			}
		});

		return xStream;
	}



	public static <T> T convertFromXML(File file, Class<T> clazz)
	{
		XStream xStream = getXStream();
		return clazz.cast(xStream.fromXML(file));
	}

	public static <T> T convertFromString(String string, Class<T> clazz)
	{
		XStream xStream = getXStream();
		return clazz.cast(xStream.fromXML(string));
	}
}
