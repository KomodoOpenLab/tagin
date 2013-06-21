package ca.idrc.tagin.model;

public class Util {

	/**
	 * Calculates the Rank of the beacon by normalizing with respect to Max RSSI
	 * @param rssi - received signal strength indication
	 * @param maxRSSIEver - Maximum RSSI ever recorded by this device
	 * @return Normalized rank value in the range [0, 1]
	 */
	public static Double calculateRank(int rssi, int maxRSSIEver) {
		Double maxPowerEver = dBm2Power(maxRSSIEver);
		return Math.pow(dBm2Power(rssi) / maxPowerEver, 0.25);
	}

	public static double dBm2Power(int rssi) {
		return Math.pow(10.0, Double.valueOf(rssi - 30) / 10.0);
	}
}
