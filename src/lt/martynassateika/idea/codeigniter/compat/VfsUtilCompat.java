/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lt.martynassateika.idea.codeigniter.compat;

import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.openapi.vfs.VfsUtilCore.getCommonAncestor;
import static com.intellij.openapi.vfs.VfsUtilCore.getRelativePath;

/**
 * Based on {@link com.intellij.openapi.vfs.VfsUtil}.
 *
 * This class exists here to enable 2016.1 support.
 *
 * @since 0.4.1
 */
public class VfsUtilCompat {

  /**
   * Returns the relative path from one virtual file to another. If {@code src} is a file, the path
   * is calculated from its parent directory.
   *
   * @param src the file or directory, from which the path is built
   * @param dst the file or directory, to which the path is built
   * @param separatorChar the separator for the path components
   * @return the relative path, or {@code null} if the files have no common ancestor
   * @author JetBrains
   * @since 0.4.1 (intellij-community: 2018.1)
   */
  @Nullable
  public static String findRelativePath(@NotNull VirtualFile src, @NotNull VirtualFile dst,
      char separatorChar) {
    if (!src.getFileSystem().equals(dst.getFileSystem())) {
      return null;
    }

    if (!src.isDirectory()) {
      src = src.getParent();
      if (src == null) {
        return null;
      }
    }

    VirtualFile commonAncestor = getCommonAncestor(src, dst);
    if (commonAncestor == null) {
      return null;
    }

    StringBuilder buffer = new StringBuilder();

    if (!Comparing.equal(src, commonAncestor)) {
      while (!Comparing.equal(src, commonAncestor)) {
        buffer.append("..").append(separatorChar);
        src = src.getParent();
      }
    }

    buffer.append(getRelativePath(dst, commonAncestor, separatorChar));

    if (StringUtil.endsWithChar(buffer, separatorChar)) {
      buffer.setLength(buffer.length() - 1);
    }

    return buffer.toString();
  }

}
