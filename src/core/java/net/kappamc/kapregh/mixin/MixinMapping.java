package net.kappamc.kapregh.mixin;

import net.kappamc.kapregh.utils.asm.AsmUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MixinMapping {
    public final List<String> mixins = new ArrayList<>();
    public final List<String> targets = new ArrayList<>();
    public final HashMap<String, String> invokes = new HashMap<>();
    public final HashMap<String, String> accessors = new HashMap<>();
    public final HashMap<String, List<String>> methods = new HashMap<>();
    public String className = null;

    public void accept(byte[] basic) {
        ClassReader classReader = new ClassReader(basic);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        if (classNode.invisibleAnnotations == null) {
            return;
        }

        for (AnnotationNode invisibleAnnotation : classNode.invisibleAnnotations) {
            if (invisibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                className = classNode.name;
                List<Type> values = AsmUtils.getAnnotationValue(invisibleAnnotation, "value");
                if (values != null) {
                    for (Type type : values) {
                        mixins.add(type.getClassName().replace(".", "/"));
                    }
                }

                List<String> targets = AsmUtils.getAnnotationValue(invisibleAnnotation, "targets");
                if (targets != null) {
                    for (String target : targets) {
                        mixins.add(target.replace(".", "/"));
                    }
                }
            }
        }

        if (className == null) {
            return;
        }

        for (MethodNode methodNode : classNode.methods) {

            if (methodNode.visibleAnnotations == null) {
                continue;
            }

            for (AnnotationNode visibleAnnotation : methodNode.visibleAnnotations) {
                if (visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;")
                        || visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/injection/Redirect;")) {

                    List<String> method = AsmUtils.getAnnotationValue(visibleAnnotation, "method");
                    if (method != null) {
                        methods.put(methodNode.desc, method);
                    }

                    List<AnnotationNode> at = AsmUtils.getAnnotationValue(visibleAnnotation, "at");
                    if (at != null) {
                        for (AnnotationNode annotationNode : at) {
                            String target = AsmUtils.getAnnotationValue(annotationNode, "target");
                            String values = AsmUtils.getAnnotationValue(annotationNode, "value");

                            if (values != null) {
                                if (values.toLowerCase(Locale.ROOT).equals("field")) {
                                    if (target != null) {
                                        targets.add("field:" + target);
                                    }
                                } else {
                                    if (target != null) {
                                        targets.add(target);
                                    }
                                }
                            }
                        }
                    }
                }

                if (visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/gen/Invoker;")) {
                    String value = AsmUtils.getAnnotationValue(visibleAnnotation, "value");
                    if (value != null) {
                        invokes.put(methodNode.desc, value);
                    }
                }

                if (visibleAnnotation.desc.equals("Lorg/spongepowered/asm/mixin/gen/Accessor;")) {
                    String value = AsmUtils.getAnnotationValue(visibleAnnotation, "value");
                    if (value != null) {
                        accessors.put(methodNode.desc, value);
                    }
                }
            }
        }
    }
}
