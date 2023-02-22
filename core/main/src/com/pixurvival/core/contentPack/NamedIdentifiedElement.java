package com.pixurvival.core.contentPack;

import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.alteration.StatFormula;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class NamedIdentifiedElement extends IdentifiedElement {

    private static final long serialVersionUID = 1L;

    @Length(min = 1)
    private String name;

    protected NamedIdentifiedElement(String name, int id) {
        super(id);
        this.name = name;
    }

    public void forEachStatFormula(Consumer<StatFormula> action) {
        // for override
    }

    public void forEachAlteration(Consumer<Alteration> action) {

    }

    public void initialize() {
        // for override
    }

    @Override
    public String toString() {
        return name;
    }

}
