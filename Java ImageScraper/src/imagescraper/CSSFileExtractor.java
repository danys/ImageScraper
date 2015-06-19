package imagescraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class CSSFileExtractor
{
	private URL cssURL;
	private String baseURL;
	
	public CSSFileExtractor()
	{
		//do nothing
	}
	
	
	private int computeMinIndex(String s)
	{
		if (s.indexOf("http")!=-1) return "http://".length()-1;
		else if (s.indexOf("https")!=-1) return "https://".length()-1;
		return -1;
	}
	
	private String resolveLink(String s)
	{
		if ((s.indexOf("http://")==0) || (s.indexOf("https://")==0)) return s;
		String domainPath = URLFetcher.domainPath(baseURL,computeMinIndex(baseURL));
		int index = s.indexOf('/');
		if ((index==0) && (s.length()>1))	return domainPath+s.substring(1);
		else if ((index==0) && (s.length()==1)) return domainPath;
		//Add code to handle paths with more than one ".."
		if (s.indexOf("..")==0) return URLFetcher.backPath(baseURL)+s.substring("../".length());
		return URLFetcher.stemPath(baseURL,computeMinIndex(baseURL))+s;
	}
	
	
	private void extractURLsFromString(List<String> res, String content)
	{
		int pos = content.indexOf("url(\'"),pos2;
		while(pos!=-1)
		{
			pos2 = content.indexOf('\'',pos+5);
			res.add(resolveLink(content.substring(pos+5,pos2)));
			if (pos+1<content.length()) pos = content.indexOf("url(\'",pos+1);
			else break;
		}
	}
	
	public void getExtractedImageURLs(List<String> urls,List<String> cssURLStrings)
	{
		StringBuffer sb = null;
		String content,line;
		BufferedReader in = null;
		boolean failed = false;
		for(String cssString: cssURLStrings)
		{
			try
			{
				failed=false;
				baseURL = cssString;
				cssURL = new URL(cssString);
				sb = new StringBuffer();
				in = new BufferedReader(new InputStreamReader(cssURL.openStream()));
				while((line = in.readLine())!=null) sb.append(line);
			}
			catch(MalformedURLException e)
			{
				failed=true;
				e.printStackTrace();
			}
			catch(IOException e)
			{
				failed=true;
				e.printStackTrace();
			}
			finally
			{
				if (in!=null)
					try
					{
						in.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
			}
			if (!failed)
			{
				content = sb.toString();
				extractURLsFromString(urls,content);
			}
		}	
	}
}
