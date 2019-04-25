/*
 * Copyright 2019 Martynas Sateika
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

package lt.martynassateika.idea.codeigniter.config;

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
import com.jetbrains.php.lang.psi.visitors.PhpRecursiveElementVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lt.martynassateika.idea.codeigniter.psi.MyPsiUtil;

/**
 * Utility methods related to the CI Config.
 *
 * @author martynas.sateika
 * @since 0.5.0
 */
class CiConfigUtil {

  /**
   * Returns {@code true} if the supplied file is a CI config file.
   *
   * @param file a PHP file
   * @return {@code true} if the file is a CI config file
   */
  static boolean isConfigFile(PhpFile file) {
    return file.getContainingDirectory().getName().equals("config");
  }

  /**
   * Returns {@code true} if the supplied element represents a config item name.
   *
   * @param element an element
   * @return {@code true} if the supplied element represents a config item name
   */
  static boolean isConfigItemNameElement(PsiElement element) {
    return isArgumentOfConfigItemMethod(element)
        || isArgumentOfConfigItemFunction(element)
        || isArgumentOfConfigSetItem(element)
        || isArgumentOfConfigSlashItem(element);
  }

  private static boolean isArgumentOfConfigItemMethod(PsiElement element) {
    return MyPsiUtil.isArgumentOfMethod(element, "config", "item", 0);
  }

  private static boolean isArgumentOfConfigItemFunction(PsiElement element) {
    return MyPsiUtil.isArgumentOfFunction(element, "config_item", 0);
  }

  private static boolean isArgumentOfConfigSlashItem(PsiElement element) {
    return MyPsiUtil.isArgumentOfMethod(element, "config", "slash_item", 0);
  }

  private static boolean isArgumentOfConfigSetItem(PsiElement element) {
    return MyPsiUtil.isArgumentOfMethod(element, "config", "set_item", 0);
  }

  /**
   * @param project current project
   * @param text config key
   * @return assignment expressions matching the config key across all config files
   */
  @SuppressWarnings("deprecation")
  static List<AssignmentExpression> findValuesFor(Project project, String text) {
    List<AssignmentExpression> expressions = new ArrayList<>();
    FileBasedIndex.getInstance().getFilesWithKey(ConfigFileIndex.KEY,
        Collections.singleton(text), file -> {
          PsiManager psiManager = PsiManager.getInstance(project);
          PsiFile psiFile = psiManager.findFile(file);
          if (psiFile instanceof PhpFile) {
            psiFile.accept(new PhpRecursiveElementVisitor() {
              @Override
              public void visitPhpAssignmentExpression(AssignmentExpression assignmentExpression) {
                PhpPsiElement variable = assignmentExpression.getVariable();
                if (variable instanceof ArrayAccessExpression) {
                  ArrayAccessExpression arrayAccessExpression = (ArrayAccessExpression) variable;
                  ArrayIndex index = arrayAccessExpression.getIndex();
                  if (index != null) {
                    if (index.getText().equals(text)) {
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
