package x.nodegin.flake.services;

public class FlakeService extends android.app.Service {

    static public boolean SERVICE_RUNNING = false;

    final private String ACTION_TERMINATE = "flake.TERMINATE";

    public x.nodegin.flake.Flake App;
    public android.view.WindowManager Manager;
    private android.content.BroadcastReceiver Receiver;
    private android.widget.FrameLayout Layout;
    private android.view.WindowManager.LayoutParams Params;

    final private int[] FLAKES = new int[] {
            x.nodegin.flake.R.drawable.flake0,
            x.nodegin.flake.R.drawable.flake1,
            x.nodegin.flake.R.drawable.flake2,
            x.nodegin.flake.R.drawable.flake3,
            x.nodegin.flake.R.drawable.flake4
    };
    final private int MAX_FLAKE = 100;
    private java.util.LinkedList<android.widget.ImageView> Flakes = new java.util.LinkedList<>();

    public android.os.IBinder onBind(android.content.Intent intent) {
        return null;
    }

    public void onCreate() {
        App = (x.nodegin.flake.Flake) getApplication();
        Manager = (android.view.WindowManager) getSystemService(android.content.Context.WINDOW_SERVICE);
        measureDimensions();
        registerBroadcastReceiver();
        persistService();
        snowfall();
        SERVICE_RUNNING = true;
    }

    private void measureDimensions() {
        android.view.Display display = Manager.getDefaultDisplay();
        final android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
        display.getRealMetrics(metrics);
        App.DIMEN_SCREEN_X = metrics.widthPixels;
        App.DIMEN_SCREEN_Y = metrics.heightPixels;
    }

    private void persistService() {
        startForeground(4689, new android.app.Notification.Builder(this)
                .setContentIntent(android.app.PendingIntent.getBroadcast(
                        getApplicationContext(),
                        0,
                        new android.content.Intent(ACTION_TERMINATE),
                        android.app.PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentTitle("Flake")
                .setContentText("Tap to exit application")
                .setTicker("Flake service now running")
                .setSmallIcon(x.nodegin.flake.R.drawable.flake4)
                .setPriority(android.app.Notification.PRIORITY_HIGH)
                .build());
    }

    private void registerBroadcastReceiver() {
        android.content.IntentFilter filter = new android.content.IntentFilter();
        filter.addAction(ACTION_TERMINATE);
        filter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        filter.addAction("android.intent.action.USER_PRESENT");
        filter.addAction("android.intent.action.SCREEN_OFF");
        Receiver = new android.content.BroadcastReceiver() {
            public void onReceive(android.content.Context context,  android.content.Intent intent) {
                // intent.getStringExtra("reason") #=> recentapps / homekey
                String action = intent.getAction();
                if (action.equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {}
                    //Central.collapse();
                else if (action.equals(ACTION_TERMINATE))
                    terminate();
            }
        };
        registerReceiver(Receiver, filter);
    }


    private void snowfall() {
        java.util.Random random = new java.util.Random();
        Layout = new android.widget.FrameLayout(this);
        Params = new android.view.WindowManager.LayoutParams(
                App.DIMEN_SCREEN_X,
                App.DIMEN_SCREEN_Y,
                android.view.WindowManager.LayoutParams.TYPE_PHONE,
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |
                        android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                android.graphics.PixelFormat.TRANSPARENT);
        Params.gravity = android.view.Gravity.TOP | android.view.Gravity.LEFT;

        for (int i = 0; i < MAX_FLAKE; i++) {
            android.widget.ImageView flake = new android.widget.ImageView(this);
            flake.setImageResource(FLAKES[random.nextInt(FLAKES.length)]);
            flake.setX(getInitialX(flake.getWidth()));
            flake.setY(getInitialY(flake.getHeight()));
            Flakes.add(flake);
            android.widget.FrameLayout.LayoutParams param = new android.widget.FrameLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.Gravity.TOP | android.view.Gravity.LEFT);
            Layout.addView(flake, param);
        }
        Manager.addView(Layout, Params);
        onFrame();
    }

    public int getInitialX (int flakeWidth) {
        return (int) Math.round(Math.random() * Params.width) - flakeWidth;
    }

    public int getInitialY (int flakeHeight) {
        return (int) Math.round(Math.random() * Params.height * -2) - flakeHeight;
    }

    private void onFrame() {
        for (android.view.View flake : Flakes) {
            float x = flake.getX();
            float y = flake.getY() + 5;
            if (y > Params.height) {
                x = getInitialX(flake.getWidth());
                y = getInitialY(flake.getHeight());
            }
            flake.setX(x);
            flake.setY(y);
        }
        new android.os.Handler().postDelayed(new java.lang.Runnable() {
            public void run() {
                onFrame();
            }
        }, 1000 / 40);
    }

    public void onConfigurationChanged(android.content.res.Configuration config) {
        super.onConfigurationChanged(config);
        boolean portrait = config.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT;
        int oldWidth = Params.width;
        Params.width = portrait ? App.DIMEN_SCREEN_X : App.DIMEN_SCREEN_Y;
        Params.height = portrait ? App.DIMEN_SCREEN_Y : App.DIMEN_SCREEN_X;
        for (android.view.View flake : Flakes) {
            float newX = (flake.getX() / oldWidth) * Params.width;
            flake.setX(newX);
        }
        Manager.updateViewLayout(Layout, Params);
    }

    public void terminate() {
        unregisterReceiver(Receiver);
        stopForeground(true);
        stopSelf();
        Manager.removeViewImmediate(Layout);
        SERVICE_RUNNING = false;
    }

}
