package me.loidsemus.pluginlib.config;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testNonExistentFile() {
        Config.loadFile(tempFolder.getRoot(), "nonexistentfile.conf");
        assertThat(Config.file("nonexistentfile").getNode().getValue()).isNull();
        Config.unloadFile("nonexistentfile");
    }

    @Test
    public void saveAndReloadNewConfig() throws IOException {
        Config.loadFile(tempFolder.getRoot(), "new.conf");
        assertThat(Config.file("new").getNode("test").isVirtual());
        Config.file("new").getNode("test").setValue("test");
        Config.file("new").save();
        assertThat(new File(tempFolder.getRoot(), "new.conf").exists());

        Config.file("new").reload();
        assertThat(Config.file("new").getNode("test").isVirtual()).isFalse();
        assertThat(Config.file("new").getNode("test").getString("")).isEqualTo("test");
    }

    @Test
    public void testCopyDefaults() throws IOException {
        // Old file, simulating an old plugin version's config
        File oldFile = tempFolder.newFile("test.conf");
        FileUtils.copyInputStreamToFile(this.getClass().getResourceAsStream("/config/test.conf"), oldFile);
        assertThat(oldFile.exists());

        Config.loadFile(tempFolder.getRoot(), "test.conf");
        Config.file("test").copyDefaults(getClass().getResource("/config/test-new.conf"));

        // Fresh load of the config with all the defaults values merged
        Config.unloadFile("test");
        Config.loadFile(tempFolder.getRoot(), "test.conf");
        assertThat(Config.file("test").getNode("test", "val1").getString("")).isEqualTo("old value");
        assertThat(Config.file("test").getNode("test", "val2").getString("")).isEqualTo("new value");
    }

}
