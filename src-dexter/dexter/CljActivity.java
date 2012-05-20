package dexter;

import android.app.Activity;
import android.os.Bundle;

/** Your clojure classes should inherit from this.
 * 
 * They can get access to the regular activity class through .activity
 * and override whatever methods they need to.
 */

public class CljActivity {
    private Activity activity;
    public CljActivity(Activity activity) {
        this.activity = activity;
    }
    public void onCreate(Bundle savedInstanceState) { };
    protected Activity getActivity() {
        return activity;
    }
}
