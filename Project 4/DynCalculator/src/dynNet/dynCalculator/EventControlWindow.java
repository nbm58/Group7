package dynNet.dynCalculator;

import java.awt.event.*;
import javax.swing.*;


	
/**
 * Class [EventControlWindow]
 * <p>
 * Event processing for the DynCalculator
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 * @version Feb. 2018
 */
class EventControlWindow extends WindowAdapter{

	// When the close button is clicked, close the window and terminate the application
	public void windowClosing(WindowEvent theWindowEvent){
		((JFrame) theWindowEvent.getSource()).dispose();
		System.exit(0);
	}
}