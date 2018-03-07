package com.pixurvival.server;

import com.esotericsoftware.kryonet.util.TcpIdleSender;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.message.MapPart;
import com.pixurvival.core.util.ByteArray2D;

public class MapSender extends TcpIdleSender {

	private TiledMap tiledMap;
	private int partCountX;
	private int partCountY;
	private int x = -1;
	private int y = 0;

	public MapSender(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
		partCountX = (int) Math.ceil(tiledMap.getData().getWidth() / 64);
		partCountY = (int) Math.ceil(tiledMap.getData().getHeight() / 64);
	}

	@Override
	protected Object next() {
		if (++x >= partCountX) {
			x = 0;
			if (++y >= partCountY) {
				return null;
			}
		}
		int width = x < partCountX - 1 ? 64 : tiledMap.getData().getWidth() - x * 64;
		int height = y < partCountY - 1 ? 64 : tiledMap.getData().getHeight() - y * 64;
		ByteArray2D data = tiledMap.getData().getRect(x * 64, y * 64, width, height);
		return new MapPart(x * 64, y * 64, data);
	}
}
