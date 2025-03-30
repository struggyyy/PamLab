package pl.wsei.pam.lab01.lab06.data.repository

import kotlinx.coroutines.flow.Flow
import pl.wsei.pam.lab01.lab06.TodoTask

interface TodoTaskRepository {
    fun getAllAsStream(): Flow<List<TodoTask>>
    fun getItemAsStream(id: Int): Flow<TodoTask?>
    suspend fun insertItem(item: TodoTask)
    suspend fun deleteItem(item: TodoTask)
    suspend fun updateItem(item: TodoTask)
}