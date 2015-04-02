package imagescraper;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class URLFetcher
{
	private Document doc;
	private String baseURL;
	
	public URLFetcher(Document doc,String baseURL)
	{
		this.doc = doc;
		this.baseURL = baseURL;
	}
	
	private String stemPath(String s,int minIndex)
	{
		int index = s.lastIndexOf('/');
		if ((index==-1) || (index<=minIndex)) return s+"/";
		else return s.substring(0, index+1);
	}
	
	private String backPath(String s)
	{
		int index = s.lastIndexOf('/');
		int index2 = s.lastIndexOf('/',index-1);
		if (index2==-1) return s;
		else return s.substring(0, index2+1);
	}
	
	private String domainPath(String s,int minIndex)
	{
		if (minIndex+1>s.length()-1) return s;
		int index = s.indexOf('/',minIndex+1);
		if (index==-1) return s+"/";
		else return s.substring(0,index+1);
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
		String domainPath = domainPath(baseURL,computeMinIndex(baseURL));
		int index = s.indexOf('/');
		if ((index==0) && (s.length()>1))	return domainPath+s.substring(1);
		else if ((index==0) && (s.length()==1)) return domainPath;
		//Add code to handle paths with more than one ".."
		if (s.indexOf("..")==0) return backPath(baseURL)+s.substring("../".length());
		return stemPath(baseURL,computeMinIndex(baseURL))+s;
	}
	
	public List<String> fetchLinks()
	{
		List<String> links = new ArrayList<String>();
		Elements rawLinkTags = doc.select("img");
		String rawLink;
		for(Element tag : rawLinkTags)
		{
			rawLink = tag.attr("src");
			if ((!rawLink.isEmpty()) && (rawLink.compareTo("")!=0)) links.add(resolveLink(rawLink));
		}
		return links;
	}
}
