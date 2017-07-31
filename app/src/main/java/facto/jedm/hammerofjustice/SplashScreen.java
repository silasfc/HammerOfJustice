package facto.jedm.hammerofjustice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SplashScreen extends AppCompatActivity {
    // TODO: 05/07/17 Providenciar arte para exibição na animação inicial do jogo
    private static final int TIME_SPLASH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // Deixar a tela sem barra de navegação
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openGameMenu();
            }
        }, TIME_SPLASH);
    }

    private void openGameMenu() {
        startActivity(new Intent(SplashScreen.this, LoadScenes.class));
        finish();
    }
}
