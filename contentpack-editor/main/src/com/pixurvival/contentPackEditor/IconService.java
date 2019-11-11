package com.pixurvival.contentPackEditor;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.swing.Icon;

import com.pixurvival.contentPackEditor.component.util.GraphicsUtils;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.ElementRemovedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.util.ReflectionUtils;

import lombok.Getter;

public class IconService {

	private static @Getter IconService instance = new IconService();

	private Map<IdentifiedElement, Icon> elementIcons = new HashMap<>();
	private Map<Class<? extends IdentifiedElement>, Function<IdentifiedElement, Icon>> iconSuppliers = new HashMap<>();
	private Map<ElementType, Icon> typeIcons = new EnumMap<>(ElementType.class);

	private IconService() {
		EventManager.getInstance().register(this);
		iconSuppliers.put(Item.class, this::item);
		iconSuppliers.put(Tile.class, this::tile);
		iconSuppliers.put(Structure.class, this::structure);
		iconSuppliers.put(Creature.class, this::creature);
		iconSuppliers.put(Effect.class, this::effect);
		iconSuppliers.put(SpriteSheet.class, e -> this.get((SpriteSheet) e));
		iconSuppliers.put(ResourceEntry.class, e -> e instanceof ResourceEntry ? ((ResourceEntry) e).getIcon() : null);
		BufferedImage image = (BufferedImage) ImageService.getInstance().get("elements_icons");
		for (ElementType type : ElementType.values()) {
			typeIcons.put(type, get(image, new Frame(type.ordinal() % 5, type.ordinal() / 5), 16, 16));
		}
	}

	public Icon get(ElementType type) {
		return typeIcons.get(type);
	}

	public Icon get(IdentifiedElement element) {
		if (element == null) {
			return null;
		} else {
			return elementIcons.computeIfAbsent(element, e -> {
				Function<IdentifiedElement, Icon> iconSupplier = iconSuppliers.get(ReflectionUtils.getSuperClassUnder(e.getClass(), IdentifiedElement.class));
				if (iconSupplier == null) {
					return null;
				} else {
					return iconSupplier.apply(e);
				}
			});
		}
	}

	public Icon item(IdentifiedElement element) {
		Item item = (Item) element;
		if (item == null || item.getImage() == null || item.getFrame() == null) {
			return null;
		} else {
			return get(item.getImage(), item.getFrame());
		}
	}

	public Icon tile(IdentifiedElement element) {
		Tile tile = (Tile) element;
		if (tile == null || tile.getImage() == null || tile.getFrames() == null || tile.getFrames().isEmpty()) {
			return null;
		} else {
			return get(tile.getImage(), tile.getFrames().get(0));
		}
	}

	public Icon structure(IdentifiedElement element) {
		Structure structure = (Structure) element;
		if (structure.getSpriteSheet() == null) {
			return null;
		} else {
			return get(structure.getSpriteSheet());
		}
	}

	public Icon creature(IdentifiedElement element) {
		Creature creature = (Creature) element;
		if (creature.getSpriteSheet() == null) {
			return null;
		} else {
			return get(creature.getSpriteSheet());
		}
	}

	public Icon effect(IdentifiedElement element) {
		Effect effect = (Effect) element;
		if (effect.getSpriteSheet() == null) {
			return null;
		} else {
			return get(effect.getSpriteSheet());
		}
	}

	public Icon get(SpriteSheet spriteSheet) {
		return get(spriteSheet.getImage(), new Frame(0, 0), spriteSheet.getWidth(), spriteSheet.getHeight());
	}

	public Icon get(String imageName, Frame frame) {
		return get(imageName, frame, GameConstants.PIXEL_PER_UNIT, GameConstants.PIXEL_PER_UNIT);
	}

	private Icon get(String imageName, Frame frame, int width, int height) {
		if (frame == null) {
			return null;
		}
		if (imageName == null) {
			return null;
		}
		ResourceEntry resource = ResourcesService.getInstance().getResource(imageName);
		if (resource == null || !(resource.getPreview() instanceof BufferedImage)) {
			return null;
		}
		return get((BufferedImage) resource.getPreview(), frame, width, height);
	}

	private Icon get(BufferedImage image, Frame frame, int width, int height) {
		try {
			BufferedImage subimage = image.getSubimage(frame.getX() * width, frame.getY() * height, width, height);

			return GraphicsUtils.createIcon(subimage);
		} catch (RasterFormatException e) {
			return null;
		}
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		elementIcons.clear();
	}

	@EventListener
	public void elementChangedEvent(ElementChangedEvent event) {
		elementIcons.remove(event.getElement());
	}

	@EventListener
	public void elementRemovedEvent(ElementRemovedEvent event) {
		elementIcons.remove(event.getElement());
	}
}
