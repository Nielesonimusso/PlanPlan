package nl.group11.planplan;

import android.app.Activity;
import android.app.Application;

/**
 * Created by Anne on 25/03/2016.
 */
public class PlanPlan extends Application {
    public void onCreate() {
        super.onCreate();
    }

    private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }
}