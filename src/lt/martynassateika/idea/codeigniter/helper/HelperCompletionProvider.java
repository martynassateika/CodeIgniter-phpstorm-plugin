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

package lt.martynassateika.idea.codeigniter.helper;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import java.util.List;
import javax.swing.Icon;
import lt.martynassateika.idea.codeigniter.CodeIgniterProjectComponent;
import lt.martynassateika.idea.codeigniter.contributor.BasicFileLookupElement;
import lt.martynassateika.idea.codeigniter.psi.MyPsiUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Provides relative paths for 'helper()' calls.
 *
 * @author martynas.sateika
 * @since 0.3.0
 */
public class HelperCompletionProvider extends CompletionProvider<CompletionParameters> {

  @Override
  protected void addCompletions(@NotNull CompletionParameters completionParameters,
      @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
    PsiElement originalPosition = completionParameters.getOriginalPosition();
    if (shouldShowSuggestions(originalPosition)) {
      Project project = originalPosition.getProject();
      List<PsiFileSystemItem> helperDirectories = CiHelperUtil.getHelperDirectories(project);
      for (PsiFileSystemItem helperDirectory : helperDirectories) {
        VirtualFile directoryVirtualFile = helperDirectory.getVirtualFile();
        VirtualFile applicationDirectory = directoryVirtualFile.getParent();
        VfsUtil
            .visitChildrenRecursively(directoryVirtualFile, new VirtualFileVisitor<Object>() {
              @Override
              public boolean visitFile(@NotNull VirtualFile file) {
                if (CiHelperUtil.isHelperFile(file)) {
                  String relativePath = VfsUtil
                      .findRelativePath(directoryVirtualFile, file, '/');
                  if (relativePath != null) {
                    String formattedRelativePath = CiHelperUtil
                        .formatHelperPath(relativePath);
                    if (StringUtil.isNotEmpty(relativePath)) {
                      Icon icon = file.getFileType().getIcon();
                      resultSet.addElement(new BasicFileLookupElement(
                          formattedRelativePath,
                          applicationDirectory,
                          icon
                      ));
                    }
                  }
                }
                return true;
              }
            });
      }
    }
  }

  /**
   * @param originalPosition original position
   * @return if suggestions should be shown
   */
  private static boolean shouldShowSuggestions(PsiElement originalPosition) {
    if (originalPosition != null) {
      Project project = originalPosition.getProject();
      if (CodeIgniterProjectComponent.isEnabled(project)) {
        StringLiteralExpression literalExpression = MyPsiUtil
            .getParentOfType(originalPosition, StringLiteralExpression.class);
        if (literalExpression != null) {
          return CiHelperUtil.isHelperNameElement(literalExpression);
        }
      }
    }
    return false;
  }

  @NotNull
  public static PsiElementPattern.Capture<LeafPsiElement> getPlace() {
    // helper('foo')
    // helper(array('foo', 'bar', 'baz'))
    return PlatformPatterns
        .psiElement(LeafPsiElement.class)
        .withParent(StringLiteralExpression.class)
        .withLanguage(PhpLanguage.INSTANCE);
  }

}
