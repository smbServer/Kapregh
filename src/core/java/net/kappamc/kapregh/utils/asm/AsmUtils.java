package net.kappamc.kapregh.utils.asm;

import org.objectweb.asm.tree.AnnotationNode;

public class AsmUtils {
    @SuppressWarnings("unchecked")
    public static <T> T getAnnotationValue(AnnotationNode annotationNode, String key) {
        boolean getNextValue = false;

        if (annotationNode.values == null) {
            return null;
        }

        for (Object value : annotationNode.values) {
            if (getNextValue) {
                return (T) value;
            }
            if (value.equals(key)) {
                getNextValue = true;
            }
        }

        return null;
    }
}
