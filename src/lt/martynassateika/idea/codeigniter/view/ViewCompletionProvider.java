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

package lt.martynassateika.idea.codeigniter.view;

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
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import java.util.List;
import javax.swing.Icon;
import lt.martynassateika.idea.codeigniter.CodeIgniterProjectComponent;
import lt.martynassateika.idea.codeigniter.contributor.BasicFileLookupElement;
import lt.martynassateika.idea.codeigniter.psi.MyPsiUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Provides relative paths for 'view()' calls.
 *
 * @author martynas.sateika
 * @since 0.2.0
 */
public class ViewCompletionProvider extends CompletionProvider<CompletionParameters> {

  @Override
  protected void addCompletions(@NotNull CompletionParameters completionParameters,
      ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
    PsiElement originalPosition = completionParameters.getOriginalPosition();
    if (originalPosition != null) {
      Project project = originalPosition.getProject();
      if (CodeIgniterProjectComponent.isEnabled(project)) {
        if (isViewNameElement(originalPosition)) {
          List<PsiFileSystemItem> viewDirectories = CiViewUtil.getViewDirectories(project);
          for (PsiFileSystemItem viewDirectory : viewDirectories) {
            VirtualFile directoryVirtualFile = viewDirectory.getVirtualFile();
            VirtualFile applicationDirectory = directoryVirtualFile.getParent();
            VfsUtil
                .visitChildrenRecursively(directoryVirtualFile, new VirtualFileVisitor<Object>() {
                  @Override
                  public boolean visitFile(@NotNull VirtualFile file) {
                    if (!file.isDirectory()) {
                      String relativePath = VfsUtil
                          .findRelativePath(directoryVirtualFile, file, '/');
                      if (StringUtil.isNotEmpty(relativePath)) {
                        Icon icon = file.getFileType().getIcon();
                        resultSet.addElement(new BasicFileLookupElement(
                            relativePath,
                            applicationDirectory,
                            icon
                        ));
                      }
                    }
                    return true;
                  }
                });
          }
        }
      }
    }
  }

  /**
   * @param element a PSI element
   * @return true if the element is in the first argument position within a call to 'load->view()'
   */
  private static boolean isViewNameElement(PsiElement element) {
    StringLiteralExpression literalExpression = MyPsiUtil
        .getParentOfType(element, StringLiteralExpression.class);
    if (literalExpression != null) {
      return CiViewUtil.isArgumentOfLoadView(literalExpression, 0);
    }
    return false;
  }

  @NotNull
  public static PsiElementPattern.Capture<LeafPsiElement> getPlace() {
    // view('foo');
    return PlatformPatterns
        .psiElement(LeafPsiElement.class)
        .withParent(StringLiteralExpression.class)
        .withSuperParent(2, ParameterList.class)
        .withSuperParent(3, FunctionReference.class)
        .withLanguage(PhpLanguage.INSTANCE);
  }

}
