/*
 * The MIT License
 * 
 * Copyright (c) 2011 Steven G. Brown
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.filesfoundtrigger;

import static hudson.plugins.filesfoundtrigger.Support.DIRECTORY;
import static hudson.plugins.filesfoundtrigger.Support.FILES;
import static hudson.plugins.filesfoundtrigger.Support.IGNORED_FILES;
import static hudson.plugins.filesfoundtrigger.Support.config;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;

/**
 * Unit test for the {@link FilesFoundTriggerConfig} class.
 * 
 * @author Steven G. Brown
 */
public class FilesFoundTriggerConfigTest {

  /**
   */
  @Rule
  public TemporaryFolderRule folder = new TemporaryFolderRule();

  /**
   */
  @Test
  public void getDirectory() {
    assertThat(config(DIRECTORY, FILES, IGNORED_FILES).getDirectory(),
        is(DIRECTORY));
  }

  /**
   */
  @Test
  public void getDirectoryTrimmed() {
    assertThat(config("  " + DIRECTORY + "  ", FILES, IGNORED_FILES)
        .getDirectory(), is(DIRECTORY));
  }

  /**
   */
  @Test
  public void getFiles() {
    assertThat(config(DIRECTORY, FILES, IGNORED_FILES).getFiles(), is(FILES));
  }

  /**
   */
  @Test
  public void getFilesTrimmed() {
    assertThat(
        config(DIRECTORY, "  " + FILES + "  ", IGNORED_FILES).getFiles(),
        is(FILES));
  }

  /**
   */
  @Test
  public void getIgnoredFiles() {
    assertThat(config(DIRECTORY, FILES, IGNORED_FILES).getIgnoredFiles(),
        is(IGNORED_FILES));
  }

  /**
   */
  @Test
  public void getIgnoredFilesTrimmed() {
    assertThat(config(DIRECTORY, FILES, "  " + IGNORED_FILES + "  ")
        .getIgnoredFiles(), is(IGNORED_FILES));
  }

  /**
   */
  @Test
  public void findFilesDirectoryNotSpecified() {
    FilesFoundTriggerConfig config = config("", FILES, IGNORED_FILES);
    assertThat(config.findFiles(), is(Collections.<String> emptyList()));
  }

  /**
   */
  @Test
  public void findFilesDirectoryNotFound() {
    File nonExistentDirectory = new File(folder.getRoot(), "nonexistent");
    FilesFoundTriggerConfig config = config(nonExistentDirectory
        .getAbsolutePath(), FILES, IGNORED_FILES);
    assertThat(config.findFiles(), is(Collections.<String> emptyList()));
  }

  /**
   */
  @Test
  public void findFilesFilesNotSpecified() {
    FilesFoundTriggerConfig config = config(folder.getRoot().getAbsolutePath(),
        "", IGNORED_FILES);
    assertThat(config.findFiles(), is(Collections.<String> emptyList()));
  }

  /**
   */
  @Test
  public void findFilesNoFiles() {
    FilesFoundTriggerConfig config = config(folder.getRoot().getAbsolutePath(),
        FILES, IGNORED_FILES);
    assertThat(config.findFiles(), is(Collections.<String> emptyList()));
  }

  /**
   * @throws IOException
   *           If an I/O error occurred
   */
  @Test
  public void findFilesOneFile() throws IOException {
    folder.newFile("test");
    FilesFoundTriggerConfig config = config(folder.getRoot().getAbsolutePath(),
        FILES, IGNORED_FILES);
    assertThat(config.findFiles(), is(Collections.singletonList("test")));
  }

  /**
   * @throws IOException
   *           If an I/O error occurred
   */
  @Test
  public void findFilesTwoFiles() throws IOException {
    folder.newFile("test");
    folder.newFile("test2");
    FilesFoundTriggerConfig config = config(folder.getRoot().getAbsolutePath(),
        FILES, IGNORED_FILES);
    assertThat(config.findFiles(), is(Arrays.asList("test", "test2")));
  }

  /**
   * @throws IOException
   *           If an I/O error occurred
   */
  @Test
  public void findFilesNoUnignoredFiles() throws IOException {
    folder.newFile("test");
    FilesFoundTriggerConfig config = config(folder.getRoot().getAbsolutePath(),
        FILES, "**");
    assertThat(config.findFiles(), is(Collections.<String> emptyList()));
  }

  /**
   */
  @Test
  public void toStringContainsDirectory() {
    assertThat(config(DIRECTORY, FILES, IGNORED_FILES).toString(),
        containsString(DIRECTORY));
  }

  /**
   */
  @Test
  public void toStringContainsFiles() {
    assertThat(config(DIRECTORY, FILES, IGNORED_FILES).toString(),
        containsString(FILES));
  }

  /**
   */
  @Test
  public void toStringContainsIgnoredFiles() {
    assertThat(config(DIRECTORY, FILES, IGNORED_FILES).toString(),
        containsString(IGNORED_FILES));
  }

  /**
   */
  @Test
  public void getDisplayName() {
    assertThat(new FilesFoundTriggerConfig.DescriptorImpl().getDisplayName(),
        not(nullValue()));
  }

  /**
   */
  @Test
  public void doTestConfigurationDirectoryNotSpecified() {
    FormValidation result = new FilesFoundTriggerConfig.DescriptorImpl()
        .doTestConfiguration("", FILES, IGNORED_FILES);
    assertThat(result.kind, is(FormValidation.Kind.ERROR));
  }

  /**
   */
  @Test
  public void doTestConfigurationFilesNotSpecified() {
    FormValidation result = new FilesFoundTriggerConfig.DescriptorImpl()
        .doTestConfiguration(DIRECTORY, "", IGNORED_FILES);
    assertThat(result.kind, is(FormValidation.Kind.ERROR));
  }

  /**
   */
  @Test
  public void doTestConfigurationDirectoryNotFound() {
    File nonExistentDirectory = new File(folder.getRoot(), "nonexistent");
    FormValidation result = new FilesFoundTriggerConfig.DescriptorImpl()
        .doTestConfiguration(nonExistentDirectory.getAbsolutePath(), FILES,
            IGNORED_FILES);
    assertThat(result.kind, is(FormValidation.Kind.WARNING));
  }

  /**
   */
  @Test
  public void doTestConfigurationFilesNotFound() {
    FormValidation result = new FilesFoundTriggerConfig.DescriptorImpl()
        .doTestConfiguration(folder.getRoot().getAbsolutePath(), FILES,
            IGNORED_FILES);
    assertThat(result.kind, is(FormValidation.Kind.OK));
  }

  /**
   * @throws IOException
   *           If an I/O error occurred
   */
  @Test
  public void doTestConfigurationFilesFound() throws IOException {
    folder.newFile("test");
    FormValidation result = new FilesFoundTriggerConfig.DescriptorImpl()
        .doTestConfiguration(folder.getRoot().getAbsolutePath(), FILES,
            IGNORED_FILES);
    assertThat(result.kind, is(FormValidation.Kind.OK));
  }
}
