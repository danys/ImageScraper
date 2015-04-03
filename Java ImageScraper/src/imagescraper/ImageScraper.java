package imagescraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;

public class ImageScraper
{
	private static final int htmlDownloadBufferlength = 1000;
	private static final int fileDownloadBufferlength = 100000;
	private static final long threadKillWaitTime = 10;
	private static final int threadPoolSize = 10;
	
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
			htmlDoc = fetcher.getHTMLDocument(url,htmlDownloadBufferlength);
		}
		catch(IOException e)
		{
			System.out.println("There was a problem getting the page content");
			return;
		}
		URLFetcher urlFetcher = new URLFetcher(htmlDoc,urlString);
		List<String> list = urlFetcher.fetchLinks();
		String filename = "C:\\Users\\Dany\\Downloads\\test\\";
		String name, fname;
		ExecutorService exec = Executors.newFixedThreadPool(threadPoolSize);
		for(int i=0;i<list.size();i++)
		{
			System.out.println("Downloading : "+list.get(i));
			name = list.get(i);
			fname = fileTitle(name);
			if (fname.length()>0) exec.execute(new FileDownloader(name, filename+fileTitle(name)+"."+fileExtension(name), fileDownloadBufferlength));
			else exec.execute(new FileDownloader(name, filename+Integer.toString(i)+"."+fileExtension(name), fileDownloadBufferlength));
		}
		//Shut down threads as described in Java API
		exec.shutdown(); //Stop accepting new execute commands
		try
		{
		   //Wait before killing remaining threads
		   if (!exec.awaitTermination(threadKillWaitTime, TimeUnit.SECONDS))
		   {
		     exec.shutdownNow();
		     if (!exec.awaitTermination(threadKillWaitTime, TimeUnit.SECONDS)) System.err.println("Pool did not terminate");
		    }
		}
		catch (InterruptedException ie)
		{
		    exec.shutdownNow();
		    Thread.currentThread().interrupt();
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
	
	private static String fileTitle(String fileName)
	{
		int index = fileName.lastIndexOf('.');
		if (index==-1) return "";
		int nextindex = fileName.lastIndexOf('/', index);
		if (nextindex==-1) return "";
		String res = fileName.substring(nextindex,index);
		res = res.replace("\\", "");
		res = res.replace("/", "");
		return res;
	}
}
