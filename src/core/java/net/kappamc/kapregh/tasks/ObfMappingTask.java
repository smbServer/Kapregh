package net.kappamc.kapregh.tasks;

import net.kappamc.kapregh.utils.game.GameUtil;
import net.kappamc.kapregh.utils.mappings.MappingUtils;
import net.kappamc.kapregh.utils.mappings.RemappingUtils;

public class ObfMappingTask extends Task {
    public MappingUtils mappingUtil = MappingUtils.getInstance(extensions);
    public RemappingUtils remappingUtil = RemappingUtils.getInstance("obfuscation", mappingUtil.getMap(false));

    public ObfMappingTask() {
        remappingUtil.analyzeJar(GameUtil.getDeobfServerFile(extensions));
    }
}
