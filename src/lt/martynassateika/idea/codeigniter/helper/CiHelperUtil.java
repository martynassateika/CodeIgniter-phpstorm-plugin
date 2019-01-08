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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lt.martynassateika.idea.codeigniter.psi.MyPsiUtil;

/**
 * Utility methods related to CI Helpers.
 *
 * @author martynas.sateika
 * @since 0.3.0
 */
class CiHelperUtil {

  private static final String FILE_SUFFIX = "_helper.php";

  /**
   * @param file a file
   * @return whether {@code file} should be considered a helper file
   */
  static boolean isHelperFile(VirtualFile file) {
    return file.getFileType() == PhpFileType.INSTANCE && file.getName().endsWith(FILE_SUFFIX);
  }

  /**
   * @param relativePath a relative path
   * @return the relative path formatted so as not to have the {@link #FILE_SUFFIX} at the end
   */
  static String formatHelperPath(String relativePath) {
    return relativePath.replaceAll("(_helper)?(\\.php)?$", "");
  }

  /**
   * Returns a list of helper files whose relative path from a helper directory equals the supplied
   * relative path.
   *
   * @param relativePath relative path to a helper file
   * @param project current project
   * @return list of all matching helper files
   */
  static List<PsiFile> findHelperFiles(String relativePath, Project project) {
    PsiManager psiManager = PsiManager.getInstance(project);

    // Get rid of various possible combinations of '_helper' and '.php', then add the suffix back
    String fullPath = formatHelperPath(relativePath) + FILE_SUFFIX;

    List<PsiFile> helperFiles = new ArrayList<>();
    for (PsiFileSystemItem fileSystemItem : getHelperDirectories(project)) {
      VirtualFile helperDirectory = fileSystemItem.getVirtualFile();
      VirtualFile helperFile = helperDirectory.findFileByRelativePath(fullPath);
      if (helperFile != null && !helperFile.isDirectory()) {
        PsiFile psiFile = psiManager.findFile(helperFile);
        if (psiFile != null) {
          helperFiles.add(psiFile);
        }
      }
    }
    return helperFiles;
  }

  /**
   * @param project current project
   * @return all directories called 'helpers' in the project
   */
  static List<PsiFileSystemItem> getHelperDirectories(Project project) {
    GlobalSearchScope scope = GlobalSearchScope.allScope(project);
    PsiFileSystemItem[] items = FilenameIndex.getFilesByName(project, "helpers", scope, true);
    return Arrays.stream(items).filter(PsiFileSystemItem::isDirectory).collect(Collectors.toList());
  }

  /**
   * Returns true if the supplied element represents the name of a helper file.
   *
   * Helper file names can be specified in several ways:
   * <pre>{@code
   * $this->load->helper('>foo<'); // single helper
   * $this->load->helper('>foo<', '>bar<', '>baz<'); // multiple helpers
   * }</pre>
   *
   * @param literalExpression a string literal expression
   * @return true if the element represents the name of a helper file
   */
  static boolean isHelperNameElement(StringLiteralExpression literalExpression) {
    PsiElement arrayValue = literalExpression.getParent();
    if (arrayValue.getNode().getElementType() == PhpElementTypes.ARRAY_VALUE) {
      // Check if defined in array
      ArrayCreationExpression arrayCreationExpression = MyPsiUtil
          .getParentOfType(arrayValue, ArrayCreationExpression.class);
      return isArgumentOfLoadHelper(arrayCreationExpression, 0);
    } else {
      // Check if single argument call
      return isArgumentOfLoadHelper(literalExpression, 0);
    }
  }

  /**
   * @param element an element
   * @param argIndex method parameter index (0-based)
   * @return {@code true} if {@code element} is an argument of a {@code load->view()} call
   */
  private static boolean isArgumentOfLoadHelper(PsiElement element, int argIndex) {
    return MyPsiUtil.isArgumentOfMethod(element, "load", "helper", argIndex);
  }

}
