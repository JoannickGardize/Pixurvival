package com.pixurvival.core.contentPack.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.pixurvival.core.contentPack.creature.behaviorImpl.DoNothingBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.DropItemsBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.GetAwayBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.GetAwayFromLightBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.HarvestBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.MoveTowardBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.PickUpItemsBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.TurnAroundBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.VanishBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.WanderBehavior;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.DistanceCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.DistanceToStructureCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.InLightCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.InventoryContainsCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.IsDayCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.NothingToDoCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.TaskFinishedCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.TimeCondition;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.TookDamageCondition;
import com.pixurvival.core.contentPack.effect.BackToOriginEffectMovement;
import com.pixurvival.core.contentPack.effect.BoundEffectMovement;
import com.pixurvival.core.contentPack.effect.DelayedFollowingElement;
import com.pixurvival.core.contentPack.effect.FollowingCreature;
import com.pixurvival.core.contentPack.effect.FollowingEffect;
import com.pixurvival.core.contentPack.effect.LinearEffectMovement;
import com.pixurvival.core.contentPack.effect.StaticEffectMovement;
import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ExclusiveElementSet;
import com.pixurvival.core.contentPack.elementSet.InclusiveElementSet;
import com.pixurvival.core.contentPack.gameMode.DayNightCycle;
import com.pixurvival.core.contentPack.gameMode.EternalDayCycle;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.NoEndCondition;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.RemainingTeamCondition;
import com.pixurvival.core.contentPack.gameMode.event.EffectEvent;
import com.pixurvival.core.contentPack.gameMode.event.PlayerProximityEventPosition;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.ClothingItem;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.contentPack.item.ResourceItem;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.contentPack.structure.HarvestableStructure;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.livingEntity.ability.CreatureAlterationAbility;
import com.pixurvival.core.livingEntity.ability.ItemAlterationAbility;
import com.pixurvival.core.livingEntity.alteration.AddItemAlteration;
import com.pixurvival.core.livingEntity.alteration.ContinuousDamageAlteration;
import com.pixurvival.core.livingEntity.alteration.FixedMovementAlteration;
import com.pixurvival.core.livingEntity.alteration.FollowingElementAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantDamageAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantEatAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantHealAlteration;
import com.pixurvival.core.livingEntity.alteration.InvincibleAlteration;
import com.pixurvival.core.livingEntity.alteration.OverridingSpriteSheetAlteration;
import com.pixurvival.core.livingEntity.alteration.PlaySoundAlteration;
import com.pixurvival.core.livingEntity.alteration.RepeatAlteration;
import com.pixurvival.core.livingEntity.alteration.SilenceAlteration;
import com.pixurvival.core.livingEntity.alteration.StatAlteration;
import com.pixurvival.core.livingEntity.alteration.StunAlteration;
import com.pixurvival.core.livingEntity.alteration.TeleportationAlteration;
import com.pixurvival.core.util.FileUtils;

import lombok.Getter;
import lombok.SneakyThrows;

/**
 * IO for {@link ContentPack}s, create or read the zip containing all resources
 * and the yaml file for all elements definitions. It also has a context, with a
 * working directory, wich can be scanned to list available ContentPacks.
 * 
 * @author SharkHendrix
 *
 */
public class ContentPackSerialization {

	public static final String SERIALIZATION_ENTRY_NAME = "contentPack.yml";

	private static final String RESOURCES_ROOT = "resources/";

	private static final String TRANSLATIONS_ROOT = "translations/";

	private @Getter File workingDirectory;
	private Yaml yaml;
	private NameAnchorGenerator nameAnchorGenerator = new NameAnchorGenerator();
	private Map<String, Class<?>> classFromSimpleName = new HashMap<>();
	private List<ContentPackSerializationPlugin> plugins = new ArrayList<>();

	public ContentPackSerialization(File workingDirectory) {
		this.workingDirectory = workingDirectory;
		if (workingDirectory != null && !workingDirectory.isDirectory()) {
			throw new IllegalStateException("Not a directory : " + workingDirectory);
		}
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

	public ContentPackSerialization() {
		this(null);
	}

	public void addPlugin(ContentPackSerializationPlugin plugin) {
		plugins.add(plugin);
	}

	public List<ContentPackIdentifier> list() {
		if (workingDirectory == null) {
			throw new IllegalStateException("No working directory defined");
		}
		List<ContentPackIdentifier> list = new ArrayList<>();
		for (File file : workingDirectory.listFiles()) {
			ContentPackIdentifier identifier = ContentPackIdentifier.getIndentifierIfValid(file.getName());
			if (identifier != null) {
				list.add(identifier);
			}
		}
		return list;
	}

	public File fileOf(ContentPackIdentifier identifier) {
		return new File(workingDirectory, identifier.fileName());
	}

	public ContentPack load(ContentPackIdentifier identifier) throws ContentPackException {
		return load(fileOf(identifier));
	}

	public ContentPack load(ContentPackIdentifier identifier, boolean loadResources) throws ContentPackException {
		return load(fileOf(identifier), false);
	}

	public ContentPack load(File file) throws ContentPackException {
		return load(file, true);
	}

	public ContentPack load(File file, boolean loadResources) throws ContentPackException {
		if (!file.exists()) {
			throw new ContentPackException(new FileNotFoundException(file.getAbsolutePath()));
		}
		ContentPackIdentifier identifier = ContentPackIdentifier.getIndentifierIfValid(file.getName());
		if (identifier == null) {
			throw new ContentPackException(file.getName() + " is not a valid Content Pack file name.");
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
				if (entry.getName().startsWith(RESOURCES_ROOT) && loadResources) {
					contentPack.addResource(entry.getName().substring(10), FileUtils.readBytes(zipFile.getInputStream(entry)));
				} else if (entry.getName().startsWith(TRANSLATIONS_ROOT)) {
					Properties properties = new Properties();
					properties.load(zipFile.getInputStream(entry));
					Locale locale = Locale.forLanguageTag(entry.getName().substring(25).split("\\.")[0]);
					contentPack.addTranslation(locale, properties);
				}
			}
			for (ContentPackSerializationPlugin plugin : plugins) {
				plugin.read(contentPack, zipFile);
			}
			contentPack.setIdentifier(identifier);
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
			for (ContentPackSerializationPlugin plugin : plugins) {
				plugin.write(contentPack, zipOutputStream);
			}
			zipOutputStream.closeEntry();
		}
	}

	public ContentPackValidityCheckResult checkValidity(ContentPackIdentifier identifier, byte[] checksum) {
		if (!list().contains(identifier)) {
			return ContentPackValidityCheckResult.NOT_FOUND;
		} else if (!Arrays.equals(getChecksum(identifier), checksum)) {
			return ContentPackValidityCheckResult.NOT_SAME;
		} else {
			return ContentPackValidityCheckResult.OK;
		}
	}

	public byte[] getChecksum(ContentPackIdentifier identifier) {
		return getChecksum(fileOf(identifier));
	}

	@SneakyThrows
	private byte[] getChecksum(File file) {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		try (FileInputStream is = new FileInputStream(file)) {
			byte[] buffer = new byte[1024];
			int size;
			while ((size = is.read()) != -1) {
				digest.update(buffer, 0, size);
			}
			return digest.digest();
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
		addClassTag(representer, ItemAlterationAbility.class);
		addClassTag(representer, CreatureAlterationAbility.class);
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
		addClassTag(representer, TookDamageCondition.class);
		addClassTag(representer, FixedMovementAlteration.class);
		addClassTag(representer, OverridingSpriteSheetAlteration.class);
		addClassTag(representer, RepeatAlteration.class);
		addClassTag(representer, PlayerProximityEventPosition.class);
		addClassTag(representer, EffectEvent.class);
		addClassTag(representer, StatAlteration.class);
		addClassTag(representer, PlaySoundAlteration.class);
		addClassTag(representer, DistanceToStructureCondition.class);
		addClassTag(representer, HarvestBehavior.class);
		addClassTag(representer, PickUpItemsBehavior.class);
		addClassTag(representer, NothingToDoCondition.class);
		addClassTag(representer, TaskFinishedCondition.class);
		addClassTag(representer, DropItemsBehavior.class);
		addClassTag(representer, InventoryContainsCondition.class);
		addClassTag(representer, AllElementSet.class);
		addClassTag(representer, InclusiveElementSet.class);
		addClassTag(representer, ExclusiveElementSet.class);
	}

	private void addClassTag(Representer representer, Class<?> type) {
		representer.addClassTag(type, new Tag("!!" + type.getSimpleName()));
		classFromSimpleName.put(type.getSimpleName(), type);
	}
}
