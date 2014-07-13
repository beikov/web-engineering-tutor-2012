package at.ac.big.tuwien.ewa.panels;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import at.ac.big.tuwien.ewa.ws.generated.TournamentType;

/**
 * This panel is used to show the details of a submitted highscore report
 * 
 * @author pl
 * 
 */
public class DetailsPanel extends Panel {

	SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.YYYY");

	public DetailsPanel(String id, CompoundPropertyModel<TournamentType> model) {
		super(id, model);

		// Add username, date of birth and gender
		add(new Label("players.player[0].username"));		
		//Retrieve the date of birth manually		
		add(new Label("players.player[0].dateOfBirth", Model.of(formatter.format(model.getObject().getPlayers().getPlayer().get(0).getDateOfBirth().getTime()))));
		add(new Label("players.player[0].gender"));
		
		
		//Add the details about the game
		
		//Date
		add(new Label("rounds.round[0].game[0].date", Model.of(formatter.format(model.getObject().getRounds().getRound().get(0).getGame().get(0).getDate().getTime()))));
		//Status
		add(new Label("rounds.round[0].game[0].status"));
		//Dauer
		add(new Label("rounds.round[0].game[0].duration"));
		//Gewinner
		add(new Label("rounds.round[0].game[0].winner"));

		
		
		

	}

}
