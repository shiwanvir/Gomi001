package info.gomi.gomi001;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class SendAdNotification {


    public  SendAdNotification(String message,String heading,String notificationKey){

        try {
            JSONObject notificationcontent=new JSONObject("{'contents':{'en':'" + message+"'},"+
                    "'include_player_ids':['"+notificationKey+"'],"+
                    "'headings':{'en':'"+heading+"'}}");
            OneSignal.postNotification(notificationcontent,null);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
