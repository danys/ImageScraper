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
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.nodes.Document;

/**
 * This class provides the entry point of the the ImageScraper application 
 * @author Dany
 *
 */
public class ImageScraper
{
	private static final int htmlDownloadBufferlength = 1000;
	private static final int fileDownloadBufferlength = 10000;
	private static final long threadKillWaitTime = 10;
	private static final int threadPoolSize = 10;
	private static final String dirname = "C:\\Users\\Dany\\Downloads\\test\\";
	private static final long sleepMillis = 100;
	
	public static volatile AtomicInteger ntasks;
	
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
			//Read URL from console and check validity
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
		//Fetch the HTML page associated to the given URL
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
		//Extract src attribute from <img> tags
		URLFetcher urlFetcher = new URLFetcher(htmlDoc,urlString);
		List<String> list = urlFetcher.fetchImageLinks();
		//Extract URLs of CSS files
		List<String> cssLinks = urlFetcher.fetchCSSLinks();
		
		String name, fname;
		ExecutorService exec = Executors.newFixedThreadPool(threadPoolSize);
		ntasks = new AtomicInteger();
		for(int i=0;i<list.size();i++)
		{
			System.out.println("Downloading : "+list.get(i));
			name = list.get(i);
			fname = fileTitle(name);
			if (fname.length()>0)	exec.execute(new FileDownloader(name, dirname+fileTitle(name)+"."+fileExtension(name), fileDownloadBufferlength));
			else	exec.execute(new FileDownloader(name, dirname+Integer.toString(i)+"."+fileExtension(name), fileDownloadBufferlength));
			/*if (fname.length()>0) new Thread(new FileDownloader(name, dirname+fileTitle(name)+"."+fileExtension(name), fileDownloadBufferlength)).start();
			else new Thread(new FileDownloader(name, dirname+Integer.toString(i)+"."+fileExtension(name), fileDownloadBufferlength)).start();*/
		}
		//Shut down threads as described in Java API
		exec.shutdown(); //Stop accepting new execute commands
		while(ntasks.get()>0)
		{
			try
			{
				Thread.sleep(sleepMillis);
			}
			catch (InterruptedException e)
			{
				//Repeat the loop
			}
		}
		try
		{
		     exec.shutdownNow();
		     if (!exec.awaitTermination(threadKillWaitTime, TimeUnit.SECONDS)) System.out.println("Pool did not terminate");
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
