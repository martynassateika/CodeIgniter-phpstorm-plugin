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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lt.martynassateika.idea.codeigniter.psi.MyPsiUtil;

/**
 * Utility methods related to the CI Views.
 *
 * @author martynas.sateika
 * @since 0.2.0
 */
class CiViewUtil {

  /**
   * Returns a list of view files whose relative path from a view directory equals the supplied
   * relative path.
   *
   * @param relativePath relative path to a view file
   * @param project current project
   * @return list of all matching view files
   */
  static List<PsiFile> findViewFiles(String relativePath, Project project) {
    PsiManager psiManager = PsiManager.getInstance(project);

    // If no extension is specified, it's a PHP file
    relativePath = PhpExtensionUtil.addIfMissing(relativePath);

    List<PsiFile> viewFiles = new ArrayList<>();
    for (PsiFileSystemItem fileSystemItem : getViewDirectories(project)) {
      VirtualFile viewDirectory = fileSystemItem.getVirtualFile();
      VirtualFile viewFile = viewDirectory.findFileByRelativePath(relativePath);
      if (viewFile != null && !viewFile.isDirectory()) {
        PsiFile psiFile = psiManager.findFile(viewFile);
        if (psiFile != null) {
          viewFiles.add(psiFile);
        }
      }
    }
    return viewFiles;
  }

  /**
   * @param project current project
   * @return all directories called 'views' in the project
   */
  static List<PsiFileSystemItem> getViewDirectories(Project project) {
    GlobalSearchScope scope = GlobalSearchScope.allScope(project);
    PsiFileSystemItem[] items = FilenameIndex.getFilesByName(project, "views", scope, true);
    return Arrays.stream(items).filter(PsiFileSystemItem::isDirectory).collect(Collectors.toList());
  }

  /**
   * @param element an element
   * @param argIndex method parameter index (0-based)
   * @return {@code true} if {@code element} is an argument of a {@code load->view()} call
   */
  static boolean isArgumentOfLoadView(PsiElement element, int argIndex) {
    return MyPsiUtil.isArgumentOfMethod(element, "load", "view", argIndex);
  }

}
