package fr.jburet.nav.database.airspace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.jburet.nav.database.DatabaseHelper;

public class AirspaceQueryImpl implements AirspaceQuery {

	private DatabaseHelper databaseHelper;

	private AirspaceMapper mapper;

	public AirspaceQueryImpl(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
		this.mapper = new AirspaceMapper();
	}

	public void delete(Airspace entity) {
		// TODO Auto-generated method stub

	}

	public void deleteByPk(Serializable pk) {
		// TODO Auto-generated method stub

	}

	public Airspace selectByPk(Serializable pk) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Airspace> listAll() {
		List<Airspace> res = new ArrayList<Airspace>();
		Airspace airspace = new Airspace();
		airspace.setClasse(Airspace.RESTRICTED);
		airspace.setCode("R6A");
		airspace.setDescription("R6A Mailly");
		airspace.setFrequency("120.95");
		airspace.setAltitudeBottom(0);
		airspace.setAltitudeTop(5500);
		airspace.setLongName("R6A Mailly");
		airspace.setShape("V X=48:39:00 N 004:19:00 E;DB 48:40:03 N 004:26:11 E,48:43:32 N 004:16:22 E");
		res.add(airspace);

		airspace = new Airspace();
		airspace.setClasse(Airspace.RESTRICTED);
		airspace.setCode("R6D");
		airspace.setDescription("R6D Mailly");
		airspace.setFrequency("120.95");
		airspace.setAltitudeBottom(14500);
		airspace.setAltitudeTop(19500);
		airspace.setLongName("R6D Mailly");
		airspace.setShape("V X=48:39:00 N 004:19:00 E;DC 4.86");
		res.add(airspace);

		airspace = new Airspace();
		airspace.setClasse(Airspace.RESTRICTED);
		airspace.setDescription("R4A RAI");
		airspace.setFrequency("118.62");
		airspace.setLongName("R4A Suippe");
		airspace.setAltitudeBottom(0);
		airspace.setAltitudeTop(1000);
		airspace.setShape("DP 49:07:20 N 004:40:06 E;DP 48:54:00 N 004:48:00 E;DP 48:52:31 N 004:40:10 E;DP 49:04:19 N 004:32:38 E");
		res.add(airspace);

		airspace = new Airspace();
		airspace.setClasse(Airspace.RESTRICTED);
		airspace.setDescription("R4B RAI");
		airspace.setFrequency("118.62");
		airspace.setLongName("R4B Suippe");
		airspace.setAltitudeBottom(1000);
		airspace.setAltitudeTop(3000);
		airspace.setShape("DP 49:11:30 N 004:25:00 E;DP 49:06:30 N 004:21:30 E;DP 49:02:06 N 004:27:08 E;DP 49:04:19 N 004:32:38 E;DP 48:52:31 N 004:40:10 E;DP 49:02:06 N 004:10:25 E;DP 49:07:23 N 004:10:25 E");
		res.add(airspace);
		
		return res;
	}

	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	public List<Airspace> listAirspaceInPolygon(float topLatitude, float leftLongitude, float bottomLatitude,
			float rigthLongitude) {
		return listAll();
	}

}
