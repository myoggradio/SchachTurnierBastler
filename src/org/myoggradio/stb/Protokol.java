package org.myoggradio.stb;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Protokol 
{
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Writer log = null;
	public static void write(String s)
	{
		Date jetzt = new Date();
		String prefix = sdf.format(jetzt);
		if (log == null)
		{
			try
			{
				String logDatei = Parameter.autoSaveDirectory + File.separator + "log_" + prefix + ".txt";
				log = new FileWriter(new File(logDatei));
			}
			catch (Exception e)
			{
				log = null;
				System.out.println("Protokol:write:Exception:");
				System.out.println(e.toString());
			}
		}
		System.out.println(prefix + " " + s);
		if (log != null)
		{
			try
			{
				log.write(prefix + " " + s);
				log.flush();
			}
			catch (Exception e)
			{
				System.out.println("Protokol:write:Exception:");
				System.out.println(e.toString());
			}
		}
	}
}
