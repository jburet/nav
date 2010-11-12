package fr.jburet.nav.database.point;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import android.util.Log;

public class WaypointImporterFromCup {
	private WaypointQuery waypointQuery;

	public WaypointImporterFromCup(WaypointQuery waypointQuery) {
		this.waypointQuery = waypointQuery;
	}

	public void parseFile(String path) throws CannotImportException {
		File waypointFile = new File(path);
		if (!waypointFile.exists() || !waypointFile.canRead()) {
			throw new CannotImportException("Waypoint cannot be read");
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(waypointFile));
			// First line don"t contain waypoint
			br.readLine();
			String line;
			while ((line = br.readLine()) != null) {
				importWaypointFromCupLine(line);
			}
		} catch (FileNotFoundException e) {
			throw new CannotImportException("Waypoint cannot be read", e);
		} catch (IOException e) {
			throw new CannotImportException("Waypoint cannot be read", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {

				}
			}
		}
	}

	private void importWaypointFromCupLine(String line) throws CannotImportException {
		Waypoint newWaypoint = null;
		try {
			StringTokenizer st = new StringTokenizer(line, ",", false);
			newWaypoint = new Waypoint();
			newWaypoint.setName(st.nextToken().trim());
			newWaypoint.setCode(st.nextToken().trim());
			// PAYS
			st.nextToken();
			// Seeyou format DDMM.MMMN
			newWaypoint.setLatitude(convertLatitude(st.nextToken().trim()));
			newWaypoint.setLongitude(convertLongitude(st.nextToken().trim()));
			newWaypoint.setAltitude(convertAltitude(st.nextToken().trim()));
			// Style
			// Direction
			// Length
			newWaypoint.setFrequency(st.nextToken().trim());
			// Description

		} catch (Exception e) {
			Log.e("CUP_IMPORTER", line + " : not in .cup format");
			return;
		}
		waypointQuery.save(newWaypoint);

	}

	private float convertAltitude(String nextToken) {
		nextToken.replace('m', 'M');
		return Float.parseFloat(nextToken.substring(0, nextToken.indexOf("M")));
	}

	private float convertLatitude(String nextToken) {
		float res = 0;
		// Take two first char --> degree
		res += Float.parseFloat(nextToken.substring(0, 2));
		// Take two more char --> convert to dregree/100
		res += (Float.parseFloat(nextToken.substring(2, 4)) / 60f);
		// Leave . and parse 3 more char --> cent
		res += (Float.parseFloat(nextToken.substring(5, 8)) / 100f / 100f);
		if (nextToken.substring(8, 9).equals("N")) {
			return res;
		} else {
			return -res;
		}
	}

	private float convertLongitude(String nextToken) {
		float res = 0;
		// Take two first char --> degree
		res += Float.parseFloat(nextToken.substring(0, 3));
		// Take two more char --> convert to dregree/100
		res += (Float.parseFloat(nextToken.substring(3, 5)) / 60f);
		// Leave . and parse 3 more char --> cent
		res += (Float.parseFloat(nextToken.substring(6, 9)) / 100f / 100f);
		if (nextToken.substring(9, 10).equals("E")) {
			return res;
		} else {
			return -res;
		}
	}

}
