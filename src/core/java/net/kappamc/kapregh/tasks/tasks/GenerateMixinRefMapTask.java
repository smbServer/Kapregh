package net.kappamc.kapregh.tasks.tasks;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.kappamc.kapregh.mixin.MixinMapping;
import net.kappamc.kapregh.tasks.ObfMappingTask;
import net.kappamc.kapregh.utils.MethodUtils;
import net.kappamc.kapregh.utils.mappings.MappingUtils;
import org.apache.commons.io.FileUtils;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class GenerateMixinRefMapTask extends ObfMappingTask {
    @TaskAction
    public void generateMixinRefMap() {
        logger.info("Generating mixin reference map...");

        if (extensions.mixin.referenceMap != null) {
            try {
                JsonObject mixinReferenceMap = new JsonObject();
                JsonObject mixinMappings = new JsonObject();

                Files.walkFileTree(classesDir.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        final MixinMapping mixinMapping = new MixinMapping();
                        mixinMapping.accept(FileUtils.readFileToByteArray(file.toFile()));
                        remappingUtil.superHashMap.put(mixinMapping.className, new HashSet<>(mixinMapping.mixins));
                        for (String mixin : mixinMapping.mixins) {
                            JsonObject mapping = new JsonObject();

                            // Method
                            mixinMapping.methods.forEach((descriptor, methods) -> {
                                for (String method : methods) {
                                    if (!method.contains("(")) {
                                        String descriptors = getCleanDescByObfDesc(descriptor.replace("Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;", ""));

                                        String clean = mixin + "." + method + " " + descriptors;
                                        String obf = getMethodObf(clean, mappingUtil);
                                        String mixinObf = obf.replace(".", ";").replace(" ", "");

                                        if (!mapping.has(method)) {
                                            mapping.addProperty(method, mixinObf);
                                        }
                                    } else {
                                        String name = method.substring(0, method.indexOf("("));
                                        String desc = method.substring(method.indexOf("("));
                                        String clean = mixin + "." + name + " " + desc;
                                        String obf = getMethodObf(clean, mappingUtil);
                                        String mixinObf = obf.replace(".", ";").replace(" ", "");

                                        if (!mapping.has(method)) {
                                            mapping.addProperty(method, mixinObf);
                                        }
                                    }
                                }
                            });

                            // Target
                            for (String mixinTarget : mixinMapping.targets) {
                                if (!mixinTarget.contains("field:")) {
                                    String desc = mixinTarget.substring(mixinTarget.indexOf("("));
                                    String name = mixinTarget.substring(mixinTarget.indexOf(";") + 1, mixinTarget.indexOf("("));
                                    String clazz = mixinTarget.substring(1, mixinTarget.indexOf(";"));

                                    String obf = getMethodObf(clazz + "." + name + " " + desc, mappingUtil);
                                    if (!mapping.has(mixinTarget)) {
                                        mapping.addProperty(mixinTarget, obf);
                                    }
                                } else {
                                    String field = mixinTarget.split("field:")[1];

                                    String clean = field.substring(1, field.indexOf(":")).replace(";", ".");
                                    String desc = field.substring(field.indexOf(":") + 1);
                                    if (MethodUtils.notPrimitives(desc)) {
                                        String cleans = desc.substring(desc.indexOf("L") + 1, desc.indexOf(";"));
                                        desc = "L" + mappingUtil.classCleanToObfMap.getOrDefault(cleans, cleans);
                                    }
                                    String obf = getFieldObf(clean, mappingUtil);

                                    if (!mapping.has(mixinTarget)) {
                                        mapping.addProperty(field, obf + ":" + desc);
                                    }
                                }
                            }

                            // Accessors
                            mixinMapping.accessors.forEach((k, v) -> {
                                String clean = mixin + "." + v;
                                String obf = mappingUtil.fieldCleanToObfMap.getOrDefault(clean, clean);
                                String name = obf.substring(obf.indexOf(".") + 1);
                                String desc = k.substring(k.indexOf(")") + 1);

                                if (MethodUtils.notPrimitives(desc)) {
                                    String cleans = desc.substring(desc.indexOf("L") + 1, desc.indexOf(";"));
                                    desc = "L" + mappingUtil.classCleanToObfMap.getOrDefault(cleans, cleans);
                                }

                                if (!mapping.has(v)) {
                                    mapping.addProperty(v, "L" + mixin + ";" + name + ":" + desc);
                                }
                            });

                            // Invoker
                            for (Map.Entry<String, String> entry : mixinMapping.invokes.entrySet()) {
                                String descriptor = getCleanDescByObfDesc(entry.getKey());

                                String clean = mixin + "." + entry.getValue() + " " + descriptor;
                                String obf = getMethodObf(clean, mappingUtil);
                                String mixinObf = obf.replace(".", ";").replace(" ", "");

                                if (!mapping.has(entry.getValue())) {
                                    mapping.addProperty(entry.getValue(), mixinObf);
                                }
                            }

                            mixinMappings.add(mixinMapping.className, mapping);
                        }

                        mixinReferenceMap.add("mappings", mixinMappings);
                        return super.visitFile(file, attrs);
                    }
                });

                JavaPluginExtension java = getProject().getExtensions().getByType(JavaPluginExtension.class);
                for (SourceSet sourceSet : java.getSourceSets()) {
                    if (!resourcesDir.exists()) {
                        resourcesDir.mkdir();
                    }
                    File dir = new File(resourcesDir, sourceSet.getName());
                    if (!dir.exists()) {
                        dir.mkdir();
                    }

                    if (extensions.mixin.referenceMap != null) {
                        try {
                            FileUtils.write(new File(dir, extensions.mixin.referenceMap), new GsonBuilder().setPrettyPrinting().create().toJson(mixinReferenceMap), StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            getProject().getLogger().lifecycle(e.getMessage(), e);
                        }
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private String getMethodObf(String clean, MappingUtils mappingUtil) {
        String method_obf = mappingUtil.methodCleanToObfMap.getOrDefault(clean, clean);
        String method_obf_pack = method_obf.substring(0, method_obf.indexOf("."));
        String method_obf_name = method_obf.substring(method_obf.indexOf(".") + 1, method_obf.indexOf(" "));
        String method_obf_desc = method_obf.substring(method_obf.indexOf(" ") + 1);

        return "L" + method_obf_pack + ";" + method_obf_name + method_obf_desc;
    }

    private String getFieldObf(String clean, MappingUtils mappingUtil) {
        String field_obf = mappingUtil.fieldCleanToObfMap.getOrDefault(clean, clean);
        String field_clazz = field_obf.substring(0, field_obf.indexOf("."));
        String field_name = field_obf.substring(field_obf.indexOf(".") + 1);

        return "L" + field_clazz + ";" + field_name;
    }

    private String getCleanDescByObfDesc(String desc) {
        final Map<String, String> map = new HashMap<>();

        for (Map.Entry<String, String> stringStringEntry : mappingUtil.methodObfToCleanMap.entrySet()) {
            String obf = stringStringEntry.getKey().substring(stringStringEntry.getKey().indexOf(" ") + 1);
            String clean = stringStringEntry.getValue().substring(stringStringEntry.getValue().indexOf(" ") + 1);

            if (!map.containsKey(obf)) map.put(obf, clean);
        }

        return map.getOrDefault(desc, desc);
    }
}
