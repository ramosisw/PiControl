package mx.itson.picontrol.vistas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import mx.itson.picontrol.R;

public class Splash extends Activity {

    private static final int SPLASH_DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        //Get Image for roation
        final View contentView = findViewById(R.id.fullscreen_content);
        Animation rotar = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        rotar.reset();
        contentView.setAnimation(rotar);

        Handler handler = new Handler();

        // run a thread after 2 seconds to start the home screen
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // make sure we close the splash screen so the user won't come back when it presses back key
                finish();
                // start the home screen if the back button wasn't pressed already
                Intent intent = new Intent(Splash.this, Startup.class);
                Splash.this.startActivity(intent);

            }
        }, SPLASH_DURATION); // time in milliseconds (1 second = 1000 milliseconds) until the run() method will be called
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
