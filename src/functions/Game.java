package functions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import main.DataSet;
import main.GameLink;
import main.Link;
import main.Odpair;
import main.TNTPReader;

public class Game {
	// use to update rho
	private float beta;
	private float omega;
	private float d;

	private List<GameLink> links;
	private List<Odpair> trips;
	private int nodeNumber;
	private int linkNumber;
	private int n_iteration;
	private float v;

	public Game(DataSet dataset) {
		this.links = links2glinks(dataset.getLinks());
		this.trips = dataset.getTrips();
		this.nodeNumber = dataset.getNodeNubmer();
		this.linkNumber = dataset.getLinkNumber();
		this.omega = 0;
		this.d = 1;
		this.v = 0;
	}

	public List<GameLink> links2glinks(List<Link> links) {
		List<GameLink> glinks = new LinkedList<GameLink>();
		for (Link l : links) {
			glinks.add(new GameLink(l));
		}
		return glinks;
	}

	public float getBeta() {
		return beta;
	}

	public void setBeta(float beta) {
		this.beta = beta;
	}

	public float getOmega() {
		return omega;
	}

	public void setOmega(float omega) {
		this.omega = omega;
	}

	public float getD() {
		return d;
	}

	public void setD(float d) {
		this.d = d;
	}

	public void run(float beta, float omega, float d, float delta) {
		// write to excel
		Workbook workbook = new HSSFWorkbook();
		Sheet flowsheet = workbook.createSheet("flow");
		Sheet rhosheet = workbook.createSheet("rho");
		Sheet gammasheet = workbook.createSheet("gamma");
		flowsheet.createRow(0).createCell(0).setCellValue("abs(pre_V - V");
		rhosheet.createRow(0).createCell(0).setCellValue("abs(pre_V - V");
		gammasheet.createRow(0).createCell(0).setCellValue("abs(pre_V - V");
		int i = 1;
		for (GameLink l : links) {
			flowsheet.createRow(i).createCell(0).setCellValue("(" + l.getFrom() + "," + l.getTo() + ")");
			rhosheet.createRow(i).createCell(0).setCellValue("(" + l.getFrom() + "," + l.getTo() + ")");
			gammasheet.createRow(i).createCell(0).setCellValue("(" + l.getFrom() + "," + l.getTo() + ")");
			i++;
		}

		float _prev = 0;
		float _v = 0;
		n_iteration = 1;

		for (GameLink l : links) {
			l.setRho(1f / linkNumber);
			l.setGamma(0);
			l.setC_normal(l.getFtime());
			l.setC_fail(beta * linkNumber);
		}

		n_iteration = 2;

		do {
			_prev = v; // save the previous V
			_v = 0; // initiate the present V
			for (GameLink l : links) {
				float _rho = l.getRho();
				float _t = l.getT();
				float _s = (1 - _rho) * l.getC_normal() + _rho * l.getC_fail();
				_t = (1f / n_iteration) * _s + (1 - 1f / n_iteration) * _t;
				l.setS(_s);
				l.setT(_t);
			}

			GameUserEquilibrium ue = new GameUserEquilibrium(links, trips, nodeNumber);
			ue.compute(50);

			float _totalflow = GameUserEquilibrium.getTotalFlow(links);
			float _sum = 0;
			for (GameLink l : links) {
				_sum += l.getRho() * (l.getT() + omega) / d;
			}
			for (GameLink l : links) {
				float _gamma = l.getFlow() / _totalflow;
				float _rho = ((_gamma * (l.getT() + omega)) / d) / _sum;
				l.setGamma(_gamma);
				l.setRho(_rho);
			}
			for (GameLink l : links) {
				_v += l.getRho() * l.getGamma() * l.getT();
			}
			v = _v;

			float _change = Math.abs(_prev - _v);

			// write to excel
			flowsheet.getRow(0).createCell(n_iteration - 1).setCellValue(_change);
			rhosheet.getRow(0).createCell(n_iteration - 1).setCellValue(_change);
			gammasheet.getRow(0).createCell(n_iteration - 1).setCellValue(_change);
			i = 1;
			for (GameLink l : links) {
				flowsheet.getRow(i).createCell(n_iteration - 1).setCellValue(l.getFlow());
				rhosheet.getRow(i).createCell(n_iteration - 1).setCellValue(l.getRho());
				gammasheet.getRow(i).createCell(n_iteration - 1).setCellValue(l.getGamma());
				i++;
			}
			try {
				FileOutputStream output = new FileOutputStream("log/game_log.xls");
				workbook.write(output);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			n_iteration++;

		} while (Math.abs(_prev - _v) > delta);
	}

	public static void main(String args[]) {
		DataSet ds = TNTPReader.read(TNTPReader.SIOUXFALLS_TRIP, TNTPReader.SIOUXFALLS_NET);
		Game game = new Game(ds);
		game.run(1, 0, 1, 0.0001f);
	}
}
