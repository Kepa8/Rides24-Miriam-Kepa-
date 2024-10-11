package dataAccess;

import java.util.Date;

public class ErreklamazioaBidaliInfo {
	public String nor;
	public String nori;
	public Date gaur;
	public String textua;

	public ErreklamazioaBidaliInfo(String nor, String nori, Date gaur, String textua) {
		this.nor = nor;
		this.nori = nori;
		this.gaur = gaur;
		this.textua = textua;
	}
}