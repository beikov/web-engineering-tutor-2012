package at.ac.big.tuwien.ewa;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;

import at.ac.big.tuwien.ewa.pages.HomePage;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see ac.ac.big.tuwien.ewa.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{    
	
	/** Defines wether the application shall be started in develop or deployment mode */
	private static RuntimeConfigurationType CONFIGURATION_TYPE = RuntimeConfigurationType.DEPLOYMENT;
	
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<HomePage> getHomePage()
	{
		return HomePage.class;
	}
	
	
	/**
	 * Returns the configuration type (develop or deployment)
	 */
	@Override
	public RuntimeConfigurationType getConfigurationType() {
		
		return CONFIGURATION_TYPE;

	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();
		
				
		

	}
}
