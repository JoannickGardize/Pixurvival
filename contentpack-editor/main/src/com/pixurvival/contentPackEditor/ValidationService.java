package com.pixurvival.contentPackEditor;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.validation.ContentPackValidator;
import com.pixurvival.core.contentPack.validation.ErrorNode;
import lombok.Getter;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class ValidationService {

    private static final @Getter ValidationService instance = new ValidationService();

    private ContentPackValidator validator = new ContentPackValidator();

    private ValidationService() {
        validator.setImageAccessor(name -> {
            ResourceEntry entry = ResourcesService.getInstance().getResource(name);
            if (entry == null) {
                return null;
            } else {
                Object preview = entry.getPreview();
                if (preview instanceof BufferedImage) {
                    return (BufferedImage) preview;
                } else {
                    return null;
                }
            }
        });
    }

    public List<ErrorNode> getErrorList() {
        ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
        if (contentPack == null) {
            return Collections.emptyList();
        }
        return validator.validate(contentPack).asList();
    }
}
