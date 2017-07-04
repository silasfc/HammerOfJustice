package facto.jedm.hammerofjustice;

import android.cg.com.megavirada.AndGraph.AGGameManager;
import android.cg.com.megavirada.AndGraph.AGInputManager;
import android.cg.com.megavirada.AndGraph.AGScene;
import android.cg.com.megavirada.AndGraph.AGScreenManager;
import android.cg.com.megavirada.AndGraph.AGSoundManager;
import android.cg.com.megavirada.AndGraph.AGSprite;

public class MenuScene extends AGScene {
    private static final String TAG = "Log de Menu";
    AGSprite playButton = null;
    AGSprite creditsButton = null;
    AGSprite exitButton = null;
    int codigoEfeito = 0;

    public MenuScene(AGGameManager pManager) {
        super(pManager);
    }

    @Override
    public void init() {
        codigoEfeito = AGSoundManager.vrSoundEffects.loadSoundEffect("abutre.mp4");

        // Criando e definindo botão jogar
        playButton = this.createSprite(R.drawable.play, 1, 1);
        playButton.setScreenPercent(8, 4);
        playButton.vrPosition.setX(AGScreenManager.iScreenWidth / 2);
        playButton.vrPosition.setY(AGScreenManager.iScreenHeight / ( (float) 4 / 3 ) );

        // Criando e definindo botão créditos
        creditsButton = this.createSprite(R.drawable.about, 1, 1);
        creditsButton.setScreenPercent(8, 4);
        creditsButton.vrPosition.setX(AGScreenManager.iScreenWidth / 2);
        creditsButton.vrPosition.setY(AGScreenManager.iScreenHeight / 4 / 2 );

        // Criando e definindo botão exit
        exitButton = this.createSprite(R.drawable.exit, 1, 1);
        exitButton.setScreenPercent(8, 4);
        exitButton.vrPosition.setX(AGScreenManager.iScreenWidth / 2);
        exitButton.vrPosition.setY(AGScreenManager.iScreenHeight / 4 / 1 );

        // Carregando plano de fundo
        setSceneBackgroundColor("#FFF600");

        // Carregando trilha sonora e mantendo-a em repetição
        AGSoundManager.vrMusic.loadMusic("abutre.mp4", true);
        //AGSoundManager.vrMusic.play();
    }

    @Override
    public void restart() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void loop() {
        if (playButton.collide(AGInputManager.vrTouchEvents.getLastPosition()))
            vrGameManager.setCurrentScene(1);

        if (creditsButton.collide(AGInputManager.vrTouchEvents.getLastPosition()))
            vrGameManager.setCurrentScene(2);

        if (exitButton.collide(AGInputManager.vrTouchEvents.getLastPosition()))
            vrGameManager.vrActivity.finish();
    }
}
