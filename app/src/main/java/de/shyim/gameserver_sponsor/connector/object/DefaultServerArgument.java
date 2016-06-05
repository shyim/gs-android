package de.shyim.gameserver_sponsor.connector.object;

import org.json.JSONException;
import org.json.JSONObject;

public class DefaultServerArgument extends JSONObject {
    public DefaultServerArgument (Integer gsID) {
        try {
            put("gsID", gsID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
