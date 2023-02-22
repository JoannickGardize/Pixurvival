package com.pixurvival.core.alteration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DamageAttributes {

    private static final @Getter DamageAttributes defaults = new DamageAttributes() {

        @Override
        public void setBypassInvincibility(boolean bypassInvincibility) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setTrueDamage(boolean trueDamage) {
            throw new UnsupportedOperationException();
        }
    };

    private boolean bypassInvincibility = false;

    private boolean trueDamage = false;
}
