package test;

import java.io.IOException;
import java.util.Set;

import functions.ShortestPath;
import functions.TaxiSurcharge;
import functions.TaxiSurcharge.MyLink;
import functions.TaxiSurcharge.Trip;
import jnetwork.Graph;

public class TaxiSurchargeTest {
	public static void main(String[] args) {
		TaxiSurcharge tc = new TaxiSurcharge();
		try {
			tc.run(TaxiSurcharge.CAPACITY_URL, TaxiSurcharge.FFTT_URL, TaxiSurcharge.Other_VOLUME_URL,
					TaxiSurcharge.TRIP_URL, TaxiSurcharge.CURRENT_COST_URL, 500f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
