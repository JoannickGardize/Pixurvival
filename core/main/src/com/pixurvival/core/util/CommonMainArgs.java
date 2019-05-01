package com.pixurvival.core.util;

import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class CommonMainArgs {

	private String logLevel = "INFO";

	private boolean simulateLag = false;

	private int minSimulateLag = 40;

	private int maxSimulateLag = 50;

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
