package gdx.entities;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class Main extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	
	
	
	@Override
	public void create () {
		dropImage = new Texture(Gdx.files.internal("images/water/water_drop.png"));
		bucketImage = new Texture(Gdx.files.internal("images/objetos/bucket.png"));
		
		dropSound = Gdx.audio.newSound(Gdx.files.internal("sound/water/waterdrop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/water/rain.mp3"));
		
		rainMusic.setLooping(true);
		rainMusic.play();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		batch = new SpriteBatch();
		
		bucket = new Rectangle();
		bucket.x = Gdx.graphics.getHeight() / 2 - bucketImage.getWidth() / 2; 
		bucket.y = 20;
		bucket.width = bucketImage.getWidth();
		bucket.height = bucketImage.getHeight();
		
		raindrops = new Array<>();
		spawnRaindrop();
		
		
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for(Rectangle raindrop : raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();
		
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - bucket.getWidth() / 2;
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();
		
		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > Gdx.graphics.getWidth() - bucket.getWidth()) bucket.x = Gdx.graphics.getWidth() - bucket.getWidth();
		
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
		for(java.util.Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext();) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + raindrop.getHeight() < 0) iter.remove();
			if(raindrop.overlaps(bucket)) {
				dropSound.play();
				iter.remove();
			}
		}
		
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		dropImage.dispose();
		bucketImage.dispose();
	}
	
	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, Gdx.graphics.getWidth() - dropImage.getWidth());
		raindrop.y = Gdx.graphics.getHeight();
		raindrop.width = dropImage.getWidth();
		raindrop.height = dropImage.getHeight();
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
}
