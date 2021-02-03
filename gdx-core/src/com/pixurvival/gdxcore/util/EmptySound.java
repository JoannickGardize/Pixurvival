package com.pixurvival.gdxcore.util;

import com.badlogic.gdx.audio.Sound;

import lombok.Getter;

public class EmptySound implements Sound {

	private static final @Getter EmptySound instance = new EmptySound();

	@Override
	public long play() {
		return -1;
	}

	@Override
	public long play(float volume) {
		return -1;
	}

	@Override
	public long play(float volume, float pitch, float pan) {
		return -1;
	}

	@Override
	public long loop() {
		return -1;
	}

	@Override
	public long loop(float volume) {
		return -1;
	}

	@Override
	public long loop(float volume, float pitch, float pan) {
		return -1;
	}

	@Override
	public void stop() {
		// Empty
	}

	@Override
	public void pause() {
		// Empty
	}

	@Override
	public void resume() {
		// Empty
	}

	@Override
	public void dispose() {
		// Empty
	}

	@Override
	public void stop(long soundId) {
		// Empty
	}

	@Override
	public void pause(long soundId) {
		// Empty
	}

	@Override
	public void resume(long soundId) {
		// Empty
	}

	@Override
	public void setLooping(long soundId, boolean looping) {
		// Empty
	}

	@Override
	public void setPitch(long soundId, float pitch) {
		// Empty
	}

	@Override
	public void setVolume(long soundId, float volume) {
		// Empty
	}

	@Override
	public void setPan(long soundId, float pan, float volume) {
		// Empty
	}

}
