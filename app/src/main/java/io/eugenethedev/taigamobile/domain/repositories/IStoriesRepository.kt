package io.eugenethedev.taigamobile.domain.repositories

import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.Story

interface IStoriesRepository {
    suspend fun getStatuses(projectId: Long, sprintId: Long? = null): List<Status>
    suspend fun getStories(projectId: Long, statusId: Long, page: Int, sprintId: Long? = null): List<Story>
}