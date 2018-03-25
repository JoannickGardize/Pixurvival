package com.pixurvival.core.contentPack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.item.ItemRewards;
import com.pixurvival.core.contentPack.item.Items;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.MapGenerators;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.Structures;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.map.Tiles;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.AnimationTemplates;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.sprite.Sprites;
import com.pixurvival.core.item.Item;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContentPack {

	private @NonNull ContentPackFileInfo info;
	private Sprites sprites;
	private Tiles tiles;
	private Items items;
	private ItemRewards itemRewards;
	private Structures structures;
	private MapGenerators mapGenerators;
	private List<Tile> tilesById = new ArrayList<>();
	private List<Item> itemsById = new ArrayList<>();
	private List<Structure> structuresById = new ArrayList<>();

	static ContentPack load(RefContext refContext, Unmarshaller unmarshaller, ContentPackFileInfo info)
			throws ContentPackReadException {
		ContentPack contentPack = new ContentPack(info);
		try (ZipFile zipFile = new ZipFile(info.getFile())) {

			AnimationTemplates animationTemplates = (AnimationTemplates) readXmlFile(unmarshaller, zipFile,
					"animationTemplates.xml");
			refContext.addElementSet(AnimationTemplate.class, animationTemplates);
			contentPack.sprites = (Sprites) readXmlFile(unmarshaller, zipFile, "sprites.xml");
			refContext.addElementSet(SpriteSheet.class, contentPack.sprites);
			contentPack.tiles = (Tiles) readXmlFile(unmarshaller, zipFile, "tiles.xml");
			refContext.addElementSet(Tile.class, contentPack.tiles);
			contentPack.items = (Items) readXmlFile(unmarshaller, zipFile, "items.xml");
			refContext.addElementSet(Item.class, contentPack.items);
			contentPack.itemRewards = (ItemRewards) readXmlFile(unmarshaller, zipFile, "itemRewards.xml");
			refContext.addElementSet(ItemReward.class, contentPack.itemRewards);
			contentPack.structures = (Structures) readXmlFile(unmarshaller, zipFile, "structures.xml");
			refContext.addElementSet(Structure.class, contentPack.structures);
			contentPack.mapGenerators = (MapGenerators) readXmlFile(unmarshaller, zipFile, "mapGenerators.xml");
			refContext.addElementSet(MapGenerator.class, contentPack.mapGenerators);
			refContext.removeCurrentSets();
			refContext.getAdapter(Tile.class).allSets().stream().flatMap(e -> e.all().values().stream())
					.forEach(new Consumer<Tile>() {

						private byte tileId = 0;

						@Override
						public void accept(Tile t) {
							t.setId(tileId++);
							contentPack.tilesById.add(t);
						}
					});
			refContext.getAdapter(Item.class).allSets().stream().flatMap(e -> e.all().values().stream())
					.forEach(new Consumer<Item>() {

						private short nextId = 0;

						@Override
						public void accept(Item i) {
							i.setId(nextId++);
							contentPack.itemsById.add(i);
						}
					});
			refContext.getAdapter(Structure.class).allSets().stream().flatMap(e -> e.all().values().stream())
					.forEach(new Consumer<Structure>() {

						private byte nextId = 0;

						@Override
						public void accept(Structure t) {
							t.setId(nextId++);
							contentPack.structuresById.add(t);
						}
					});
			return contentPack;
		} catch (Exception e) {
			throw new ContentPackReadException(e);
		}
	}

	private static Object readXmlFile(Unmarshaller unmarshaller, ZipFile zipFile, String name)
			throws ContentPackReadException, JAXBException, IOException {
		ZipEntry entry = zipFile.getEntry(name);
		if (entry == null) {
			throw new ContentPackReadException("Missing file " + name);
		}
		return unmarshaller.unmarshal(zipFile.getInputStream(entry));
	}
}
