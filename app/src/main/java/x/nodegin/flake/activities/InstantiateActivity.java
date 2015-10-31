package x.nodegin.flake.activities;

public class InstantiateActivity extends android.app.Activity {

    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!x.nodegin.flake.services.FlakeService.SERVICE_RUNNING)
            startService(new android.content.Intent(this, x.nodegin.flake.services.FlakeService.class));
        else
            android.widget.Toast.makeText(
                    this,
                    x.nodegin.flake.R.string.msg_already_service_running,
                    android.widget.Toast.LENGTH_SHORT).show();
        finishAffinity();
    }

}