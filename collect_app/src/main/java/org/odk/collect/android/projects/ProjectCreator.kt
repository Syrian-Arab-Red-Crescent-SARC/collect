package org.odk.collect.android.projects

import org.odk.collect.android.configure.SettingsImporter
import org.odk.collect.android.storage.StoragePathProvider
import org.odk.collect.projects.ProjectsRepository
import java.io.File

class ProjectCreator(
    private val projectImporter: ProjectImporter,
    private val projectsRepository: ProjectsRepository,
    private val currentProjectProvider: CurrentProjectProvider,
    private val settingsImporter: SettingsImporter,
    private val storagePathProvider: StoragePathProvider
) {

    fun createNewProject(settingsJson: String): Boolean {
        val savedProject = projectImporter.importNewProject()

        val settingsImportedSuccessfully = settingsImporter.fromJSON(settingsJson, savedProject)

        return if (settingsImportedSuccessfully) {
            currentProjectProvider.setCurrentProject(savedProject.uuid)
            File((storagePathProvider.getProjectRootDirPath() + File.separator + currentProjectProvider.getCurrentProject().name)).createNewFile()
            true
        } else {
            projectsRepository.delete(savedProject.uuid)
            false
        }
    }
}
