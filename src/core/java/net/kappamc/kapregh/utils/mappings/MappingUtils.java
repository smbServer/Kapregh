package net.kappamc.kapregh.utils.mappings;

import net.kappamc.kapregh.extension.KapreghExtension;
import net.kappamc.kapregh.utils.MethodUtils;
import net.kappamc.kapregh.utils.game.GameUtil;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/*
 * 一个解析映射的工具包，我只是做了对Spigot混淆名的修改
 *
 * @author: Enaium
 * @create: 2022/?/?
 * @modifier: Frish2021
 * @modified: 2024/6/22
 */
public class MappingUtils {
    private static MappingUtils instance;
    private static MappingUtil mappingUtil;

    public static MappingUtils getInstance(KapreghExtension extensions) {
        if (instance == null) {
            try {
                mappingUtil = MappingUtil.getInstance(extensions);
                instance = new MappingUtils(extensions);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    private final Map<String, String> fatherClassCleanToObfMap = new HashMap<>();
    public final Map<String, String> classObfToCleanMap = new HashMap<>();
    public final Map<String, String> classCleanToObfMap = new HashMap<>();
    public final Map<String, String> fieldObfToCleanMap = new HashMap<>();
    public final Map<String, String> fieldCleanToObfMap = new HashMap<>();
    public final Map<String, String> methodObfToCleanMap = new HashMap<>();
    public final Map<String, String> methodCleanToObfMap = new HashMap<>();


    private MappingUtils(KapreghExtension extensions) throws IOException {
        String text = FileUtils.readFileToString(GameUtil.getMojangMappingFile(extensions), StandardCharsets.UTF_8);

        String NAME_LINE = "^.+:";
        String SPLITTER = "( |->)+";
        String LINE = "\\r\\n|\\n";

        {
            String[] lines = text.split(LINE);
            for (String line : lines) {
                if (line.startsWith("#"))
                    continue;

                //class
                if (line.matches(NAME_LINE)) {
                    String[] split = line.split(SPLITTER);
                    String clean = MethodUtils.internalize(split[0]);
                    String obf = MethodUtils.internalize(split[1]);
                    obf = obf.substring(0, obf.indexOf(':'));
                    fatherClassCleanToObfMap.put(clean, obf);
                    if (!mappingUtil.classExcludeList.contains(obf)) {
                        clean = mappingUtil.classObfToCleanMap.getOrDefault(obf, clean);
                    }

                    if (!line.contains("$")) {
                        classObfToCleanMap.put(obf, clean);
                        classCleanToObfMap.put(clean, obf);
                    }

                    if (line.contains("$")) {
                        String obfs = obf.substring(0, obf.indexOf("$"));
                        String new_clean = clean.replace(clean.substring(0, clean.indexOf("$")), classObfToCleanMap.get(obfs));

                        classObfToCleanMap.put(obf, new_clean);
                        classCleanToObfMap.put(new_clean, obf);
                    }
                }
            }
        }

        {
            String[] lines = text.split(LINE);
            String currentObfClass = null;
            String currentCleanClass = null;
            for (String line : lines) {
                if (line.startsWith("#"))
                    continue;

                if (line.matches(NAME_LINE)) {
                    currentObfClass = MethodUtils.internalize(line.substring(line.lastIndexOf(" ") + 1, line.indexOf(":")));
                    currentCleanClass = classObfToCleanMap.getOrDefault(currentObfClass, MethodUtils.internalize(currentObfClass));
                    continue;
                }

                if (currentObfClass == null)
                    continue;

                if (!line.contains("(")) {
                    //Field
                    String[] split = line.trim().split(SPLITTER);
                    String clean = currentCleanClass + "." + split[1];
                    String obf = currentObfClass + "." + split[2];
                    fieldObfToCleanMap.put(obf, clean);
                    fieldCleanToObfMap.put(clean, obf);
                } else {
                    //Method
                    String[] split = line.contains(":") ? line.substring(line.lastIndexOf(":") + 1).trim().split(SPLITTER) : line.trim().split(SPLITTER);
                    String cleanReturn = split[0];
                    if (!cleanReturn.contains("[")) {
                        if (MethodUtils.notPrimitive(cleanReturn)) {
                            cleanReturn = "L" + getBukkitName(cleanReturn, false) + ";";
                        } else {
                            cleanReturn = MethodUtils.internalize(cleanReturn);
                        }
                    } else {
                        String className = cleanReturn.substring(0, cleanReturn.indexOf("["));

                        if (MethodUtils.notPrimitive(className)) {
                            cleanReturn = "L" + cleanReturn.replace(className, getBukkitName(className, false)) + ";";
                        } else {
                            cleanReturn = cleanReturn.replace(className, MethodUtils.internalize(className));
                        }
                    }

                    String cleanName = split[1].substring(0, split[1].lastIndexOf("("));
                    String cleanArgs = split[1].substring(split[1].indexOf("(") + 1, split[1].lastIndexOf(")"));
                    String obfReturn = split[0];
                    if (!obfReturn.contains("[")) {
                        if (MethodUtils.notPrimitive(obfReturn)) {
                            obfReturn = "L" + getBukkitName(MethodUtils.internalize(split[0]), true) + ";";
                        } else {
                            obfReturn = cleanReturn;
                        }
                    } else {
                        String className = obfReturn.substring(0, obfReturn.indexOf("["));

                        if (MethodUtils.notPrimitive(className)) {
                            obfReturn = "L" + obfReturn.replace(className, getBukkitName(className, true)) + ";";
                        } else {
                            obfReturn = cleanReturn;
                        }
                    }

                    String obfName = split[2];
                    String obfArgs;

                    if (!cleanArgs.isEmpty()) {
                        StringBuilder tempCleanArs = new StringBuilder();
                        StringBuilder tempObfArs = new StringBuilder();
                        for (String s : cleanArgs.split(",")) {
                            if (!s.contains("[")) {
                                if (MethodUtils.notPrimitive(s)) {
                                    tempObfArs.append("L").append(getBukkitName(s, true)).append(";");
                                    tempCleanArs.append("L").append(getBukkitName(s, false)).append(";");
                                } else {
                                    String internalize = MethodUtils.internalize(s);
                                    tempObfArs.append(internalize);
                                    tempCleanArs.append(internalize);
                                }
                            } else {
                                String className = s.substring(0, s.indexOf("["));

                                if (MethodUtils.notPrimitive(className)) {
                                    String cleanReplace = s.replace(className, getBukkitName(className, true));
                                    String obfReplace = s.replace(className, getBukkitName(className, false));
                                    tempObfArs.append("L").append(cleanReplace).append(";");
                                    tempCleanArs.append("L").append(obfReplace).append(";");
                                } else {
                                    String replace = s.replace(className, MethodUtils.internalize(className));
                                    tempObfArs.append(replace);
                                    tempCleanArs.append(replace);
                                }
                            }
                        }
                        obfArgs = "(" + tempObfArs + ")";
                        cleanArgs = "(" + tempCleanArs + ")";
                    } else {
                        obfArgs = "()";
                        cleanArgs = "()";
                    }

                    String obf = currentObfClass + "." + obfName + " " + obfArgs + obfReturn;
                    String clean = currentCleanClass + "." + cleanName + " " + cleanArgs + cleanReturn;
                    methodObfToCleanMap.put(obf, clean);
                    methodCleanToObfMap.put(clean, obf);
                }
            }
        }
    }

    public Map<String, String> getMap(boolean clean) {
        Map<String, String> map = new HashMap<>();
        if (clean) {
            map.putAll(classObfToCleanMap);
        } else {
            map.putAll(classCleanToObfMap);
        }

        fieldObfToCleanMap.forEach((k, v) -> {
            String key = clean ? k : v;
            String value = clean ? v : k;
            String obfClassName = key.substring(0, key.lastIndexOf("."));
            String obfFieldName = key.substring(key.lastIndexOf(".") + 1);
            map.put(obfClassName + "." + obfFieldName, value.substring(value.lastIndexOf(".") + 1));
        });

        methodObfToCleanMap.forEach((k, v) -> {
            String key = clean ? k : v;
            String value = clean ? v : k;
            String obfLeft = key.split(" ")[0];
            String obfRight = key.split(" ")[1];
            String cleanLeft = value.split(" ")[0];
            String cleanMethodName = cleanLeft.substring(cleanLeft.lastIndexOf(".") + 1);
            String obfClassName = obfLeft.substring(0, obfLeft.lastIndexOf("."));
            String obfMethodName = obfLeft.substring(obfLeft.lastIndexOf(".") + 1);
            map.put(obfClassName + "." + obfMethodName + obfRight, cleanMethodName);
        });
        return map;
    }

    private String getBukkitName(String mojangName, boolean obfs) {
        String internalize = MethodUtils.internalize(mojangName);

        if (!obfs) {
            String father_obf = fatherClassCleanToObfMap.getOrDefault(internalize, internalize);
            return classObfToCleanMap.getOrDefault(father_obf, father_obf);
        } else {
            return fatherClassCleanToObfMap.getOrDefault(internalize, internalize);
        }
    }
}
