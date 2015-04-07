package imagescraper;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FileDownloader implements Runnable
{
	
	private String urlString;
	private String fileName;
	private int maxlen;
	
	public FileDownloader(String urlString, String fileName, int maxlen)
	{
		this.urlString = urlString;
		this.fileName = fileName;
		this.maxlen = maxlen;
	}
	
	public void run()
	{
		ImageScraper.ntasks.incrementAndGet();
		try
		{
			getFile();
		}
		catch (MalformedURLException e)
		{
			System.out.println("Error: "+urlString+" is not a valid URL!");
		}
		catch (FileNotFoundException e)
		{
			System.out.println("The file "+fileName+" could not be found!");
		}
		catch (IOException e)
		{
			System.out.println("There was a problem downloading file "+fileName);
		}
		finally
		{
			ImageScraper.ntasks.decrementAndGet();
		}
	}
	
	private void getFile() throws MalformedURLException, IOException, FileNotFoundException
	{
		BufferedInputStream reader = null;
		FileOutputStream fs = null;
		try
		{
			URL url = new URL(urlString);
			fs = new FileOutputStream(fileName);
			reader = new BufferedInputStream(url.openStream());
			int count = 0;
			byte barray[] = new byte[maxlen];
			while(count!=-1)
			{
				count = reader.read(barray, 0, maxlen);
				if (count!=-1)	fs.write(barray,0, count);
			}
		}
		finally
		{
			if (reader!=null) reader.close();
			if (fs!=null) fs.close();
		}
	}
}
