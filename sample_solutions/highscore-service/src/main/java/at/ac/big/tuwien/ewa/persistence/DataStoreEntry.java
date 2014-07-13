package at.ac.big.tuwien.ewa.persistence;

import org.joda.time.DateTime;

import at.ac.big.tuwien.ewa.ws.generated.TournamentType;

/**
 * This class is used to wrap a data store entry
 * @author pl
 *
 */
public class DataStoreEntry  {

		
	private DateTime submitted;
	private String id;
	private TournamentType tournament;
	public DateTime getSubmitted() {
		return submitted;
	}
	public void setSubmitted(DateTime submitted) {
		this.submitted = submitted;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public TournamentType getTournament() {
		return tournament;
	}
	public void setTournament(TournamentType tournament) {
		this.tournament = tournament;
	}
	@Override
	public String toString() {
		return "DataStoreEntry [submitted=" + submitted + ", id=" + id
				+ ", tournament=" + tournament + "]";
	}

	
	
	
	
	
	
	
}
