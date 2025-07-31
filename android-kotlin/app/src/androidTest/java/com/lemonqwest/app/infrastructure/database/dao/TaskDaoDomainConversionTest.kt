package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.infrastructure.database.entities.TaskEntity
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("TaskDaoDomainConversionTest")
class TaskDaoDomainConversionTest : DatabaseTestBase() {
    private lateinit var taskDao: TaskDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        taskDao = database.taskDao()
    }

    @Test
    fun shouldConvertToDomainModel() = runTest {
        val taskEntity = EntityTestFactory.createTaskEntity(
            id = "test-task",
            category = com.lemonqwest.app.domain.task.TaskCategory.PERSONAL_CARE,
            tokenReward = 15,
            isCompleted = false,
            assignedToUserId = "user-1",
        )
        taskDao.insert(taskEntity)
        val retrievedEntity = taskDao.findById("test-task")!!
        val domainTask = retrievedEntity.toDomain()
        assertEquals(taskEntity.id, domainTask.id)
        assertEquals(taskEntity.title, domainTask.title)
        assertEquals(taskEntity.category, domainTask.category.name)
        assertEquals(taskEntity.tokenReward, domainTask.tokenReward)
        assertEquals(taskEntity.isCompleted, domainTask.isCompleted)
        assertEquals(taskEntity.assignedToUserId, domainTask.assignedToUserId)
        assertEquals(taskEntity.createdAt, domainTask.createdAt)
    }

    @Test
    fun shouldConvertFromDomainModel() = runTest {
        val domainTask = com.lemonqwest.app.domain.TestDataFactory.createTask(
            id = "domain-task",
            title = "Domain Task",
            category = com.lemonqwest.app.domain.task.TaskCategory.HOMEWORK,
            assignedToUserId = "user-1",
            isCompleted = true,
        )
        val taskEntity = TaskEntity.fromDomain(domainTask)
        taskDao.insert(taskEntity)
        val retrievedEntity = taskDao.findById("domain-task")
        assertNotNull(retrievedEntity)
        assertEquals(domainTask.id, retrievedEntity.id)
        assertEquals(domainTask.title, retrievedEntity.title)
        assertEquals(domainTask.category.name, retrievedEntity.category)
        assertEquals(domainTask.tokenReward, retrievedEntity.tokenReward)
        assertEquals(domainTask.isCompleted, retrievedEntity.isCompleted)
        assertEquals(domainTask.assignedToUserId, retrievedEntity.assignedToUserId)
        assertEquals(domainTask.createdAt, retrievedEntity.createdAt)
    }
}
