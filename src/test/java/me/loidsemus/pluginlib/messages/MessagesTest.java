package me.loidsemus.pluginlib.messages;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class MessagesTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testDefault() {
        Messages.load(tempFolder.getRoot(), "default", LangKey.values(), LangKey.PREFIX);
        assertThat(new File(tempFolder.getRoot(), "lang_default.properties")).exists();
        assertThat(Messages.get(LangKey.PREFIX, false)).isEqualTo("prefix");
        assertThat(Messages.get(LangKey.TEST_MESSAGE, true, "placeholder")).isEqualTo("prefix Test message: placeholder");
    }

    @Test
    public void testCustom() throws IOException {
        // Copy file contents from the resource
        File file = tempFolder.newFile("lang_custom.properties");
        FileUtils.copyInputStreamToFile(this.getClass().getResourceAsStream("/lang_custom.properties"), file);

        Messages.load(tempFolder.getRoot(), "custom", LangKey.values(), LangKey.PREFIX);
        assertThat(Messages.get(LangKey.PREFIX, false)).isEqualTo("customprefix");
        assertThat(Messages.get(LangKey.TEST_MESSAGE, true, "placeholder")).isEqualTo("customprefix Custom test message: placeholder");
    }

    @Test
    public void testLoadFailure() {
        System.out.println("Testing failure, warning is expected");
        boolean result = Messages.load(tempFolder.getRoot(),"test",LangKey.values(),LangKey.PREFIX);
        assertThat(result).isFalse();
    }

    private enum LangKey implements Translatable {
        PREFIX("prefix"),
        TEST_MESSAGE("Test message: {test}", "test");

        private final String defaultValue;
        private final String[] args;

        LangKey(String defaultValue, String... args) {
            this.defaultValue = defaultValue;
            this.args = args;
        }

        @Override
        public String getDefaultValue() {
            return defaultValue;
        }

        @Override
        public String[] getArgs() {
            return args;
        }

        @Override
        public String getKey() {
            return this.toString().toLowerCase();
        }
    }

}
