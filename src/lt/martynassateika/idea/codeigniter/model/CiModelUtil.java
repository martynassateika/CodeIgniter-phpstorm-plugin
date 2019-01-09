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
import lt.martynassateika.idea.codeigniter.PhpExtensionUtil;
import lt.martynassateika.idea.codeigniter.psi.MyPsiUtil;

/**
 * Utility methods related to the CI Models.
 *
 * @author martynas.sateika
 * @since 0.4.0
 */
public class CiModelUtil {

  /**
   * Returns a list of model files whose relative path from a model directory equals the supplied
   * relative path.
   *
   * @param relativePath relative path to a model file
   * @param project current project
   * @return list of all matching model files
   */
  static List<PsiFile> findModelFiles(String relativePath, Project project) {
    PsiManager psiManager = PsiManager.getInstance(project);

    // If no extension is specified, it's a PHP file
    relativePath = PhpExtensionUtil.addIfMissing(relativePath);

    List<PsiFile> modelFiles = new ArrayList<>();
    for (PsiFileSystemItem fileSystemItem : getModelDirectories(project)) {
      VirtualFile modelDirectory = fileSystemItem.getVirtualFile();
      VirtualFile modelFile = modelDirectory.findFileByRelativePath(relativePath);
      if (modelFile != null && !modelFile.isDirectory()) {
        PsiFile psiFile = psiManager.findFile(modelFile);
        if (psiFile != null) {
          modelFiles.add(psiFile);
        }
      }
    }
    return modelFiles;
  }

  /**
   * @param project current project
   * @return all directories called 'models' in the project
   */
  static List<PsiFileSystemItem> getModelDirectories(Project project) {
    GlobalSearchScope scope = GlobalSearchScope.allScope(project);
    PsiFileSystemItem[] items = FilenameIndex.getFilesByName(project, "models", scope, true);
    return Arrays.stream(items).filter(PsiFileSystemItem::isDirectory).collect(Collectors.toList());
  }

  /**
   * @param element an element
   * @param argIndex method parameter index (0-based)
   * @return {@code true} if {@code element} is an argument of a {@code load->model()} call
   */
  static boolean isArgumentOfLoadModel(PsiElement element, int argIndex) {
    return MyPsiUtil.isArgumentOfMethod(element, "load", "model", argIndex);
  }

}
