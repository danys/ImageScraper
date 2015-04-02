package imagescraper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PageDownloader
{
	
	public PageDownloader()
	{
		//
	}
	
	public Document getHTMLDocument(URL url,int maxlen) throws IOException
	{
		String htmlContent = getHTML(url,maxlen);
		return Jsoup.parse(htmlContent);
	}
	
	private String getHTML(URL url, int maxlen) throws IOException
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			int count = 0;
			char carray[] = new char[maxlen];
			StringBuilder sb = new StringBuilder();
			while(count!=-1)
			{
				count = reader.read(carray, 0, maxlen);
				if (count!=-1)
				{
					for(int i=0;i<count;i++)	sb.append(carray[i]);
				}
			}
			return sb.toString();
		}
		finally
		{
			if (reader!=null) reader.close();
		}
	}
	
	public void getFile(String urlString, String filename, int maxlen) throws MalformedURLException, IOException, FileNotFoundException
	{
		BufferedInputStream reader = null;
		FileOutputStream fs = null;
		try
		{
			URL url = new URL(urlString);
			fs = new FileOutputStream(filename);
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
