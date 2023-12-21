package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import org.w3c.dom.Text;

public class Zaebalsa extends ApplicationAdapter {
	private boolean moveLeft = false;
	private boolean moveRight = false;
	private Stage stage;
	private Skin skin;
	private Texture backgroundTexture;
	private SpriteBatch batch;
	private Texture playerTexture;
	private Texture groundTexture;
	private Vector2 playerPosition=new Vector2(0,0);
	private Vector2 playerVelocity=new Vector2(0,0);
	private Rectangle groundRect;
	private OrthographicCamera camera;
	private TiledMap map;
	private AssetManager assetManager;
	private OrthogonalTiledMapRenderer tiledMapRenderer;
	private TiledMapTileLayer collisionLayer;

	private float gravity = -2f; // Гравитация
	private float jumpVelocity = 30f; // Скорость прыжка
	private float moveSpeed = 5f; // Скорость движения

	@Override
	public void create () {

		batch = new SpriteBatch();
		playerTexture = new Texture("player.png");
		backgroundTexture = new Texture("sky.png");
// Создаем загрузчик карты
		TmxMapLoader.Parameters parameters = new TmxMapLoader.Parameters();
		parameters.textureMinFilter = Texture.TextureFilter.Linear;
		parameters.textureMagFilter = Texture.TextureFilter.Linear;

		// Используем FileHandleResolver для разрешения путей к ресурсам
		FileHandleResolver resolver = new FileHandleResolver() {
			@Override
			public FileHandle resolve(String fileName) {

				// Про
				// веряем имя файла тайлсета и возвращаем соответствующий FileHandle
				if (fileName.equals("TX Village Props.tsx")) {
					return Gdx.files.internal("TX Village Props.tsx");
				}
				// Добавьте обработку других файлов тайлсетов, если необходимо
				 else if (fileName.equals("TX Tileset Ground.tsx")) {
					return Gdx.files.internal("TX Tileset Ground.tsx");
				}
				 else if (fileName.equals("GameMap.tmx"))
				{
					return Gdx.files.internal("GameMap.tmx");
				}



				return null; // Возвращаем null, если не удалось разрешить путь
			}
		};
		TiledMapTileLayer collisionLayer = (TiledMapTileLayer) map.getLayers().get("Tiled_1");

		// Загружаем карту, указывая загрузчику, путь к карте и FileHandleResolver
		if (map == null) {
			map = new TmxMapLoader(resolver).load("GameMap.tmx", parameters);
		}

		if (map != null) {
			// Ваш код, использующий map
			map.getLayers(); // Пример вызова метода getLayers()
			// Другой ваш код...
		}


		// Продолжайте работу с вашей загруженной картой здесь
		playerPosition = new Vector2(700f, 96f);
		playerVelocity = new Vector2(0f, 0f);
		groundRect = new Rectangle(0f, 0f, Gdx.graphics.getWidth(), 20f);
		// Инициализация camera перед использованием
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(false);

		// Другой ваш код...
		// Инициализация поля класса tiledMapRenderer

		this.tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
		this.tiledMapRenderer.setView(camera); // Установка камеры

		// Создание кнопки ImageButton вперед
		Texture forwardTexture = new Texture(Gdx.files.internal("forward.png"));
		Drawable forwardDrawable = new TextureRegionDrawable(new TextureRegion(forwardTexture));
		ImageButton forward = new ImageButton(forwardDrawable);
		forward.setSize(100,100);
		forward.setPosition(200,30);

		// Создание кнопки ImageButton назад
		Texture backwardTexture = new Texture(Gdx.files.internal("backward.png"));
		Drawable backwardDrawable = new TextureRegionDrawable(new TextureRegion(backwardTexture));
		ImageButton backward = new ImageButton(backwardDrawable);
		backward.setSize(100,100);
		backward.setPosition(10,30);

		// Создание кнопки ImageButton назад
		Texture jumpButtonTexture = new Texture(Gdx.files.internal("jump.png"));
		Drawable jumpButtonDrawable = new TextureRegionDrawable(new TextureRegion(jumpButtonTexture));
		final ImageButton jumpButton = new ImageButton(jumpButtonDrawable);
		jumpButton.setSize(100,100);
		jumpButton.setPosition(100,30);

		// Инициализация сцены и добавление кнопки на нее
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		stage.addActor(forward);
		stage.addActor(backward);
		stage.addActor(jumpButton);

		forward.addListener(new ClickListener()
		{
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				moveRight = false;
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				moveRight = true;
				return true;
			}

		});
		backward.addListener(new ClickListener()
		{
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				moveLeft = false;
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				moveLeft = true;
				return true;
			}
		});
		jumpButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event,float x,float y)
			{
				jump();
			}
		});

	}


		// Другие абстрактные методы, если необходимо

	public Vector2 getPlayerPosition() {
		return playerPosition;
	}

	public void setPlayerPosition(Vector2 position) {
		this.playerPosition = position;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		for (MapLayer layer : map.getLayers()) {
			System.out.println(layer.getName());
		}

		handleInput();
		update();

		batch.begin();
		batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();

		camera.position.set(playerPosition.x, playerPosition.y, 0);
		camera.update();
		// Установка области отображения камеры в соответствии с игровым миром
		camera.update();
		tiledMapRenderer.setView(camera); // Обновляем вид камеры здесь

		// Установка матрицы проекции для отображения видимой части карты
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(playerTexture, playerPosition.x, playerPosition.y,100,100);
		batch.end();

		// Установка камеры для tiledMapRenderer перед его рендерингом
		tiledMapRenderer.setView(camera); // Обновляем вид камеры здесь

		tiledMapRenderer.render();

		stage.act();
		stage.draw();
	}

	private void update() {
		// Add your game logic here
		playerVelocity.y += gravity; // Применяем гравитацию
		playerPosition.add(playerVelocity); // Обновляем позицию игрока

		// Ограничение, чтобы игрок не уходил под землю
		if (playerPosition.y < 0) {
			playerPosition.y = 0f;
			playerVelocity.y = 0f;
		}
		// Проверяем коллизию в текущей позиции игрока по оси X
		if (playerVelocity.x < 0) {
			// Движение влево
			if (collisionLayer.getCell((int) playerPosition.x, (int) playerPosition.y) != null) {
				// Если есть коллизия, откатываем игрока назад
				playerPosition.x += 1;
			}
		} else if (playerVelocity.x > 0) {
			// Движение вправо
			if (collisionLayer.getCell((int) (playerPosition.x + playerTexture.getWidth()), (int) playerPosition.y) != null) {
				// Если есть коллизия, откатываем игрока назад
				playerPosition.x -= 1;
			}
		}
		// Проверяем коллизию в текущей позиции игрока по оси Y
		if (playerVelocity.y < 0) {
			// Падение вниз
			if (collisionLayer.getCell((int) playerPosition.x, (int) playerPosition.y) != null) {
				// Если есть коллизия, останавливаем падение
				playerVelocity.y = 0;
			}
		} else if (playerVelocity.y > 0) {
			// Подъем вверх
			if (collisionLayer.getCell((int) playerPosition.x, (int) (playerPosition.y + playerTexture.getHeight())) != null) {
				// Если есть коллизия, останавливаем подъем
				playerVelocity.y = 0;
			}
		}

		// Ограничение, чтобы игрок не уходил за границы экрана
		if (playerPosition.x < 0) {
			playerPosition.x = 0f;
		}
		/*if (playerPosition.x > Gdx.graphics.getWidth() - playerTexture.getWidth()) {
			playerPosition.x = (Gdx.graphics.getWidth() - playerTexture.getWidth());
		}*/
	}

	private void handleInput()
	{
		if (moveLeft) {
			playerPosition.x -= moveSpeed;
		} else if (moveRight) {
			playerPosition.x += moveSpeed;
		}

		if (Gdx.input.justTouched()) {
			jump();
		}
	}
	private void jump() {
		if (playerPosition.y <= 0) {
			playerVelocity.y = jumpVelocity; // Применяем скорость прыжка, если игрок на земле
		}
	}
	@Override
	public void dispose () {
		batch.dispose();
		playerTexture.dispose();
		groundTexture.dispose();
		map.dispose();
		backgroundTexture.dispose();
	}
}
