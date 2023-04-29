package appserver.server;

import java.util.ArrayList;

/**
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class LoadManager {

    static ArrayList satellites = null;
    static int lastSatelliteIndex = -1;

    public LoadManager() {
        satellites = new ArrayList<String>();
    }

    public void satelliteAdded(String satelliteName) {
        // add satellite
        satellites.add(satelliteName);
    }


    public String nextSatellite() throws Exception {
        
        int numberSatellites = satellites.size();
        String nextSatellite = null;
        
        synchronized (satellites) {
            // implement policy that returns the satellite name according to a round robin methodology
            if (lastSatelliteIndex == numberSatellites - 1)
            {
                lastSatelliteIndex = -1;
            }
            
            nextSatellite = (String) satellites.get(lastSatelliteIndex + 1);
            lastSatelliteIndex++;
        }

        return nextSatellite;
    }
}
