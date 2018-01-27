import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LogWriter {
	public final static String linkUrl = "log_link.txt";
	public final static String odUrl = "log_odpair.txt";
	private FileWriter linkWriter;
	private FileWriter odWriter;

	public LogWriter() {

	}

	public void init() {
		File linkFile = new File(linkUrl);
		File odFile = new File(odUrl);
		linkWriter = null;
		odWriter = null;
		try {
			linkFile.createNewFile();
			odFile.createNewFile();
			linkWriter = new FileWriter(linkFile);
			odWriter = new FileWriter(odFile);
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
