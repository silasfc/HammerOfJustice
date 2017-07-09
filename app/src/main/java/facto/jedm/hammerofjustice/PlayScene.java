package facto.jedm.hammerofjustice;

import android.cg.com.megavirada.AndGraph.AGGameManager;
import android.cg.com.megavirada.AndGraph.AGInputManager;
import android.cg.com.megavirada.AndGraph.AGScene;
import android.cg.com.megavirada.AndGraph.AGScreenManager;
import android.cg.com.megavirada.AndGraph.AGSoundManager;
import android.cg.com.megavirada.AndGraph.AGSprite;
import android.cg.com.megavirada.AndGraph.AGTimer;

import java.util.ArrayList;

public class PlayScene extends AGScene {
    // Cria o Array de sprites do scoreboard
    AGSprite[] scoreboard = new AGSprite[6];

    // Cria o vetor de tiros
    ArrayList<AGSprite> hammerVector = null;
    ArrayList<AGSprite> condemnationVector = null;

    AGSprite[] vultures = new AGSprite[2];

    // Cria o tempo para controle de movimento de Coro
    AGTimer couroTime = null;
    AGTimer hammerTime = null;

    // Cria a variavel para armazenar o cod efeito som
    int effectMovement = 0;
    int effectDowncastVulture = 0;
    int score = 0;
    int scoreTime = 0;

    // Cria sprites de fundo e do couro
    //AGSprite background = null;
    AGSprite couro = null;
    AGSprite topBar = null;

    boolean bPause = false;

    public PlayScene(AGGameManager pManager) {
        super(pManager);
    }

    @Override
    public void init() {
        // Carrega as imagens na memoria
        createSprite(R.drawable.hammer, 1, 1).bVisible = false;
        createSprite(R.drawable.comndenation, 4, 2).bVisible = false;

        hammerVector = new ArrayList<AGSprite>();
        condemnationVector = new ArrayList<AGSprite>();

        // Seta a cor do fundo
        setSceneBackgroundColor(0, 0, 0);

        // Carrega a imagem de fundo 100x100 no centro da tela
        //background = createSprite(R.drawable.background_play, 1, 1);
        //background.setScreenPercent(100, 100);
        //background.vrPosition.setX(AGScreenManager.iScreenWidth / 2);
        //background.vrPosition.setY(AGScreenManager.iScreenHeight / 2);

        // Carrega a imagem de Coro na base da tela
        couro = createSprite(R.drawable.ferreo_coro, 1, 1);
        couro.setScreenPercent(12, 20);
        couro.vrPosition.setX(AGScreenManager.iScreenWidth / 2);
        couro.vrPosition.setY(couro.getSpriteHeight() / 2);

        topBar = createSprite(R.drawable.top_bar, 1, 1);
        topBar.setScreenPercent(100, 10);
        topBar.vrPosition.fX = AGScreenManager.iScreenWidth / 2;
        topBar.vrPosition.fY = AGScreenManager.iScreenHeight - topBar.getSpriteHeight() / 2;
        topBar.bAutoRender = false;

        // Configura os sprites do placar
        int multiplier = 1;
        for (int pos = 0; pos < scoreboard.length; pos++) {
            scoreboard[pos] = createSprite(R.drawable.font, 4, 4);
            scoreboard[pos].setScreenPercent(8, 8);
            scoreboard[pos].vrPosition.fY = topBar.vrPosition.fY;
            scoreboard[pos].vrPosition.fX = 20 + multiplier * scoreboard[pos].getSpriteWidth();
            scoreboard[pos].bAutoRender = false;
            multiplier++;
            for (int i = 0; i < 10; i++) {
                scoreboard[pos].addAnimation(1, false, i);
            }
        }

        // Setando tempo de execucao de movimentos de Couro e do Martelo
        couroTime = new AGTimer(50);
        hammerTime = new AGTimer(250);

        // Criando efeitos sonoros para movimento de Couro e Condenação
        effectMovement = AGSoundManager.vrSoundEffects.loadSoundEffect("toc.wav");
        effectDowncastVulture = AGSoundManager.vrSoundEffects.loadSoundEffect("downcast_vulture.mp4");

        // Carrega os sprites dos vultures
        vultures[0] = createSprite(R.drawable.molusco, 1, 1);
        vultures[0].setScreenPercent(20, 12);
        vultures[0].iMirror = AGSprite.HORIZONTAL;
        vultures[0].vrDirection.fX = 1;
        vultures[0].vrPosition.fX = -vultures[0].getSpriteWidth() / 2;
        vultures[0].vrPosition.fY = AGScreenManager.iScreenHeight - vultures[0].getSpriteHeight() / 2 - topBar.getSpriteHeight();

        vultures[1] = createSprite(R.drawable.vampirao, 1, 1);
        vultures[1].setScreenPercent(20, 12);
        vultures[1].vrDirection.fX = -1;
        vultures[1].vrPosition.fX = AGScreenManager.iScreenWidth + vultures[1].getSpriteWidth() / 2;
        vultures[1].vrPosition.fY = vultures[0].vrPosition.fY - vultures[1].getSpriteHeight();
    }

    @Override
    public void render() {
        super.render();
        topBar.render();
        for (AGSprite digito : scoreboard) {
            digito.render();
        }
    }

    @Override
    public void restart() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void loop() {
        if (AGInputManager.vrTouchEvents.backButtonClicked()) {
            vrGameManager.setCurrentScene(0);
            bPause = !bPause;
            return;
        }

        if (bPause == false) {
            updateCouroMovement();
            updateHammers();
            createHammers();
            updateVultures();
            verificaColisaoBalasNavios();
            updateCondemnations();
            updateScoreboard();
        }
    }

    // Metodo criado para atualizar quadros do scoreboard
    private void updateScoreboard() {
        if (scoreTime > 0) {
            //for (AGSprite digito : scoreboard) {
            //    digito.bVisible = !digito.bVisible;
            //}
            scoreTime--;
            score++;
        } else {
            for (AGSprite digito : scoreboard) {
                digito.bVisible = true;
            }
        }

        scoreboard[5].setCurrentAnimation(score % 10);
        scoreboard[4].setCurrentAnimation((score % 100) / 10);
        scoreboard[3].setCurrentAnimation((score % 1000) / 100);
        scoreboard[2].setCurrentAnimation((score % 10000) / 1000);
        scoreboard[1].setCurrentAnimation((score % 100000) / 10000);
        scoreboard[0].setCurrentAnimation((score % 1000000) / 100000);

    }

    // Metodo utilizado para reciclar as explosoes
    private void updateCondemnations() {
        for (AGSprite comndenation : condemnationVector) {
            if (comndenation.getCurrentAnimation().isAnimationEnded()) {
                comndenation.bRecycled = true;
            }
        }
    }

    // Metodo utilizado para criar uma comndenation
    private void criaExplosao(float x, float y) {
        for (AGSprite comndenation : condemnationVector) {
            if (comndenation.bRecycled) {
                comndenation.bRecycled = false;
                comndenation.getCurrentAnimation().restart();
                comndenation.vrPosition.fX = x;
                comndenation.vrPosition.fY = y;
                return;
            }
        }

        AGSprite novaExplosao = createSprite(R.drawable.comndenation, 4, 2);
        novaExplosao.setScreenPercent(20, 12);
        novaExplosao.addAnimation(10, false, 0, 7);
        novaExplosao.vrPosition.fX = x;
        novaExplosao.vrPosition.fY = y;
        condemnationVector.add(novaExplosao);
    }

    // Metodo que verifica a colisao entre b alas e vultures
    private void verificaColisaoBalasNavios() {
        for (AGSprite hammer : hammerVector) {
            if (hammer.bRecycled) {
                continue;
            }
            for (AGSprite navio : vultures) {
                if (hammer.collide(navio)) {
                    scoreTime += 50;
                    criaExplosao(navio.vrPosition.fX, navio.vrPosition.fY);
                    hammer.bRecycled = true;
                    hammer.bVisible = false;
                    AGSoundManager.vrSoundEffects.play(effectDowncastVulture);

                    if (navio.vrDirection.fX == 1) {
                        navio.vrDirection.fX = -1;
                        navio.iMirror = AGSprite.NONE;
                        navio.vrPosition.fX = AGScreenManager.iScreenWidth + navio.getSpriteWidth() / 2;
                    } else {
                        navio.vrDirection.fX = 1;
                        navio.iMirror = AGSprite.HORIZONTAL;
                        navio.vrPosition.fX = -navio.getSpriteWidth();
                    }
                    break;
                }
            }

        }
    }

    // Metodo que atualiza a posicao dos vultures
    private void updateVultures() {
        for (AGSprite navio : vultures) {
            navio.vrPosition.fX += 5 * navio.vrDirection.fX;
            if (navio.vrDirection.fX == 1) {
                if (navio.vrPosition.fX > AGScreenManager.iScreenWidth + navio.getSpriteWidth() / 2) {
                    navio.iMirror = AGSprite.NONE;
                    navio.vrDirection.fX = -1;
                }
            } else {
                if (navio.vrPosition.fX <= -navio.getSpriteWidth() / 2) {
                    navio.iMirror = AGSprite.HORIZONTAL;
                    navio.vrDirection.fX = 1;
                }
            }
        }
    }

    // Coloca uma hammer no vetor de tiros
    private void createHammers() {
        hammerTime.update();

        // Tenta reciclar uma hammer criada anteriormente
        if (AGInputManager.vrTouchEvents.screenClicked()) {
            if (!hammerTime.isTimeEnded()) {
                return;
            }

            hammerTime.restart();

            for (AGSprite hammer : hammerVector) {
                if (hammer.bRecycled) {
                    hammer.bRecycled = false;
                    hammer.bVisible = true;
                    hammer.vrPosition.fX = couro.vrPosition.fX;
                    hammer.vrPosition.fY = couro.getSpriteHeight() + hammer.getSpriteHeight() / 2;
                    return;
                }
            }

            AGSprite novaBala = createSprite(R.drawable.hammer, 1, 1);
            novaBala.setScreenPercent(8, 5);
            novaBala.vrPosition.fX = couro.vrPosition.fX;
            novaBala.vrPosition.fY = couro.getSpriteHeight() + novaBala.getSpriteHeight() / 2;
            hammerVector.add(novaBala);
        }
    }

    // metodo para atualizar o movimento das hammers
    private void updateHammers() {
        for (AGSprite hammer : hammerVector) {
            hammer.vrPosition.fY += 10;
            hammer.fAngle += 7;

            if (hammer.vrPosition.fY > AGScreenManager.iScreenHeight + hammer.getSpriteHeight() / 2) {
                hammer.bRecycled = true;
                hammer.bVisible = false;
            }
        }
    }

    // Metodo criado para movimentar
    private void updateCouroMovement() {
        couroTime.update();
        if (couroTime.isTimeEnded()) {
            couroTime.restart();
            if (AGInputManager.vrAccelerometer.getAccelX() > 2.0f) {
                if (couro.vrPosition.getX() <= AGScreenManager.iScreenWidth - couro.getSpriteWidth() / 2) {
                    AGSoundManager.vrSoundEffects.play(effectMovement);
                    couro.vrPosition.setX(couro.vrPosition.getX() + 10);
                }
            } else if (AGInputManager.vrAccelerometer.getAccelX() < -2.0f) {
                if (couro.vrPosition.getX() > 0 + couro.getSpriteWidth() / 2) {
                    AGSoundManager.vrSoundEffects.play(effectMovement);
                    couro.vrPosition.setX(couro.vrPosition.getX() - 10);
                }
            }
        }
    }
}
