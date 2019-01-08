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

package lt.martynassateika.idea.codeigniter.contributor;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.vfs.VirtualFile;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author martynas.sateika
 * @since 0.3.0
 */
public class BasicFileLookupElement extends LookupElement {

  @NotNull
  private final String relativePath;

  @NotNull
  private final VirtualFile parentDirectory;

  @Nullable
  private final Icon icon;

  public BasicFileLookupElement(@NotNull String relativePath,
      @NotNull VirtualFile parentDirectory,
      @Nullable Icon icon) {
    this.relativePath = relativePath;
    this.parentDirectory = parentDirectory;
    this.icon = icon;
  }

  @NotNull
  @Override
  public String getLookupString() {
    return relativePath;
  }

  @Override
  public void renderElement(LookupElementPresentation presentation) {
    super.renderElement(presentation);
    presentation.setIcon(icon);
    presentation.setTypeText(parentDirectory.getName());
    presentation.setTypeGrayed(true);
  }

}
