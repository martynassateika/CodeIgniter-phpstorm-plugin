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

package lt.martynassateika.idea.codeigniter;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lt.martynassateika.idea.codeigniter.compat.MyAbstractProjectComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Holds project-specific CodeIgniter settings.
 *
 * @author martynas.sateika
 * @since 0.1.0
 */
@State(name = "CodeIgniterProjectSettings")
public class CodeIgniterProjectSettings extends MyAbstractProjectComponent
    implements PersistentStateComponent<CodeIgniterProjectSettings> {

  @SuppressWarnings("WeakerAccess")
  public boolean isEnabled;

  public CodeIgniterProjectSettings() {
    this.isEnabled = false;
  }

  @Nullable
  @Override
  public CodeIgniterProjectSettings getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull CodeIgniterProjectSettings state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public void setEnabled(boolean enabled) {
    isEnabled = enabled;
  }

}
