package com.pixurvival.contentPackEditor.component.tree;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@RequiredArgsConstructor
public class LayoutFolder extends LayoutNode {

    @Getter
    @Setter
    private @NonNull String name;

    @Getter
    private List<LayoutNode> children = new ArrayList<>();

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public LayoutNode copy() {
        LayoutFolder folder = new LayoutFolder(name);
        folder.setValid(isValid());
        return folder;
    }

    @Override
    public void updateValidation() {
        for (LayoutNode child : children) {
            if (!child.isValid()) {
                setValid(false);
                return;
            }
        }
        setValid(true);
    }
}
