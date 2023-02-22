package com.pixurvival.contentPackEditor.component.elementChooser;

import com.pixurvival.contentPackEditor.*;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueChangeListener;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.*;
import com.pixurvival.core.contentPack.validation.handler.UnitSpriteSheetHandler;
import com.pixurvival.core.util.CollectionUtils;
import com.pixurvival.core.util.ReflectionUtils;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ElementChooserButton<T extends NamedIdentifiedElement> extends JButton implements ValueComponent<T> {

    private static final long serialVersionUID = 1L;

    private @Getter SearchPopup<T> searchPopup;
    private List<ValueChangeListener<T>> listeners = new ArrayList<>();
    private @Getter JLabel associatedLabel;
    private @Getter T value;
    private @Getter
    @Setter boolean nullable;

    private Predicate<T> additionalCondition = e -> true;

    public ElementChooserButton(Class<T> elementType) {
        this(createContentPackItemsSupplier(elementType));
    }

    public static <T extends NamedIdentifiedElement> Supplier<Collection<T>> createContentPackItemsSupplier(Class<T> elementType) {
        return () -> getContentPackItems(elementType);
    }

    @SuppressWarnings("unchecked")
    public static <T extends NamedIdentifiedElement> Collection<T> getContentPackItems(Class<T> elementType) {
        ContentPack pack = FileService.getInstance().getCurrentContentPack();
        if (pack == null) {
            return Collections.emptyList();
        } else if (elementType.getSuperclass() == NamedIdentifiedElement.class) {
            return (Collection<T>) ContentPackEditionService.getInstance().listOf(ElementType.of(elementType));
        } else {
            Class<? extends NamedIdentifiedElement> superClass = ReflectionUtils.getSuperClassUnder(elementType, NamedIdentifiedElement.class);
            return ((Collection<T>) ContentPackEditionService.getInstance().listOf(ElementType.of(superClass))).stream().filter(elementType::isInstance).collect(Collectors.toList());
        }
    }

    public ElementChooserButton(Supplier<Collection<T>> itemsSupplier) {
        super(TranslationService.getInstance().getString("elementChooserButton.none"));

        searchPopup = new SearchPopup<>(itemsSupplier);
        addActionListener(e -> {
            searchPopup.setSearchText("");
            searchPopup.show(this);
        });
        searchPopup.addItemSelectionListener(item -> {
            setValue(item);
            listeners.forEach(l -> l.valueChanged(item));
        });
        setValue(null);
    }

    @Override
    public void addValueChangeListener(ValueChangeListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public void setValue(T item) {
        value = item;
        updateDisplay();
    }

    @Override
    public void setForeground(Color fg) {
        if (associatedLabel != null) {
            associatedLabel.setForeground(fg);
        }
        super.setForeground(fg);
    }

    @Override
    public void setAssociatedLabel(JLabel label) {
        associatedLabel = label;
        associatedLabel.setForeground(getForeground());
    }

    @Override
    public boolean isValueValid(T value) {
        if (nullable && value == null) {
            return true;
        }
        Collection<T> items = searchPopup.getItems();
        return items != null && CollectionUtils.containsIdentity(items, value) && additionalCondition.test(value);
    }

    @Override
    public void paint(Graphics g) {
        updateDisplay();
        super.paint(g);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void configure(Annotation annotation) {
        if (annotation instanceof Nullable) {
            nullable = true;
        } else if (annotation instanceof ResourceReference) {
            ResourceReference resourceReference = (ResourceReference) annotation;
            Predicate<ResourceEntry> typePredicate;
            switch (resourceReference.type()) {
                case IMAGE:
                    typePredicate = r -> r.getPreview() instanceof BufferedImage;
                    break;
                case SOUND:
                    typePredicate = r -> r.getPreview() == null;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            ((SearchPopup<ResourceEntry>) searchPopup).setAdditionalFilter(typePredicate);
            additionalCondition = additionalCondition.and(r -> {
                if (r == null) {
                    return true;
                } else {
                    return typePredicate.test((ResourceEntry) r);
                }
            });
        } else if (annotation instanceof AnimationTemplateRequirement) {
            AnimationTemplateRequirement requirement = (AnimationTemplateRequirement) annotation;
            additionalCondition = additionalCondition.and(s -> {
                if (s == null) {
                    return true;
                }
                SpriteSheet spriteSheet = (SpriteSheet) s;
                return spriteSheet.getAnimationTemplate() == null || requirement.value().test(spriteSheet.getAnimationTemplate().getAnimations().keySet());
            });
        } else if (annotation instanceof RequiredEquipmentOffset) {
            additionalCondition = additionalCondition.and(s -> {
                if (s == null) {
                    return true;
                }
                return ((SpriteSheet) s).getEquipmentOffset() != null;
            });
        } else if (annotation instanceof UnitSpriteSheet) {
            additionalCondition = additionalCondition.and(e -> {
                ResourceEntry entry = (ResourceEntry) e;
                if (entry == null || !(entry.getPreview() instanceof BufferedImage)) {
                    return true;
                }

                return UnitSpriteSheetHandler.test((BufferedImage) entry.getPreview());
            });
        }
    }

    public void addAdditionalCondition(Predicate<T> condition) {
        additionalCondition = additionalCondition.and(condition);
    }

    private void updateDisplay() {

        if (isValueValid()) {
            setIcon(IconService.getInstance().get(value));
            setForeground(Color.BLACK);
        } else {
            setForeground(Color.RED);
            setIcon(null);
        }
        setText(value == null ? TranslationService.getInstance().getString("elementChooserButton.none") : value.getName());
    }
}
