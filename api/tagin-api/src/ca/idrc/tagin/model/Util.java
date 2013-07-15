package ca.idrc.tagin.model;

public class Util {

	/**
	 * Calculates the rank of the beacon by normalizing 
	 * with respect to the strongest measure ever seen.
	 * @param rssi - received signal strength indication
	 * @param maxRssi - Maximum RSSI ever recorded by this device
	 * @return Normalized rank value in the range [0, 1]
	 */
	public static Double calculateRank(Double rssi, Double maxRssi) {
		return Math.pow(rssi / maxRssi, 0.25);
	}

	public static double dBm2Power(int dbm) {
		return Math.pow(10.0, Double.valueOf(dbm - 30) / 10.0);
	}

	public static int power2dBm(double power) {
		return (int) Math.round(10 * Math.log10(power)) + 30;
	}
}
