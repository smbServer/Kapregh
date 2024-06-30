package net.kappamc.kapregh.utils.mappings;

import groovy.util.logging.Log;
import net.kappamc.kapregh.extension.KapreghExtension;
import net.kappamc.kapregh.utils.LogUtils;
import net.kappamc.kapregh.utils.game.GameUtil;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.*;

/*
 * 真正的映射工具包，其MappingUtils为ProGrand映射解析工具
 * 这个是解析Bukkit的csrg并将mojang的部分映射名修改成bukkit的映射名
 *
 * @author: Frish2021
 * @create: 2024/6/10
 */
public class MappingUtil {
    private static final Logger logger = LogUtils.getLogger();
    private static MappingUtil instance;

    public static MappingUtil getInstance(KapreghExtension extensions) {
        if (instance == null) {
            try {
                instance = new MappingUtil(extensions);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return instance;
    }

    public final Map<String, String> classObfToCleanMap = new HashMap<>();
    public final Map<String, String> classCleanToObfMap = new HashMap<>();

    public final List<String> classExcludeList = new ArrayList<>();

    private MappingUtil(KapreghExtension extension) throws IOException {
        File csrgFile = GameUtil.getBuildDataMappingFile(extension);
        File excludeFile = GameUtil.getBuildDataExcludeFile(extension);

        String string = FileUtils.readFileToString(csrgFile, "utf-8").replace("[\r\n]+$", "");
        String[] strings = string.split("\n");

        for (String line : strings) {
            String prefix = " ";

            if (line.startsWith("#")) continue;
            if (line.contains("$")) continue;

            String[] split = line.split(prefix);
            classCleanToObfMap.put(split[1].trim(), split[0].trim());
            classObfToCleanMap.put(split[0].trim(), split[1].trim());
        }

        String string1 = FileUtils.readFileToString(excludeFile, "utf-8").replace("[\r\n]+$", "");
        String[] strings1 = string1.split("\n");

        for (String line : strings1) {
            if (line.startsWith("#")) continue;
            if (line.contains("$")) continue;

            classExcludeList.add(line);
        }
    }
}
