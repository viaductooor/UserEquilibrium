import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LogWriter {
	public final static String linkUrl = "log/log_link.txt";
	public final static String odUrl = "log/log_odpair.txt";
	public final static String logUrl = "log/log.txt";
	private FileWriter linkWriter;
	private FileWriter odWriter;
	private FileWriter logWriter;
	private List<MyLink> linkList;
	private List<MyODPair> odList;

	public List<MyLink> getLinkList() {
		return linkList;
	}

	public void setLinkList(List<MyLink> linkList) {
		this.linkList = linkList;
	}

	public List<MyODPair> getOdList() {
		return odList;
	}

	public void setOdList(List<MyODPair> odList) {
		this.odList = odList;
	}

	public LogWriter(List<MyLink> ll,List<MyODPair> ol) {
		this.linkList = ll;
		this.odList = ol;
	}

	public void logWriteLink(String header){
		try {
			logWriter.write("\r\n\r\n"+header+"\r\n\r\n");
			for (MyLink ml : linkList) {
				logWriter.write(ml.toString() + "\r\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void logWriteOd(String header){
		try {
			logWriter.write("\r\n\r\n"+header+"\r\n\r\n");
			for (MyODPair modp : odList) {
				logWriter.write(modp.toString() + "\r\n");
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
		File linkFile = new File(linkUrl);
		File odFile = new File(odUrl);
		File logFile = new File(logUrl);
		
		linkWriter = null;
		odWriter = null;
		logWriter = null;
		try {
			linkFile.createNewFile();
			odFile.createNewFile();
			linkWriter = new FileWriter(linkFile);
			odWriter = new FileWriter(odFile);
			logWriter = new FileWriter(logFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		;
	};

	public void write(List<MyLink> linklist,List<MyODPair> odlist,String header) {
		try {
			linkWriter.write("\r\n\r\n"+header+"\r\n\r\n");
			for (MyLink ml : linklist) {
				linkWriter.write(ml.toString() + "\r\n");
			}
			linkWriter.write("---------------------------------------\r\n");
			
			odWriter.write("\r\n\r\n"+header+"\r\n\r\n");
			for (MyODPair modp : odlist) {
				odWriter.write(modp.toString() + "\r\n");
			}
			odWriter.write("---------------------------------------\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	public void close() {
		try {
			linkWriter.close();
			odWriter.close();
			logWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
