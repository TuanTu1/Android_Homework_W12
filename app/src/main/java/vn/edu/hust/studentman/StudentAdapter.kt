package vn.edu.hust.studentman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(val students: MutableList<StudentModel>): RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

  class StudentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val textStudentName: TextView = itemView.findViewById(R.id.text_student_name)
    val textStudentId: TextView = itemView.findViewById(R.id.text_student_id)
    val imageEdit: ImageView = itemView.findViewById(R.id.image_edit)
    val imageRemove: ImageView = itemView.findViewById(R.id.image_remove)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
    val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_student_item, parent, false)
    return StudentViewHolder(itemView)
  }

  override fun getItemCount(): Int = students.size

  override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
    val student = students[position]

    holder.textStudentName.text = student.studentName
    holder.textStudentId.text = student.studentId

    holder.imageEdit.setOnClickListener {
      val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_add_student, null)
      val inputName = dialogView.findViewById<EditText>(R.id.input_student_name)
      val inputId = dialogView.findViewById<EditText>(R.id.input_student_id)

      inputName.setText(student.studentName)
      inputId.setText(student.studentId)

      AlertDialog.Builder(holder.itemView.context)
        .setTitle("Edit Student")
        .setView(dialogView)
        .setPositiveButton("Save") { _, _ ->
          val updatedName = inputName.text.toString().trim()
          val updatedId = inputId.text.toString().trim()
          if (updatedName.isNotEmpty() && updatedId.isNotEmpty()) {
            students[position] = StudentModel(updatedName, updatedId)
            notifyItemChanged(position)
          }
        }
        .setNegativeButton("Cancel", null)
        .show()
    }

    holder.imageRemove.setOnClickListener {
      AlertDialog.Builder(holder.itemView.context)
        .setTitle("Delete Student")
        .setMessage("Are you sure you want to delete ${student.studentName}?")
        .setPositiveButton("Delete") { _, _ ->
          val deletedStudent = students[position]
          (holder.itemView.context as MainActivity).apply {
            students.removeAt(position)
            studentAdapter.notifyItemRemoved(position)
            deletedStudents.add(deletedStudent)
            showUndoSnackbar(deletedStudent, position)
          }
        }
        .setNegativeButton("Cancel", null)
        .show()
    }
  }
}
