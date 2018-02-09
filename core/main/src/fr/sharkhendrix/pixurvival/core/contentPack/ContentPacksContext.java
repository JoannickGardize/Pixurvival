package fr.sharkhendrix.pixurvival.core.contentPack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.esotericsoftware.minlog.Log;

public class ContentPacksContext {

	private File workingDirectory;
	private Map<ContentPackIdentifier, ContentPackFileInfo> infos = new HashMap<>();

	public ContentPacksContext(File workingDirectory) {
		if (!workingDirectory.isDirectory()) {
			throw new IllegalArgumentException(workingDirectory.getPath() + " is not a directory.");
		}
		this.workingDirectory = workingDirectory;
		refreshList();
	}

	public ContentPacksContext(String workingDirectory) {
		this(new File(workingDirectory));
	}

	public void refreshList() {
		infos.clear();
		Unmarshaller unmarshaller;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ContentPackInfo.class);
			jaxbContext.createUnmarshaller();
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			Log.error("ContentPack", "Unable to Create JAXB Unmarshaller", e);
			return;
		}
		for (File file : workingDirectory.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}
			try (ZipFile zip = new ZipFile(file)) {
				ZipEntry entry = zip.getEntry(ContentPackInfo.XML_FILE_NAME);
				ContentPackInfo info = (ContentPackInfo) unmarshaller.unmarshal(zip.getInputStream(entry));
				if (infos.values().contains(info)) {
					Log.warn("ContentPack", "Duplicate content pack : " + info);
				} else {
					infos.put(info, new ContentPackFileInfo(info, file));
				}
			} catch (IOException | JAXBException e) {
				Log.warn("ContentPack", "Error occured when trying to read content pack info : " + file.getName(), e);
			}
		}
	}

	public ContentPack load(ContentPackIdentifier identifier) throws ContentPackException {
		List<ContentPackFileInfo> dependencyList = resolveDependencies(identifier);

		JAXBContext context;
		try {
			context = JAXBContext.newInstance(ContentPackInfo.class, AnimationTemplates.class, Sprites.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setEventHandler(e -> !(e.getLinkedException().getCause() instanceof ContentPackReadException));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Resolve the dependencies of the given {@link ContentPackIdentifier}. The
	 * returned list is used to load the content pack, from first to last
	 * element. The first element never has dependency, and the last element is
	 * always the given content pack.
	 * 
	 * @param identifier
	 *            The content pack to load.
	 * @return List of the dependencies, ordered by a satisfying content pack
	 *         load order.
	 * @throws ContentPackDenpendencyException
	 *             If a dependency is missing in the working directory of this
	 *             instance, or if a cycle is detected to the dependency graph.
	 */
	public List<ContentPackFileInfo> resolveDependencies(ContentPackIdentifier identifier)
			throws ContentPackDenpendencyException {
		ContentPackFileInfo info = infos.get(identifier);
		if (info == null) {
			throw new ContentPackDenpendencyException("Missing content pack : " + info);
		}
		List<ContentPackFileInfo> result = new ArrayList<>();
		recursiveResolveDependencies(info, result, new ArrayList<>());
		return result;
	}

	private void recursiveResolveDependencies(ContentPackFileInfo currentNode, List<ContentPackFileInfo> result,
			List<ContentPackFileInfo> traversalStack) throws ContentPackDenpendencyException {
		if (traversalStack.contains(currentNode)) {
			throw new ContentPackDenpendencyException("Dependency cycle detected : " + currentNode);
		}
		traversalStack.add(currentNode);
		for (Dependency dependency : currentNode.getDependencies().all()) {
			ContentPackFileInfo dependencyInfo = infos.get(dependency);
			if (dependencyInfo == null) {
				throw new ContentPackDenpendencyException("Missing dependency : " + dependencyInfo);
			}
			recursiveResolveDependencies(dependencyInfo, result, traversalStack);
		}
		if (!result.contains(currentNode)) {
			result.add(currentNode);
		}
		traversalStack.remove(traversalStack.size() - 1);
	}
}
