package com.pixurvival.core.util;

import lombok.Getter;
import lombok.SneakyThrows;

import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

@Getter
public class CommonMainArgs {

	private String logLevel = "INFO";

	private boolean simulateLag = false;

	private int minSimulateLag = 40;

	private int maxSimulateLag = 50;

	private String contentPackDirectory = null;

	@SneakyThrows
	public void apply(EndPoint endPoint, Listener listener) {
		Log.class.getMethod(logLevel).invoke(null);
		if (simulateLag) {
			endPoint.addListener(new Listener.LagListener(minSimulateLag, maxSimulateLag, listener));
			Log.warn("Lag simulation mode enabled : [" + getMinSimulateLag() + " ms, " + getMaxSimulateLag() + " ms]");
		} else {
			endPoint.addListener(listener);
		}
	}
}
