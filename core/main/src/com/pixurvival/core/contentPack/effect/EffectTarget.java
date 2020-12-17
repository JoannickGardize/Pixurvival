package com.pixurvival.core.contentPack.effect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.alteration.Alteration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EffectTarget implements Serializable {

	private static final long serialVersionUID = 1L;

	private TargetType targetType;

	private boolean destroyWhenCollide;

	@Valid
	private List<Alteration> alterations = new ArrayList<>();
}
