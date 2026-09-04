package egps2.modulei;

import org.apache.commons.lang3.tuple.Triple;

import egps2.Authors;

/**
 * The CreditBean class holds information about the development team,
 * developers, and website. This class initializes its fields using data from
 * the Authors utility.
 */
public class CreditBean {
	
	private String team ;
	
	private String developers;
	
	private String webSite;

	public CreditBean() {
		super();
		
		Triple<String, String, String> yudalangAuthors = Authors.getYudalangAuthors();
		team = yudalangAuthors.getLeft();
		developers = "The EvolGene eGPS development team";
		webSite = yudalangAuthors.getRight();
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getDevelopers() {
		return developers;
	}

	public void setDevelopers(String developers) {
		this.developers = developers;
	}

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}
	
	

}
