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

package lt.martynassateika.idea.codeigniter.language;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression;
import com.jetbrains.php.lang.psi.elements.ArrayIndex;
import com.jetbrains.php.lang.psi.elements.AssignmentExpression;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.visitors.PhpRecursiveElementVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lt.martynassateika.idea.codeigniter.psi.MyPsiUtil;

/**
 * Utility methods related to the CI Language class.
 *
 * @author martynas.sateika
 * @since 0.1.0
 */
class CiLanguageUtil {

  /**
   * Returns {@code true} if the supplied file is a CI language file.
   *
   * According to the <a href="https://www.codeigniter.com/userguide3/libraries/language.html#creating-language-files">CI
   * documentation</a>, "Language files must be named with _lang.php as the filename extension".
   *
   * @param file a PHP file
   * @return {@code true} if the file is a CI language file
   */
  static boolean isLanguageFile(PhpFile file) {
    return file.getName().endsWith("_lang.php");
  }

  /**
   * Returns {@code true} if the supplied element represents a language line key.
   *
   * @param element an element
   * @return {@code true} if the supplied element represents a language line key
   */
  static boolean isLanguageLineKeyElement(PsiElement element) {
    return MyPsiUtil.isArgumentOfFunction(element, "lang", 0);
  }

  /**
   * @param project current project
   * @param literalExpression containing the language key
   * @return assignment expressions matching the language key across all language files
   */
  static List<AssignmentExpression> findTranslationsFor(
      Project project,
      StringLiteralExpression literalExpression) {
    return findTranslationsFor(project, literalExpression.getContents());
  }

  /**
   * @param project current project
   * @param text language key
   * @return assignment expressions matching the language key across all language files
   */
  private static List<AssignmentExpression> findTranslationsFor(Project project, String text) {
    List<AssignmentExpression> expressions = new ArrayList<>();
    FileBasedIndex.getInstance().getFilesWithKey(LanguageFileIndex.KEY,
        Collections.singleton(text), file -> {
          PsiManager psiManager = PsiManager.getInstance(project);
          PsiFile psiFile = psiManager.findFile(file);
          if (psiFile instanceof PhpFile) {
            //noinspection deprecation
            psiFile.accept(new PhpRecursiveElementVisitor() {
              @Override
              public void visitPhpAssignmentExpression(AssignmentExpression assignmentExpression) {
                PhpPsiElement variable = assignmentExpression.getVariable();
                if (variable instanceof ArrayAccessExpression) {
                  ArrayAccessExpression arrayAccessExpression = (ArrayAccessExpression) variable;
                  ArrayIndex index = arrayAccessExpression.getIndex();
                  if (index != null && index.getValue() instanceof StringLiteralExpression) {
                    StringLiteralExpression indexValue = (StringLiteralExpression) index.getValue();
                    if (indexValue.getContents().equals(text)) {
                      expressions.add(assignmentExpression);
                    }
                  }
                }
              }
            });
          }
          return true;
        }, GlobalSearchScope
            .getScopeRestrictedByFileTypes(GlobalSearchScope.allScope(project),
                PhpFileType.INSTANCE));
    return expressions;
  }

}
