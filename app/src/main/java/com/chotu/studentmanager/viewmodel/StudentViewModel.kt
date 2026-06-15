package com.chotu.studentmanager.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chotu.studentmanager.data.entity.StudentEntity
import com.chotu.studentmanager.repository.StudentRepository
import kotlinx.coroutines.launch

class StudentViewModel(
    private val repository: StudentRepository
) : ViewModel() {
    var students by mutableStateOf<List<StudentEntity>>(emptyList())
        private set

    var selectedStudent by mutableStateOf<StudentEntity?>(null)
        private set

    fun loadStudents() {
        viewModelScope.launch {
            students = repository.getAllStudents()
        }
    }

    fun insertStudent(
        name: String,
        course: String,
        semester: Int
    ) {
        viewModelScope.launch {
            repository.insertStudent(
                StudentEntity(
                    name = name,
                    course = course,
                    semester = semester
                )
            )
            loadStudents()
        }
    }

    fun deleteStudent(
        student: StudentEntity
    ) {
        viewModelScope.launch {
            repository.deleteStudent(student)
            loadStudents()
        }
    }

    fun selectedStudent(student: StudentEntity?) {
        selectedStudent = student
    }

    fun updateStudent(
        student: StudentEntity
    ) {
        viewModelScope.launch {
            repository.updateStudent(student)
            selectedStudent = null
            loadStudents()
        }
    }
}