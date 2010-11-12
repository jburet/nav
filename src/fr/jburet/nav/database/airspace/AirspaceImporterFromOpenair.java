package fr.jburet.nav.database.airspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import fr.jburet.nav.database.point.CannotImportException;
import fr.jburet.nav.model.Coordinate;
import fr.jburet.nav.model.CoordinateParser;

public class AirspaceImporterFromOpenair {

	// Pattern matcher
	private final Pattern centerPattern = Pattern.compile("V X=([0-9:.]*) ([NS]) ([0-9:.]*) ([EW])");
	private final Pattern drawPolygonPointPattern = Pattern.compile("DP ([0-9:.]*) ([NS]) ([0-9:.]*) ([EW])");
	private final Pattern drawArcWithCoordinatePattern = Pattern
			.compile("DB ([0-9:.]*) ([NS]) ([0-9:.]*) ([EW]),([0-9:.]*) ([NS]) ([0-9:.]*) ([EW])");

	private AirspaceQuery airspaceQuery;

	private Airspace currentAirspace = null;

	public AirspaceImporterFromOpenair(AirspaceQuery airspaceQuery) {
		super();
		this.airspaceQuery = airspaceQuery;
	}

	public void parseFile(String path) throws CannotImportException {
		File airspaceFile = new File(path);
		if (!airspaceFile.exists() || !airspaceFile.canRead()) {
			throw new CannotImportException("Airspace cannot be read");
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(airspaceFile));
			// First line don"t contain waypoint
			br.readLine();
			String line;
			while ((line = br.readLine()) != null) {
				parseOpenAirLine(line.trim());
			}
		} catch (FileNotFoundException e) {
			throw new CannotImportException("Airspace cannot be read", e);
		} catch (IOException e) {
			throw new CannotImportException("Airspace cannot be read", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {

				}
			}
		}
	}

	private void parseOpenAirLine(String line) {
		if (line.startsWith(OpenAirConstant.AIRSPACE_CLASS)) {
			// save last airspace if exist
			if (currentAirspace != null) {
				try {
					EnveloppingCoordinate enveloppingCoordinate = new OpenAirParser()
							.getEnveloppingCoordinate(currentAirspace);
					currentAirspace.setMinLatitude(enveloppingCoordinate.getMinLatitude());
					currentAirspace.setMaxLatitude(enveloppingCoordinate.getMaxLatitude());
					currentAirspace.setMinLongitude(enveloppingCoordinate.getMinLongitude());
					currentAirspace.setMaxLongitude(enveloppingCoordinate.getMaxLongitude());
					airspaceQuery.save(currentAirspace);
				} catch (Exception e) {
					Log.e("AIRSPACE_IMPORTER", "Cannot import", e);
				}
			}
			// Create a new airspace
			currentAirspace = new Airspace();
			currentAirspace.setClasse(line.substring(3).trim());
		} else if (line.startsWith(OpenAirConstant.AIRSPACE_NAME)) {
			currentAirspace.setCode(line.substring(3).trim());
		} else if (line.startsWith(OpenAirConstant.AIRSPACE_TOP)) {
			// FIXME to implements
			currentAirspace.setAltitudeTop("1000");
		} else if (line.startsWith(OpenAirConstant.AIRSPACE_LOW)) {
			// FIXME to implements
			currentAirspace.setAltitudeBottom("0");
		} else if (line.startsWith(OpenAirConstant.VARIABLE) || line.startsWith(OpenAirConstant.P_COORD)
				|| line.startsWith(OpenAirConstant.ARC_DEGREE) || line.startsWith(OpenAirConstant.ARC_COORD)
				|| line.startsWith(OpenAirConstant.CIRCLE) || line.startsWith(OpenAirConstant.SEGMENT)) {
			currentAirspace.addShape(convertToAngularValue(line));
		}
	}

	private String convertToAngularValue(String line) {
		// Replace all coordinate with minute or second to angular coordinate
		StringBuffer sb = new StringBuffer();
		Matcher currentMatch;
		Coordinate currentCoord;
		if ((currentMatch = centerPattern.matcher(line)).matches()) {
			currentCoord = CoordinateParser.parseCoordinate(currentMatch.group(1), currentMatch.group(2),
					currentMatch.group(3), currentMatch.group(4));
			sb.append("V X=");
			sb.append(currentCoord.getLatitude());
			sb.append(" ");
			sb.append(currentCoord.getLongitude());
			return sb.toString();
		}
		if ((currentMatch = drawPolygonPointPattern.matcher(line)).matches()) {
			currentCoord = CoordinateParser.parseCoordinate(currentMatch.group(1), currentMatch.group(2),
					currentMatch.group(3), currentMatch.group(4));
			sb.append("DP ");
			sb.append(currentCoord.getLatitude());
			sb.append(" ");
			sb.append(currentCoord.getLongitude());
			return sb.toString();
		}
		if ((currentMatch = drawArcWithCoordinatePattern.matcher(line)).matches()) {
			currentCoord = CoordinateParser.parseCoordinate(currentMatch.group(1), currentMatch.group(2),
					currentMatch.group(3), currentMatch.group(4));
			sb.append("DB ");
			sb.append(currentCoord.getLatitude());
			sb.append(" ");
			sb.append(currentCoord.getLongitude());
			sb.append(",");
			currentCoord = CoordinateParser.parseCoordinate(currentMatch.group(5), currentMatch.group(6),
					currentMatch.group(7), currentMatch.group(8));
			sb.append(currentCoord.getLatitude());
			sb.append(" ");
			sb.append(currentCoord.getLongitude());
			return sb.toString();
		}
		return line;
	}
}
