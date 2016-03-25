package nl.group11.planplan;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Anne on 25/03/2016.
 */
public class BaseActivity extends AppCompatActivity {
    protected PlanPlan planPlan;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        planPlan = (PlanPlan)this.getApplicationContext();
    }
    protected void onResume() {
        super.onResume();
        planPlan.setCurrentActivity(this);
    }
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = planPlan.getCurrentActivity();
        if (this.equals(currActivity))
            planPlan.setCurrentActivity(null);
    }
}
