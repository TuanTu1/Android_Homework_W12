package vn.edu.hust.studentman

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
  private val students = mutableListOf(
    StudentModel("Nguyễn Văn An", "SV001"),
    StudentModel("Trần Thị Bảo", "SV002"),
    StudentModel("Lê Hoàng Cường", "SV003"),
    StudentModel("Phạm Thị Dung", "SV004"),
    StudentModel("Đỗ Minh Đức", "SV005"),
    StudentModel("Vũ Thị Hoa", "SV006")
  )

  private lateinit var adapter: ArrayAdapter<String>
  private lateinit var studentNames: MutableList<String>
  private lateinit var listView: ListView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    studentNames = students.map { it.studentName }.toMutableList()
    adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, studentNames)

    listView = findViewById<ListView>(R.id.list_view_students).apply {
      adapter = this@MainActivity.adapter
      registerForContextMenu(this) // Enable context menu
    }

    findViewById<Button>(R.id.btn_add_new).setOnClickListener {
      openAddStudentActivity()
    }
  }

  // Inflate the option menu
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)
    return true
  }

  // Handle menu item selection
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_add_new -> {
        openAddStudentActivity()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  // Open Add/Edit Student Activity
  private fun openAddStudentActivity(existingStudent: StudentModel? = null, position: Int = -1) {
    val dialogView = layoutInflater.inflate(R.layout.dialog_add_student, null)
    val inputName = dialogView.findViewById<EditText>(R.id.input_student_name)
    val inputId = dialogView.findViewById<EditText>(R.id.input_student_id)

    // Pre-fill data if editing
    existingStudent?.let {
      inputName.setText(it.studentName)
      inputId.setText(it.studentId)
    }

    val title = if (existingStudent == null) "Add New Student" else "Edit Student"

    AlertDialog.Builder(this)
      .setTitle(title)
      .setView(dialogView)
      .setPositiveButton("Save") { _, _ ->
        val name = inputName.text.toString().trim()
        val id = inputId.text.toString().trim()

        if (name.isNotEmpty() && id.isNotEmpty()) {
          if (existingStudent == null) {
            // Adding new student
            val newStudent = StudentModel(name, id)
            students.add(newStudent)
            studentNames.add(name)
            adapter.notifyDataSetChanged()
          } else {
            // Editing existing student
            students[position] = StudentModel(name, id)
            studentNames[position] = name
            adapter.notifyDataSetChanged()
          }
        }
      }
      .setNegativeButton("Cancel", null)
      .show()
  }

  // Create the context menu for list items
  override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
    super.onCreateContextMenu(menu, v, menuInfo)
    if (v?.id == R.id.list_view_students) {
      val inflater: MenuInflater = menuInflater
      inflater.inflate(R.menu.context_menu, menu)
    }
  }

  // Handle context menu item selection
  override fun onContextItemSelected(item: MenuItem): Boolean {
    val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
    val position = info.position
    val selectedStudent = students[position]

    return when (item.itemId) {
      R.id.menu_edit -> {
        openAddStudentActivity(selectedStudent, position)
        true
      }
      R.id.menu_remove -> {
        removeStudent(position)
        true
      }
      else -> super.onContextItemSelected(item)
    }
  }

  // Remove a student from the list
  private fun removeStudent(position: Int) {
    val removedStudent = students.removeAt(position)
    studentNames.removeAt(position)
    adapter.notifyDataSetChanged()

    Snackbar.make(findViewById(R.id.main), "${removedStudent.studentName} removed", Snackbar.LENGTH_LONG)
      .setAction("Undo") {
        students.add(position, removedStudent)
        studentNames.add(position, removedStudent.studentName)
        adapter.notifyDataSetChanged()
      }
      .show()
  }
}
