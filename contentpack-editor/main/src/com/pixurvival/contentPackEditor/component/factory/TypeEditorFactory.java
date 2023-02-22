package com.pixurvival.contentPackEditor.component.factory;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.EnumSet;

public interface TypeEditorFactory {

    JComponent build(Field field, EnumSet<AttributeEditorFlag> flags);
}
