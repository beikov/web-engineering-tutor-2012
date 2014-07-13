package at.ac.big.tuwien.ewa.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import at.ac.big.tuwien.ewa.panels.DetailsPanel;
import at.ac.big.tuwien.ewa.persistence.DataStore;
import at.ac.big.tuwien.ewa.persistence.DataStoreEntry;
import at.ac.big.tuwien.ewa.ws.generated.TournamentType;

/**
 * Page for showing the high score results of web engineering
 * @author pl
 *
 */
public class HomePage extends BasePage {
	private static final long serialVersionUID = 1L;

	 DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.YYYY HH:mm:ss");
	
    public HomePage() {
		   
    	
		//Create a table with the received highscore results
		ListView<DataStoreEntry> listview = new ListView<DataStoreEntry>("repeater", DataStore.getInstance().getEntries()) {
			@Override
			protected void populateItem(ListItem <DataStoreEntry> item) {
					
					DataStoreEntry entry = item.getModelObject();
				
					item.add(new Label("received", Model.of(fmt.print(entry.getSubmitted()))));
					item.add(new Label("id", Model.of(entry.getId())));
					item.add(new DetailsPanel("detailsPanel", new CompoundPropertyModel<TournamentType>(entry.getTournament())));
			}
			
		};
		this.add(listview);
    	
    	
    }
}
