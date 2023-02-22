package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.component.constraint.LengthConstraint;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import lombok.AllArgsConstructor;

import java.lang.annotation.Annotation;

@AllArgsConstructor
public class StringInput extends FormattedTextInput<String> {

    private static final long serialVersionUID = 1L;

    private LengthConstraint lengthConstraint = LengthConstraint.none();

    public StringInput(int columns) {
        super(columns);
    }

    public StringInput() {
    }

    @Override
    protected String parse(String text) {
        String result = text.trim();
        if (lengthConstraint.test(result)) {
            return result;
        } else {
            return null;
        }
    }

    @Override
    protected String format(String value) {
        return value.trim();
    }

    @Override
    public void configure(Annotation annotation) {
        if (annotation instanceof Length) {
            lengthConstraint = LengthConstraint.fromAnnotation((Length) annotation);
        }
    }
}
