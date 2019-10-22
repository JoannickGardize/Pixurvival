package com.pixurvival.core.contentPack.serialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.creature.behaviorImpl.DistanceCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.DoNothingBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.GetAwayBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.GetAwayFromLightBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.InLightCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.IsDayCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.MoveTowardBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.TimeCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.TurnAroundBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.VanishBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.WanderBehavior;
import com.pixurvival.core.contentPack.effect.BackToOriginEffectMovement;
import com.pixurvival.core.contentPack.effect.BoundEffectMovement;
import com.pixurvival.core.contentPack.effect.DelayedFollowingElement;
import com.pixurvival.core.contentPack.effect.FollowingCreature;
import com.pixurvival.core.contentPack.effect.FollowingEffect;
import com.pixurvival.core.contentPack.effect.LinearEffectMovement;
import com.pixurvival.core.contentPack.effect.StaticEffectMovement;
import com.pixurvival.core.contentPack.gameMode.DayNightCycle;
import com.pixurvival.core.contentPack.gameMode.EternalDayCycle;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.NoEndCondition;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.RemainingTeamCondition;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.ClothingItem;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.contentPack.item.ResourceItem;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.contentPack.structure.HarvestableStructure;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.livingEntity.ability.AlterationAbility;
import com.pixurvival.core.livingEntity.alteration.AddItemAlteration;
import com.pixurvival.core.livingEntity.alteration.ContinuousDamageAlteration;
import com.pixurvival.core.livingEntity.alteration.FollowingElementAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantDamageAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantEatAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantHealAlteration;
import com.pixurvival.core.livingEntity.alteration.InvincibleAlteration;
import com.pixurvival.core.livingEntity.alteration.SilenceAlteration;
import com.pixurvival.core.livingEntity.alteration.StunAlteration;
import com.pixurvival.core.livingEntity.alteration.TeleportationAlteration;
import com.pixurvival.core.util.FileUtils;

/**
 * ContentPack serializer, create a zip containing all resources and a yaml file
 * for all elements definitions.
 * 
 * @author SharkHendrix
 *
 */
public class ContentPackSerializer {

	public static final String SERIALIZATION_ENTRY_NAME = "contentPack.yml";

	private static final String RESOURCES_ROOT = "resources/";

	private static final String TRANSLATIONS_ROOT = "translations/";

	private File workingDirectory;
	private Yaml yaml;
	private NameAnchorGenerator nameAnchorGenerator = new NameAnchorGenerator();
	private Map<String, Class<?>> classFromSimpleName = new HashMap<>();
	private List<ContentPackSerializerPlugin> plugins = new ArrayList<>();

	public ContentPackSerializer(File workingDirectory) {
		this.workingDirectory = workingDirectory;
		Representer representer = new Representer();
		representer.setPropertyUtils(new DeclarationPropertyOrderUtils());
		representer.getPropertyUtils().setSkipMissingProperties(true);
		addAllClassTags(representer);
		Constructor constructor = new Constructor() {
			@Override
			protected Class<?> getClassForName(String name) throws ClassNotFoundException {
				Class<?> clazz = classFromSimpleName.get(name);
				if (clazz == null) {
					return super.getClassForName(name);
				} else {
					return clazz;
				}
			}
		};
		DumperOptions dumperOptions = new DumperOptions();
		dumperOptions.setWidth(120);
		dumperOptions.setAnchorGenerator(nameAnchorGenerator);
		yaml = new Yaml(constructor, representer, dumperOptions);
		yaml.setBeanAccess(BeanAccess.FIELD);
	}

	public ContentPackSerializer() {
		this(null);
	}

	public void addPlugin(ContentPackSerializerPlugin plugin) {
		plugins.add(plugin);
	}

	public ContentPack load(ContentPackIdentifier identifier) throws ContentPackException {
		String fileName = identifier.fileName();
		File file = new File(workingDirectory, fileName);

		return load(file);
	}

	public ContentPack load(File file) throws ContentPackException {
		if (!file.exists()) {
			throw new ContentPackException(new FileNotFoundException(file.getAbsolutePath()));
		}
		try (ZipFile zipFile = new ZipFile(file)) {
			ZipEntry entry = zipFile.getEntry(SERIALIZATION_ENTRY_NAME);
			ContentPack contentPack = null;
			contentPack = yaml.loadAs(zipFile.getInputStream(entry), ContentPack.class);
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			while (enumeration.hasMoreElements()) {
				entry = enumeration.nextElement();
				if (entry.isDirectory()) {
					continue;
				}
				if (entry.getName().startsWith(RESOURCES_ROOT)) {
					contentPack.addResource(entry.getName().substring(10), FileUtils.readBytes(zipFile.getInputStream(entry)));
				} else if (entry.getName().startsWith(TRANSLATIONS_ROOT)) {
					Properties properties = new Properties();
					properties.load(zipFile.getInputStream(entry));
					Locale locale = Locale.forLanguageTag(entry.getName().substring(25).split("\\.")[0]);
					contentPack.addTranslation(locale, properties);
				}
			}
			for (ContentPackSerializerPlugin plugin : plugins) {
				plugin.read(contentPack, zipFile);
			}
			return contentPack;
		} catch (IOException e) {
			throw new ContentPackException(e);
		}
	}

	public void save(File file, ContentPack contentPack) throws IOException {
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file))) {
			zipOutputStream.putNextEntry(new ZipEntry(SERIALIZATION_ENTRY_NAME));
			nameAnchorGenerator.reset();
			yaml.dump(contentPack, new OutputStreamWriter(zipOutputStream));
			for (Entry<String, byte[]> resource : contentPack.getResources().entrySet()) {
				zipOutputStream.putNextEntry(new ZipEntry(RESOURCES_ROOT + resource.getKey()));
				zipOutputStream.write(resource.getValue());
			}
			for (Entry<Locale, Properties> translation : contentPack.getTranslations().entrySet()) {
				zipOutputStream.putNextEntry(new ZipEntry(TRANSLATIONS_ROOT + "translation_" + translation.getKey().toLanguageTag() + ".properties"));
				translation.getValue().store(zipOutputStream, null);
			}
			for (ContentPackSerializerPlugin plugin : plugins) {
				plugin.write(contentPack, zipOutputStream);
			}
			zipOutputStream.closeEntry();
		}
	}

	private void addAllClassTags(Representer representer) {
		addClassTag(representer, ContentPack.class);
		addClassTag(representer, BoundEffectMovement.class);
		addClassTag(representer, LinearEffectMovement.class);
		addClassTag(representer, StaticEffectMovement.class);
		addClassTag(representer, InstantDamageAlteration.class);
		addClassTag(representer, InstantEatAlteration.class);
		addClassTag(representer, InstantHealAlteration.class);
		addClassTag(representer, AccessoryItem.class);
		addClassTag(representer, ClothingItem.class);
		addClassTag(representer, EdibleItem.class);
		addClassTag(representer, ResourceItem.class);
		addClassTag(representer, StructureItem.class);
		addClassTag(representer, WeaponItem.class);
		addClassTag(representer, HarvestableStructure.class);
		addClassTag(representer, Structure.class);
		addClassTag(representer, WanderBehavior.class);
		addClassTag(representer, TurnAroundBehavior.class);
		addClassTag(representer, MoveTowardBehavior.class);
		addClassTag(representer, GetAwayBehavior.class);
		addClassTag(representer, DistanceCondition.class);
		addClassTag(representer, TimeCondition.class);
		addClassTag(representer, MoveTowardBehavior.class);
		addClassTag(representer, MoveTowardBehavior.class);
		addClassTag(representer, MoveTowardBehavior.class);
		addClassTag(representer, AlterationAbility.class);
		addClassTag(representer, NoEndCondition.class);
		addClassTag(representer, RemainingTeamCondition.class);
		addClassTag(representer, EternalDayCycle.class);
		addClassTag(representer, DayNightCycle.class);
		addClassTag(representer, GetAwayFromLightBehavior.class);
		addClassTag(representer, InLightCondition.class);
		addClassTag(representer, IsDayCondition.class);
		addClassTag(representer, FollowingEffect.class);
		addClassTag(representer, FollowingCreature.class);
		addClassTag(representer, AddItemAlteration.class);
		addClassTag(representer, BackToOriginEffectMovement.class);
		addClassTag(representer, VanishBehavior.class);
		addClassTag(representer, FollowingElementAlteration.class);
		addClassTag(representer, ContinuousDamageAlteration.class);
		addClassTag(representer, DoNothingBehavior.class);
		addClassTag(representer, DelayedFollowingElement.class);
		addClassTag(representer, TeleportationAlteration.class);
		addClassTag(representer, SilenceAlteration.class);
		addClassTag(representer, StunAlteration.class);
		addClassTag(representer, InvincibleAlteration.class);
	}

	private void addClassTag(Representer representer, Class<?> type) {
		representer.addClassTag(type, new Tag("!!" + type.getSimpleName()));
		classFromSimpleName.put(type.getSimpleName(), type);
	}
}
