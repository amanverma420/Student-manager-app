package com.chotu.studentmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chotu.studentmanager.data.database.DatabaseProvider
import com.chotu.studentmanager.data.entity.StudentEntity
import com.chotu.studentmanager.repository.StudentRepository
import com.chotu.studentmanager.ui.theme.StudentManagerTheme
import com.chotu.studentmanager.viewmodel.StudentViewModel
import com.chotu.studentmanager.viewmodel.StudentViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentManagerTheme {
                StudentScreen()
            }
        }
    }
}

@Composable
fun StudentScreen(modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }

    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val repository = remember { StudentRepository(database.studentDao()) }
    val factory = remember { StudentViewModelFactory(repository) }
    val viewModel: StudentViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) { viewModel.loadStudents() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF0A0F1E), Color(0xFF0D1B2A), Color(0xFF111827)),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Header ──────────────────────────────────────
            Column {
                Text(
                    "Student Manager",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF0F6FF),
                    letterSpacing = (-0.5).sp
                )
                Text(
                    "Manage your student records",
                    fontSize = 12.sp,
                    color = Color(0x4DFFFFFF)
                )
            }

            // ── Form Card ───────────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x0DFFFFFF)),
                border = BorderStroke(1.dp, Color(0x17FFFFFF)),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        if (viewModel.selectedStudent == null) "ADD NEW STUDENT" else "EDIT STUDENT",
                        fontSize = 11.sp,
                        color = Color(0x4DFFFFFF),
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(12.dp))

                    // Name field
                    StudentField(
                        label = "Student name",
                        value = name,
                        placeholder = "Name"
                    ) { name = it }
                    Spacer(Modifier.height(10.dp))

                    // Course field
                    StudentField(
                        label = "Course",
                        value = course,
                        placeholder = "Course"
                    ) { course = it }
                    Spacer(Modifier.height(10.dp))

                    // Semester field
                    StudentField(
                        label = "Semester",
                        value = semester,
                        placeholder = "Semester",
                        keyboardType = KeyboardType.Number
                    ) { semester = it }

                    Spacer(Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Add / Update button
                        Button(
                            onClick = {
                                val semValue = semester.toIntOrNull()
                                if (name.isNotBlank() && course.isNotBlank() && semValue != null) {
                                    if (viewModel.selectedStudent == null) {
                                        viewModel.insertStudent(name, course, semValue)
                                    } else {
                                        viewModel.updateStudent(
                                            viewModel.selectedStudent!!.copy(
                                                name = name,
                                                course = course,
                                                semester = semValue
                                            )
                                        )
                                    }
                                    name = ""; course = ""; semester = ""
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            contentPadding = PaddingValues(vertical = 11.dp)
                        ) {
                            Icon(
                                if (viewModel.selectedStudent == null) Icons.Default.PersonAdd else Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                if (viewModel.selectedStudent == null) "Add Student" else "Update Student",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Cancel button (only in edit mode)
                        if (viewModel.selectedStudent != null) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.selectedStudent(null)
                                    name = ""; course = ""; semester = ""
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(
                                        0x80FFFFFF
                                    )
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 11.dp)
                            ) {
                                Text("Cancel", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // ── Stats Row ──────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val courses = viewModel.students.map { it.course }.toSet().size

                listOf(
                    "Total Students" to viewModel.students.size,
                    "Courses" to courses
                ).forEach { (label, count) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x0AFFFFFF)),
                        border = BorderStroke(1.dp, Color(0x12FFFFFF)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "$count",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA5B4FC)
                            )
                            Text(
                                label.uppercase(),
                                fontSize = 10.sp,
                                color = Color(0x4DFFFFFF),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            // ── Student List ───────────────────────────────
            Text("ALL STUDENTS", fontSize = 11.sp, color = Color(0x47FFFFFF), letterSpacing = 1.sp)

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(viewModel.students.size) { index ->
                    val student = viewModel.students[index]
                    StudentCard(
                        student = student,
                        onEdit = {
                            viewModel.selectedStudent(student)
                            name = student.name
                            course = student.course
                            semester = student.semester.toString()
                        },
                        onDelete = { viewModel.deleteStudent(student) }
                    )
                }
            }
        }
    }
}

// ── StudentField ────────────────────────────────────────────────
@Composable
fun StudentField(
    label: String,
    value: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(label, fontSize = 11.sp, color = Color(0x66FFFFFF), letterSpacing = 0.3.sp)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0x33FFFFFF), fontSize = 13.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0x996366F1),
                unfocusedBorderColor = Color(0x1AFFFFFF),
                focusedContainerColor = Color(0x146366F1),
                unfocusedContainerColor = Color(0x0DFFFFFF),
                focusedTextColor = Color(0xFFE2EAFF),
                unfocusedTextColor = Color(0xFFE2EAFF),
                cursorColor = Color(0xFF6366F1)
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
        )
    }
}

// ── StudentCard ─────────────────────────────────────────────────
@Composable
fun StudentCard(
    student: StudentEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // Avatar initials
    val initials = student.name
        .split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x0AFFFFFF)),
        border = BorderStroke(1.dp, Color(0x12FFFFFF)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x336366F1))
                    .border(1.dp, Color(0x4D6366F1), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    initials,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA5B4FC)
                )
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    student.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE2EAFF)
                )
                Text(student.course, fontSize = 12.sp, color = Color(0x59FFFFFF))
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x266366F1))
                        .border(1.dp, Color(0x406366F1), RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        "Semester ${student.semester}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFA5B4FC)
                    )
                }
            }

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x1F6366F1))
                        .clickable { onEdit() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFFA5B4FC),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x1FEF4444))
                        .clickable { onDelete() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFF87171),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StudentManagerTheme {
        StudentScreen()
    }
}
