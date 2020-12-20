package com.pixurvival.contentPackEditor.component.factory;

import java.lang.reflect.Field;
import java.util.EnumSet;

import javax.swing.JComponent;

public interface TypeEditorFactory {

	JComponent build(Field field, EnumSet<AttributeEditorFlag> flags);
}
