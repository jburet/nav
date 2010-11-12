package fr.jburet.nav.database.airspace;

public class Airspace {

	public static final String A = "A";
	public static final String B = "B";
	public static final String C = "C";
	public static final String D = "D";
	public static final String E = "E";
	public static final String F = "F";
	public static final String G = "G";
	public static final String RESTRICTED = "R";
	public static final String DANGER = "D";
	public static final String PROHIBITED = "P";
	public static final String CTR = "CTR";
	public static final String WAVE_WINDOWS = "W";
	public static final String GLIDER_PROHIBITED = "GP";

	private int id;
	private String code;
	private String longName;
	private String description;
	private String frequency;
	private String classe;
	private String altitudeBottom;
	private String altitudeTop;
	private String shape = "";
	private float minLatitude;
	private float maxLatitude;
	private float minLongitude;
	private float maxLongitude;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public void addShape(String line) {
		shape = shape + line + ";";
	}

	public String getAltitudeBottom() {
		return altitudeBottom;
	}

	public void setAltitudeBottom(String altitudeBottom) {
		this.altitudeBottom = altitudeBottom;
	}

	public String getAltitudeTop() {
		return altitudeTop;
	}

	public void setAltitudeTop(String altitudeTop) {
		this.altitudeTop = altitudeTop;
	}

	public float getMinLatitude() {
		return minLatitude;
	}

	public void setMinLatitude(float minLatitude) {
		this.minLatitude = minLatitude;
	}

	public float getMaxLatitude() {
		return maxLatitude;
	}

	public void setMaxLatitude(float maxLatitude) {
		this.maxLatitude = maxLatitude;
	}

	public float getMinLongitude() {
		return minLongitude;
	}

	public void setMinLongitude(float minLongitude) {
		this.minLongitude = minLongitude;
	}

	public float getMaxLongitude() {
		return maxLongitude;
	}

	public void setMaxLongitude(float maxLongitude) {
		this.maxLongitude = maxLongitude;
	}

}
