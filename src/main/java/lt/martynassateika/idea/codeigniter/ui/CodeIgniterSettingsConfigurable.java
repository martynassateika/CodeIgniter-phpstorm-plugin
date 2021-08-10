/*
 * Copyright 2018 Martynas Sateika
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lt.martynassateika.idea.codeigniter.ui;

import com.intellij.openapi.options.SearchableConfigurable;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.intellij.openapi.project.Project;
import lt.martynassateika.idea.codeigniter.CodeIgniterProjectSettings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Holds components of the project settings window.
 *
 * @author martynas.sateika
 * @since 0.1.0
 */
public class CodeIgniterSettingsConfigurable implements SearchableConfigurable {

  @NotNull
  private final Project myProject;

  private CodeIgniterConfigurablePanel panel;

  public CodeIgniterSettingsConfigurable(@NotNull Project project) {
    this.myProject = project;
  }

  @Nls(capitalization = Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "CodeIgniter";
  }

  @NotNull
  @Override
  public String getHelpTopic() {
    return "preferences.CodeIgniter";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    panel = new CodeIgniterConfigurablePanel();
    return panel.myWholePanel;
  }

  @Override
  public boolean isModified() {
    final CodeIgniterProjectSettings settings = CodeIgniterProjectSettings.getInstance(myProject);
    return panel.isModified(settings);
  }

  @Override
  public void apply() {
    final CodeIgniterProjectSettings settings = CodeIgniterProjectSettings.getInstance(myProject);
    panel.apply(settings);
  }

  @Override
  public void reset() {
    final CodeIgniterProjectSettings settings = CodeIgniterProjectSettings.getInstance(myProject);
    panel.reset(settings);
  }

  @Override
  public void disposeUIResources() {
    panel = null;
  }

  @NotNull
  @Override
  public String getId() {
    return getHelpTopic();
  }

  @Nullable
  @Override
  public Runnable enableSearch(String s) {
    return null; // default method overridden for 2016.1 support
  }

  public static class CodeIgniterConfigurablePanel {

    private JPanel myWholePanel;

    private JCheckBox isEnabledCheckBox;

    private void reset(CodeIgniterProjectSettings settings) {
      final boolean isEnabled = settings.isEnabled();
      isEnabledCheckBox.setSelected(isEnabled);
    }

    private void apply(CodeIgniterProjectSettings settings) {
      settings.setEnabled(isEnabledCheckBox.isSelected());
    }

    private boolean isModified(CodeIgniterProjectSettings settings) {
      final boolean isEnabled = settings.isEnabled();
      return isEnabledCheckBox.isSelected() != isEnabled;
    }
  }

}
