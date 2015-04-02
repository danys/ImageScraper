package imagescraper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jsoup.nodes.Document;

public class ImageScraper
{
	private static boolean validateURLString(String s)
	{
		if (s.indexOf(" ")!=-1) return false;
		int startindex = s.indexOf("http://");
		if ((startindex==-1) && (s.indexOf("https://")==-1)) return false;
		if (s.indexOf('.', startindex)==-1) return false;
		return true;
	}
	
	public static void main(String args[])
	{
		System.out.println("ImageScraper");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String urlString="";
		URL url = null;
		try
		{
			boolean b = false;
			while(!b)
			{
				System.out.println("Please enter a valid URL");
				urlString = br.readLine();
				b = validateURLString(urlString);
				try
				{
					if (b) url = new URL(urlString);
				} catch (MalformedURLException e)
				{
					e.printStackTrace();
					b=false;
				}
			}
		}
		catch (IOException e)
		{
			System.out.println("Reading the URL from the command line failed!");
			return;
		}
		System.out.println("URL to be scanned: "+urlString);
		Document htmlDoc = null;
		PageDownloader fetcher = new PageDownloader();
		try
		{
			htmlDoc = fetcher.getHTMLDocument(url,1000);
		}
		catch(IOException e)
		{
			System.out.println("There was a problem getting the page content");
			return;
		}
		URLFetcher urlFetcher = new URLFetcher(htmlDoc,urlString);
		List<String> list = urlFetcher.fetchLinks();
		String filename = "C:\\Users\\Dany\\Downloads\\test\\";
		String name;
		for(int i=0;i<list.size();i++)
		{
			System.out.println("Downloading : "+list.get(i));
			try
			{
				name = list.get(i);
				fetcher.getFile(name, filename+Integer.toString(i)+"."+fileExtension(name), 10000);
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private static String fileExtension(String fileName)
	{
		int index = fileName.lastIndexOf('.');
		if (index==-1) return "";
		else if(index<=fileName.length()-1)
		{
			String res = fileName.substring(index+1);
			if (res.length()>3) res=res.substring(res.length()-3);
			res = res.replace("\\", "");
			return res;
		}
		else return "";
	}
}
