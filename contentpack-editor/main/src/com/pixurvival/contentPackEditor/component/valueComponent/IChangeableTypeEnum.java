package com.pixurvival.contentPackEditor.component.valueComponent;

public interface IChangeableTypeEnum<T> {

	ElementEditor<T> getEditor();

	Class<? extends T> getType();
}
