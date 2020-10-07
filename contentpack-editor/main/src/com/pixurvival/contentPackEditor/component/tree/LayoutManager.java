package com.pixurvival.contentPackEditor.component.tree;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.serialization.ContentPackSerializationPlugin;
import com.pixurvival.core.contentPack.serialization.DeclarationPropertyOrderUtils;

import lombok.Getter;
import lombok.Setter;

public class LayoutManager implements ContentPackSerializationPlugin {

	public static final String LAYOUT_ENTRY = "layout.yml";

	private static final @Getter LayoutManager instance = new LayoutManager();

	private Yaml yaml;

	@Getter
	@Setter
	private LayoutFolder root;

	public LayoutManager() {
		Representer representer = new Representer();
		representer.addClassTag(LayoutFolder.class, new Tag("!!folder"));
		representer.addClassTag(LayoutElement.class, new Tag("!!element"));
		representer.setPropertyUtils(new DeclarationPropertyOrderUtils());
		Constructor constructor = new Constructor() {
			@Override
			protected Class<?> getClassForName(String name) throws ClassNotFoundException {
				if (name.equals("folder")) {
					return LayoutFolder.class;
				} else if (name.equals("element")) {
					return LayoutElement.class;
				} else {
					return super.getClassForName(name);
				}
			}
		};
		DumperOptions dumperOptions = new DumperOptions();
		dumperOptions.setWidth(120);
		yaml = new Yaml(constructor, representer, dumperOptions);
		yaml.setBeanAccess(BeanAccess.FIELD);
	}

	@Override
	public void read(ContentPack contentPack, ZipFile zipFile) {
		ZipEntry entry = zipFile.getEntry(LAYOUT_ENTRY);
		if (entry != null) {
			try {
				read(zipFile.getInputStream(entry));
			} catch (IOException e) {
				e.printStackTrace();
				root = new LayoutFolder("root");
			}
		} else {
			root = new LayoutFolder("root");
		}
		refresh(contentPack);
	}

	public void read(InputStream input) {
		root = yaml.loadAs(input, LayoutFolder.class);
	}

	public void refresh(ContentPack contentPack) {
		setElementReferences(contentPack);
		insertMissingElements(contentPack);
	}

	@Override
	public void write(ContentPack contentPack, ZipOutputStream zipOutputStream) {
		updateIndexes();
		try {
			zipOutputStream.putNextEntry(new ZipEntry(LAYOUT_ENTRY));
			yaml.dump(root, new OutputStreamWriter(zipOutputStream));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update the indexes of the tree elements, wich are corrupted if a deletion was
	 * made.
	 */
	private void updateIndexes() {
		root.forEachLeaf(node -> {
			LayoutElement leaf = (LayoutElement) node;
			leaf.setId(leaf.getElement().getId());
		});
	}

	private void setElementReferences(ContentPack contentPack) {
		root.forEachLeaf(node -> {
			LayoutElement layoutElement = (LayoutElement) node;
			List<IdentifiedElement> list = ContentPackEditionService.getInstance().listOf(contentPack, layoutElement.getType());
			layoutElement.setElement(list.get(layoutElement.getId()));
		});
	}

	private void insertMissingElements(ContentPack contentPack) {
		Set<Integer>[] missingKeys = createMissingKeysSets(contentPack);
		root.forEachLeaf(node -> {
			LayoutElement layoutElement = (LayoutElement) node;
			missingKeys[layoutElement.getType().ordinal()].remove(layoutElement.getId());
		});
		for (ElementType type : ElementType.values()) {
			Set<Integer> set = missingKeys[type.ordinal()];
			List<IdentifiedElement> list = ContentPackEditionService.getInstance().listOf(contentPack, type);
			for (Integer id : set) {
				root.getChildren().add(new LayoutElement(list.get(id)));
			}
		}
	}

	private Set<Integer>[] createMissingKeysSets(ContentPack contentPack) {
		@SuppressWarnings("unchecked")
		Set<Integer>[] missingKeys = new Set[ElementType.values().length];
		for (ElementType type : ElementType.values()) {
			Set<Integer> keys = new HashSet<>();
			missingKeys[type.ordinal()] = keys;
			List<IdentifiedElement> list = ContentPackEditionService.getInstance().listOf(contentPack, type);
			for (IdentifiedElement e : list) {
				keys.add(e.getId());
			}
		}
		return missingKeys;
	}

}
