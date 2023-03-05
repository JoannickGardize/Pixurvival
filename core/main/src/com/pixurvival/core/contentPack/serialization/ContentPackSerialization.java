package com.pixurvival.core.contentPack.serialization;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.alteration.*;
import com.pixurvival.core.alteration.condition.*;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.creature.behaviorImpl.*;
import com.pixurvival.core.contentPack.creature.changeConditionImpl.*;
import com.pixurvival.core.contentPack.effect.*;
import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ExclusiveElementSet;
import com.pixurvival.core.contentPack.elementSet.InclusiveElementSet;
import com.pixurvival.core.contentPack.gameMode.DayNightCycle;
import com.pixurvival.core.contentPack.gameMode.EternalDayCycle;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.RemainingRolesEndCondition;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.RemainingTeamEndCondition;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.TimeEndCondition;
import com.pixurvival.core.contentPack.gameMode.event.EffectEvent;
import com.pixurvival.core.contentPack.gameMode.event.PlayerProximityEventPosition;
import com.pixurvival.core.contentPack.gameMode.event.RandomRectangeEventPosition;
import com.pixurvival.core.contentPack.gameMode.role.RemainingRolesWinCondition;
import com.pixurvival.core.contentPack.gameMode.role.SurviveWinCondition;
import com.pixurvival.core.contentPack.gameMode.role.TeamSurvivedWinCondition;
import com.pixurvival.core.contentPack.gameMode.spawn.AutoSquarePlayerSpawn;
import com.pixurvival.core.contentPack.gameMode.spawn.StaticPlayerSpawn;
import com.pixurvival.core.contentPack.item.*;
import com.pixurvival.core.contentPack.item.trigger.EquippedTrigger;
import com.pixurvival.core.contentPack.item.trigger.TimerTrigger;
import com.pixurvival.core.contentPack.item.trigger.UnequippedTrigger;
import com.pixurvival.core.contentPack.map.ProcedurallyGeneratedMapProvider;
import com.pixurvival.core.contentPack.map.StaticMapProvider;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.serialization.io.IOSupplier;
import com.pixurvival.core.contentPack.serialization.io.StoreFactory;
import com.pixurvival.core.contentPack.serialization.io.StoreInput;
import com.pixurvival.core.contentPack.serialization.io.StoreOutput;
import com.pixurvival.core.contentPack.structure.FactoryStructure;
import com.pixurvival.core.contentPack.structure.HarvestableStructure;
import com.pixurvival.core.contentPack.structure.InventoryStructure;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.summary.ContentPackSummary;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.ability.CreatureAlterationAbility;
import com.pixurvival.core.livingEntity.ability.ItemAlterationAbility;
import com.pixurvival.core.util.FileUtils;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.Map.Entry;

/**
 * I/O for {@link ContentPack}s, create or read the zip containing all resources
 * and the yaml file for all elements definitions.
 *
 * @author SharkHendrix
 */
public class ContentPackSerialization {

    public static final String SERIALIZATION_ENTRY_NAME = "contentPack.yml";

    public static final String SUMMARY_ENTRY_NAME = "summary.yml";

    public static final String RESOURCES_ROOT = "resources/";

    public static final String TRANSLATIONS_ROOT = "translations/";
    public static final String TRANSLATION_FILE_PREFIX = "translation_";

    private Yaml yaml;
    private NameAnchorGenerator nameAnchorGenerator = new NameAnchorGenerator();
    private Map<String, Class<?>> classFromSimpleName = new HashMap<>();
    private List<ContentPackSerializationPlugin> plugins = new ArrayList<>();

    public ContentPackSerialization() {
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

    public static String buildTranslationFileName(Locale locale) {
        return TRANSLATIONS_ROOT + TRANSLATION_FILE_PREFIX + locale.toLanguageTag() + ".properties";
    }

    public void addPlugin(ContentPackSerializationPlugin plugin) {
        plugins.add(plugin);
    }


    public void save(IOSupplier<StoreOutput> outputSupplier, ContentPack contentPack, boolean saveResources) throws IOException {
        try (StoreOutput output = outputSupplier.get()) {
            OutputStream currentStream = output.nextEntry(SUMMARY_ENTRY_NAME);
            yaml.dump(new ContentPackSummary(contentPack), new OutputStreamWriter(new BufferedOutputStream(currentStream), StandardCharsets.UTF_8));
            currentStream = output.nextEntry(SERIALIZATION_ENTRY_NAME);
            nameAnchorGenerator.reset();
            yaml.dump(contentPack, new OutputStreamWriter(currentStream, StandardCharsets.UTF_8));
            if (saveResources) {
                for (Entry<String, byte[]> resource : contentPack.getResources().entrySet()) {
                    String path = RESOURCES_ROOT + resource.getKey();
                    currentStream = output.nextEntry(RESOURCES_ROOT + resource.getKey());
                    currentStream.write(resource.getValue());
                }
            }
            for (Entry<Locale, Properties> translation : contentPack.getTranslations().entrySet()) {
                currentStream = output.nextEntry(buildTranslationFileName(translation.getKey()));
                translation.getValue().store(currentStream, null);
            }
            for (ContentPackSerializationPlugin plugin : plugins) {
                plugin.write(contentPack, output);
            }
        }
    }

    public ContentPack load(IOSupplier<StoreInput> inputSupplier) throws ContentPackException {
        return load(inputSupplier, null);
    }

    public ContentPack load(IOSupplier<StoreInput> inputSupplier, InputStream externalContentPackSerialization) throws ContentPackException {
        try (StoreInput input = inputSupplier.get()) {
            InputStream contentPackSerialization = externalContentPackSerialization == null ? input.nextEntry(SERIALIZATION_ENTRY_NAME) : externalContentPackSerialization;
            ContentPack contentPack = yaml.loadAs(new InputStreamReader(new BufferedInputStream(contentPackSerialization), StandardCharsets.UTF_8), ContentPack.class);
            input.forEachEntry(RESOURCES_ROOT, name -> contentPack.addResource(name.substring(RESOURCES_ROOT.length()), FileUtils.readBytes(input.nextEntry(name))));
            input.forEachEntry(TRANSLATIONS_ROOT, name -> {
                Properties properties = new Properties();
                properties.load(input.nextEntry(name));
                Locale locale = Locale.forLanguageTag(name.substring(TRANSLATIONS_ROOT.length() + TRANSLATION_FILE_PREFIX.length()).split("\\.")[0]);
                contentPack.addTranslation(locale, properties);
            });
            for (ContentPackSerializationPlugin plugin : plugins) {
                plugin.read(contentPack, input);
            }
            if (contentPack.getIdentifier() == null) {
                contentPack.setIdentifier(ContentPackIdentifier.getIndentifierBasedOnFileName(input.getName()));
                if (contentPack.getIdentifier() == null) {
                    contentPack.setIdentifier(new ContentPackIdentifier());
                }
            }
            return contentPack;
        } catch (Exception e) {
            throw new ContentPackException(e);
        }
    }

    public ContentPackSummary readSummary(File file) {
        try (StoreInput input = StoreFactory.input(file)) {
            InputStream inputStream = input.nextEntry(SUMMARY_ENTRY_NAME);
            ContentPackSummary summary = yaml.loadAs(new InputStreamReader(inputStream, StandardCharsets.UTF_8), ContentPackSummary.class);
            summary.getIdentifier().setFile(file);
            summary.setDirectoryMode(file.isDirectory());
            return summary;
        } catch (Exception e) {
            Log.warn("An error occured when trying to read the summary of the content pack " + file.getAbsolutePath(), e);
            return null;
        }
    }

    @SneakyThrows
    public byte[] getChecksum(File file) {
        if (!file.isFile()) {
            // TODO checksum for directories?
            return new byte[]{0};
        }
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
        addClassTag(representer, MoveTowardStructureBehavior.class);
        addClassTag(representer, DistanceToStructureCondition.class);
        addClassTag(representer, ItemAlterationAbility.class);
        addClassTag(representer, CreatureAlterationAbility.class);
        addClassTag(representer, RemainingTeamEndCondition.class);
        addClassTag(representer, TimeEndCondition.class);
        addClassTag(representer, RemainingRolesEndCondition.class);
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
        addClassTag(representer, TileCondition.class);
        addClassTag(representer, HarvestBehavior.class);
        addClassTag(representer, PickUpItemsBehavior.class);
        addClassTag(representer, NothingToDoCondition.class);
        addClassTag(representer, TaskFinishedCondition.class);
        addClassTag(representer, InventoryContainsCondition.class);
        addClassTag(representer, AllElementSet.class);
        addClassTag(representer, InclusiveElementSet.class);
        addClassTag(representer, ExclusiveElementSet.class);
        addClassTag(representer, ProcedurallyGeneratedMapProvider.class);
        addClassTag(representer, StaticMapProvider.class);
        addClassTag(representer, SurviveWinCondition.class);
        addClassTag(representer, TeamSurvivedWinCondition.class);
        addClassTag(representer, RemainingRolesWinCondition.class);
        addClassTag(representer, RandomRectangeEventPosition.class);
        addClassTag(representer, ItemStack.class);
        addClassTag(representer, AutoSquarePlayerSpawn.class);
        addClassTag(representer, StaticPlayerSpawn.class);
        addClassTag(representer, DelayedAlteration.class);
        addClassTag(representer, DropItemsAlteration.class);
        addClassTag(representer, PlayCustomSoundAlteration.class);
        addClassTag(representer, InventoryStructure.class);
        addClassTag(representer, FactoryStructure.class);
        addClassTag(representer, SetSpawnPositionAlteration.class);
        addClassTag(representer, Tile.class);
        addClassTag(representer, TimerTrigger.class);
        addClassTag(representer, UnequippedTrigger.class);
        addClassTag(representer, ConditionAlteration.class);
        addClassTag(representer, HealthAlterationCondition.class);
        addClassTag(representer, EntityAroundCountAlterationCondition.class);
        addClassTag(representer, Creature.class);
        addClassTag(representer, EquippedTrigger.class);
        addClassTag(representer, ActualSpeedAlterationCondition.class);
        addClassTag(representer, InventoryContainsAlterationCondition.class);
        addClassTag(representer, RandomAlterationCondition.class);
        addClassTag(representer, StatAlterationCondition.class);
        addClassTag(representer, StructureAroundCountAlterationCondition.class);
        addClassTag(representer, TileAlterationCondition.class);
    }

    private void addClassTag(Representer representer, Class<?> type) {
        representer.addClassTag(type, new Tag("!!" + type.getSimpleName()));
        classFromSimpleName.put(type.getSimpleName(), type);
    }
}
