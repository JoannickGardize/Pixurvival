package com.pixurvival.contentPackEditor;

import com.pixurvival.core.util.DefaultValues;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainArgs {

    private String open;

    private String contentPackDirectory = DefaultValues.CONTENT_PACK_DIRECTORY;

}
