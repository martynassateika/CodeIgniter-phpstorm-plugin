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

package lt.martynassateika.idea.codeigniter.model;

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
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import java.util.List;
import javax.swing.Icon;

import lt.martynassateika.idea.codeigniter.CodeIgniterProjectSettings;
import lt.martynassateika.idea.codeigniter.PhpExtensionUtil;
import lt.martynassateika.idea.codeigniter.compat.VfsUtilCompat;
import lt.martynassateika.idea.codeigniter.contributor.BasicFileLookupElement;
import lt.martynassateika.idea.codeigniter.psi.MyPsiUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Provides relative paths for 'model()' calls.
 *
 * @author martynas.sateika
 * @since 0.4.0
 */
public class ModelCompletionProvider extends CompletionProvider<CompletionParameters> {

  @Override
  protected void addCompletions(@NotNull CompletionParameters completionParameters,
      @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
    PsiElement originalPosition = completionParameters.getOriginalPosition();
    if (originalPosition != null) {
      Project project = originalPosition.getProject();
      if (CodeIgniterProjectSettings.getInstance(project).isEnabled()) {
        if (isModelNameElement(originalPosition)) {
          List<PsiFileSystemItem> modelDirectories = CiModelUtil.getModelDirectories(project);
          for (PsiFileSystemItem modelDirectory : modelDirectories) {
            VirtualFile directoryVirtualFile = modelDirectory.getVirtualFile();
            VirtualFile applicationDirectory = directoryVirtualFile.getParent();
            VfsUtil
                .visitChildrenRecursively(directoryVirtualFile, new VirtualFileVisitor<Object>() {
                  @Override
                  public boolean visitFile(@NotNull VirtualFile file) {
                    if (file.getFileType() == PhpFileType.INSTANCE) {
                      String relativePath = VfsUtilCompat
                          .findRelativePath(directoryVirtualFile, file, '/');
                      if (StringUtil.isNotEmpty(relativePath)) {
                        Icon icon = file.getFileType().getIcon();
                        resultSet.addElement(new BasicFileLookupElement(
                            PhpExtensionUtil.removeIfPresent(relativePath.toLowerCase()),
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
   * @return true if the element is in the first argument position within a call to 'load->model()'
   */
  private static boolean isModelNameElement(PsiElement element) {
    StringLiteralExpression literalExpression = MyPsiUtil
        .getParentOfType(element, StringLiteralExpression.class);
    if (literalExpression != null) {
      return CiModelUtil.isArgumentOfLoadModel(literalExpression, 0);
    }
    return false;
  }

  @NotNull
  public static PsiElementPattern.Capture<LeafPsiElement> getPlace() {
    // model('foo');
    return PlatformPatterns
        .psiElement(LeafPsiElement.class)
        .withParent(StringLiteralExpression.class)
        .withSuperParent(2, ParameterList.class)
        .withSuperParent(3, FunctionReference.class)
        .withLanguage(PhpLanguage.INSTANCE);
  }

}
