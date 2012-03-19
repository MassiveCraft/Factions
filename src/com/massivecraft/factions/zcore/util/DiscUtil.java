package com.massivecraft.factions.zcore.util;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class DiscUtil
{
	public static void write(File file, String content) throws IOException
	{
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF8"));
		out.write(content);
		out.close();
	}
	
	public static String read(File file) throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String ret = new String(new byte[0], "UTF-8");
		 
		String line;
		while ((line = in.readLine()) != null)
		{
			ret += line;
		}

		in.close();
		return ret;
	}
	
	public static boolean writeCatch(File file, String content)
	{
		try
		{
			write(file, content);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	public static String readCatch(File file)
	{
		try
		{
			return read(file);
		}
		catch (IOException e)
		{
			return null;
		}
	}
	
	public static boolean downloadUrl(String urlstring, File file)
	{
		try
		{
			URL url = new URL(urlstring);
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			FileOutputStream fos = new FileOutputStream(file);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean downloadUrl(String urlstring, String filename)
	{
		return downloadUrl(urlstring, new File(filename));
	}
}
