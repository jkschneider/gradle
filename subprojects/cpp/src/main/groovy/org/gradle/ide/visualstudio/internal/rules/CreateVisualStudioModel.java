/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.ide.visualstudio.internal.rules;

import org.gradle.ide.visualstudio.internal.DefaultVisualStudioExtension;
import org.gradle.ide.visualstudio.internal.DefaultVisualStudioProject;
import org.gradle.ide.visualstudio.internal.VisualStudioProjectConfiguration;
import org.gradle.language.base.BinaryContainer;
import org.gradle.model.ModelRule;
import org.gradle.nativebinaries.NativeBinary;
import org.gradle.nativebinaries.ProjectNativeBinary;
import org.gradle.nativebinaries.ProjectNativeComponent;

public class CreateVisualStudioModel extends ModelRule {
    @SuppressWarnings("UnusedDeclaration")
    public void createVisualStudioModelForBinaries(DefaultVisualStudioExtension visualStudioExtension, BinaryContainer binaryContainer) {
        for (ProjectNativeBinary binary : binaryContainer.withType(ProjectNativeBinary.class)) {
            VisualStudioProjectConfiguration configuration = visualStudioExtension.getProjectRegistry().addProjectConfiguration(binary);

            if (isDevelopmentBinary(binary)) {
                DefaultVisualStudioProject visualStudioProject = configuration.getProject();
                visualStudioExtension.getSolutionRegistry().addSolution(visualStudioProject);
            }
        }
    }

    private boolean isDevelopmentBinary(ProjectNativeBinary binary) {
        return binary == chooseDevelopmentVariant(binary.getComponent());
    }

    private NativeBinary chooseDevelopmentVariant(ProjectNativeComponent component) {
        for (ProjectNativeBinary candidate : component.getBinaries().withType(ProjectNativeBinary.class)) {
            if (candidate.isBuildable()) {
                return candidate;
            }
        }
        return null;
    }
}

