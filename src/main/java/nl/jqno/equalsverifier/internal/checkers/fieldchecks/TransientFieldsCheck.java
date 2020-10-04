package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class TransientFieldsCheck<T> implements FieldCheck<T> {
    private final PrefabValues prefabValues;
    private final TypeTag typeTag;
    private final AnnotationCache annotationCache;

    public TransientFieldsCheck(Configuration<T> config) {
        this.prefabValues = config.getPrefabValues();
        this.typeTag = config.getTypeTag();
        this.annotationCache = config.getAnnotationCache();
    }

    @Override
    public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
        Object reference = referenceAccessor.getObject();
        Object changed = changedAccessor.getObject();

        changedAccessor.changeField(prefabValues, typeTag);

        boolean equalsChanged = !reference.equals(changed);
        boolean hasAnnotation =
                annotationCache.hasFieldAnnotation(
                        typeTag.getType(),
                        referenceAccessor.getFieldName(),
                        SupportedAnnotations.TRANSIENT);
        boolean fieldIsTransient = referenceAccessor.fieldIsTransient() || hasAnnotation;
        if (equalsChanged && fieldIsTransient) {
            fail(
                    Formatter.of(
                            "Transient field %% should not be included in equals/hashCode contract.",
                            referenceAccessor.getFieldName()));
        }

        referenceAccessor.changeField(prefabValues, typeTag);
    }
}
