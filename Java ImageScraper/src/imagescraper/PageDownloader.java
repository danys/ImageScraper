package imagescraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
}
