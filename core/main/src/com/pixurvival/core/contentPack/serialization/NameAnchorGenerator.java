package com.pixurvival.core.contentPack.serialization;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.serializer.AnchorGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom anchor generator for Yaml serialization. it search for a child node
 * with key equals to "name", and use it for the anchor name, plus an
 * incremental number if the name has already be seen.
 *
 * @author SharkHendrix
 */
public class NameAnchorGenerator implements AnchorGenerator {

    private Map<String, Integer> nameCounters = new HashMap<>();

    public void reset() {
        nameCounters.clear();
    }

    @Override
    public String nextAnchor(Node node) {
        String name = getName(node);
        return addIncrement(name);
    }

    private String getName(Node node) {
        if (node instanceof MappingNode) {
            for (NodeTuple tuple : ((MappingNode) node).getValue()) {
                if (tuple.getKeyNode() instanceof ScalarNode && "name".equals(((ScalarNode) tuple.getKeyNode()).getValue()) && tuple.getValueNode() instanceof ScalarNode) {
                    return ((ScalarNode) tuple.getValueNode()).getValue().replaceAll("[^a-zA-Z0-9_-]", "_");
                }
            }
        }
        return "id";
    }

    private String addIncrement(String name) {
        String result = name;
        do {
            Integer increment = nameCounters.get(result);
            if (increment == null) {
                nameCounters.put(result, 1);
                return result;
            } else {
                increment++;
                nameCounters.put(result, increment);
                if (increment < 10) {
                    result = result + "0" + increment;
                } else {
                    result = result + increment;
                }
            }
        } while (nameCounters.containsKey(result));
        return result;
    }
}
