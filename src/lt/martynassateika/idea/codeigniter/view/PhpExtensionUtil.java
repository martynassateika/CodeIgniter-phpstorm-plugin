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

import com.intellij.util.PathUtil;
import com.jetbrains.php.refactoring.PhpNameUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Tiny utility class for addition / removal of PHP extensions.
 *
 * @author martynas.sateika
 * @since 0.2.0
 */
class PhpExtensionUtil {

  /**
   * @param path a path
   * @return the path with a PHP extension appended, if not present
   */
  static String addIfMissing(@NotNull String path) {
    if (PathUtil.getFileExtension(path) == null) {
      return path + ".php";
    } else {
      return path;
    }
  }

  /**
   * @param path a path
   * @return the path with the PHP extension removed, if present
   */
  static String removeIfPresent(@NotNull String path) {
    if ("php".equals(PathUtil.getFileExtension(path))) {
      return PhpNameUtil.getNameWithoutExtension(path);
    }
    return path;
  }

}
