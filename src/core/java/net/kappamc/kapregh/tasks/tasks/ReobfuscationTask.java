package net.kappamc.kapregh.tasks.tasks;

import net.kappamc.kapregh.tasks.ObfMappingTask;
import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class ReobfuscationTask extends ObfMappingTask {
    @TaskAction
    public void reObfuscation() {
        logger.info("obfuscating...");

        try {
            Files.walkFileTree(classesDir.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    FileUtils.writeByteArrayToFile(file.toFile(), remappingUtil.remapping(FileUtils.readFileToByteArray(file.toFile())));
                    remappingUtil.analyze(FileUtils.readFileToByteArray(file.toFile()));
                    return super.visitFile(file, attrs);
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}