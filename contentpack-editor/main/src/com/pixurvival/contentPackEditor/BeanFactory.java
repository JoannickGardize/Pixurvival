package com.pixurvival.contentPackEditor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.pixurvival.contentPackEditor.util.BeanUtils;
import com.pixurvival.core.contentPack.effect.DelayedFollowingElement;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.effect.FollowingEffect;
import com.pixurvival.core.contentPack.effect.FollowingElement;
import com.pixurvival.core.contentPack.effect.LinearEffectMovement;
import com.pixurvival.core.contentPack.effect.OffsetAngleEffect;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ResourceItem;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.FollowingElementAlteration;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BeanFactory {

	private static Map<Class<?>, Supplier<?>> suppliers = new HashMap<>();

	static {
		suppliers.put(DelayedFollowingElement.class, () -> {
			DelayedFollowingElement delayedFollowingElement = new DelayedFollowingElement();
			delayedFollowingElement.setFollowingElement(BeanFactory.newInstance(FollowingElement.class));
			return delayedFollowingElement;
		});

		suppliers.put(FollowingElement.class, () -> {
			FollowingEffect result = new FollowingEffect();
			result.setOffsetAngleEffect(new OffsetAngleEffect());
			return result;
		});

		suppliers.put(Alteration.class, () -> {
			FollowingElementAlteration followingElement = new FollowingElementAlteration();
			followingElement.setFollowingElement(newInstance(FollowingElement.class));
			return followingElement;
		});

		suppliers.put(Item.class, () -> {
			Item item = new ResourceItem();
			item.setFrame(new Frame());
			item.setMaxStackSize(1);
			return item;
		});

		suppliers.put(Effect.class, () -> {
			Effect effect = new Effect();
			effect.setMovement(new LinearEffectMovement());
			return effect;
		});
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> type) {
		Supplier<?> supplier = suppliers.get(type);
		if (supplier == null) {
			return BeanUtils.newFilledInstance(type);
		} else {
			return (T) suppliers.get(type).get();
		}
	}

	public static <T> Supplier<T> of(Class<T> type) {
		return () -> BeanFactory.newInstance(type);
	}
}
