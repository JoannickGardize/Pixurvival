package fr.sharkhendrix.pixurvival.gdxcore;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;

import fr.sharkhendrix.pixurvival.client.ClientGame;
import fr.sharkhendrix.pixurvival.client.ClientGameListener;
import fr.sharkhendrix.pixurvival.core.map.Tile;
import fr.sharkhendrix.pixurvival.core.message.Direction;
import fr.sharkhendrix.pixurvival.core.message.LoginResponse;
import fr.sharkhendrix.pixurvival.core.message.PlayerActionRequest;

public class PixurvivalGame extends ApplicationAdapter implements ClientGameListener {

	private ClientGame game;
	private WorldStage worldStage;
	private PlayerActionRequest actionRequest = new PlayerActionRequest();
	private double frameDurationMillis = 1000.0 / 30;
	private int maxUpdatePerFrame = 5;
	private double frameCounter;

	@Override
	public void create() {
		worldStage = new WorldStage();
		game = new ClientGame();
		game.addListener(this);
		game.connectToServer("localhost", 7777, "Bob");
	}

	@Override
	public void render() {
		processInputs();
		frameCounter += Gdx.graphics.getDeltaTime() * 1000;
		while (frameCounter >= frameDurationMillis) {
			game.update(frameDurationMillis);
			frameCounter -= frameDurationMillis;
		}
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		worldStage.getCamera().position.set(250, 250, 0);
		worldStage.draw();
	}

	@Override
	public void dispose() {
		worldStage.dispose();

	}

	@Override
	public void resize(int width, int height) {
		worldStage.getViewport().update(width, height);
	}

	@Override
	public void loginResponse(LoginResponse response) {
		System.out.println(response);

	}

	@Override
	public void startGame() {
		// TODO remove BOUCHON
		game.getWorld().getMap().getTiles().setAll(Tile.GRASS);
		worldStage.setWorld(game.getWorld());
	}

	private void processInputs() {
		boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
		boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
		boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
		boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
		Direction direction = Direction.SOUTH;
		boolean forward = true;
		if (right && up) {
			direction = Direction.NORTH_EAST;
		} else if (left && up) {
			direction = Direction.NORTH_WEST;
		} else if (left && down) {
			direction = Direction.SOUTH_WEST;
		} else if (right && down) {
			direction = Direction.SOUTH_EAST;
		} else if (right) {
			direction = Direction.EAST;
		} else if (up) {
			direction = Direction.NORTH;
		} else if (left) {
			direction = Direction.WEST;
		} else if (down) {
			direction = Direction.SOUTH;
		} else {
			forward = false;
		}
		boolean changed = actionRequest.getDirection() != direction || actionRequest.isForward() != forward;
		actionRequest.setDirection(direction);
		actionRequest.setForward(forward);
		if (changed) {
			game.sendAction(actionRequest);
		}

	}
}
