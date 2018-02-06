import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LogWriter {
	public final static String linkUrl = "log/log_link.txt";
	public final static String odUrl = "log/log_odpair.txt";
	public final static String logUrl = "log/log.txt";
	private FileWriter logWriter;
	private List<Link> linkList;
	private List<ODPair> odList;

	public List<Link> getLinkList() {
		return linkList;
	}

	public void setLinkList(List<Link> linkList) {
		this.linkList = linkList;
	}

	public List<ODPair> getOdList() {
		return odList;
	}

	public void setOdList(List<ODPair> odList) {
		this.odList = odList;
	}

	public LogWriter(List<Link> ll,List<ODPair> ol) {
		this.linkList = ll;
		this.odList = ol;
	}

	public void logWriteLink(String header){
		try {
			logWriter.write("\r\n\r\n"+header+"\r\n\r\n");
			for (Link l : linkList) {
				logWriter.write(l.toString() + "\r\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void logWriteOd(String header){
		try {
			logWriter.write("\r\n\r\n"+header+"\r\n\r\n");
			for (ODPair odp : odList) {
				logWriter.write(odp.toString() + "\r\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void logWriteOther(String content){
		try {
			logWriter.write("\r\n\r\n"+content+"\r\n\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void init() {
		File logFile = new File(logUrl);
		logWriter = null;
		try {
			logWriter = new FileWriter(logFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	};


	public void close() {
		try {
			logWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
