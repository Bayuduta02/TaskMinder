package com.example.taskminder2.BottomSheetFragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.taskminder2.FirebaseMessagingService.MyNotificationReceiver
import com.example.taskminder2.R
import com.example.taskminder2.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskBottomSheetFragment : BottomSheetDialogFragment() {
    private val database = FirebaseDatabase.getInstance()
    private val taskRef = database.getReference("task")
    private var isUpdate: Boolean = false
    private var taskToUpdate: Task? = null

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_task, container, false)
        val addTask = view?.findViewById<Button>(R.id.buttonAddTask)
        val editTextDate = view?.findViewById<EditText>(R.id.TextDate)

        editTextDate?.setOnClickListener {
            showDatePickerDialog(editTextDate)
        }
        val editTextTime = view?.findViewById<EditText>(R.id.TextTime)
        editTextTime?.setOnClickListener {
            showTimePickerDialog(editTextTime)
        }
        val titleEditText = view?.findViewById<EditText>(R.id.editTextTitle)
        val descriptionEditText = view?.findViewById<EditText>(R.id.editTextDescription)
        val eventEditText = view?.findViewById<EditText>(R.id.editTextEvent)

        if (isUpdate) {
            titleEditText?.setText(taskToUpdate?.title)
            descriptionEditText?.setText(taskToUpdate?.description)
            editTextDate?.setText(taskToUpdate?.date)
            editTextTime?.setText(taskToUpdate?.time)
            eventEditText?.setText(taskToUpdate?.event)
            addTask?.text = "Update"
        }
        addTask?.setOnClickListener {
            val taskTitle = titleEditText?.text.toString()
            val description = descriptionEditText?.text.toString()
            val date = editTextDate?.text.toString()
            val time = editTextTime?.text.toString()
            val event = eventEditText?.text.toString()

            if (taskTitle.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty() || event.isEmpty()) {
                if (taskTitle.isEmpty()) {
                    titleEditText?.error = "Maaf Anda Belum Mengisi Title"
                }
                if (description.isEmpty()) {
                    descriptionEditText?.error = "Maaf Anda Belum Mengisi Deskripsi"
                }
                if (date.isEmpty()) {
                    editTextDate?.error = "Maaf Anda Belum Mengisi Tanggal"
                }
                if (time.isEmpty()) {
                    editTextTime?.error = "Maaf Anda Belum Mengisi Waktu"
                }
                if (event.isEmpty()) {
                    eventEditText?.error = "Maaf Anda Belum Mengisi Event"
                }
                return@setOnClickListener
            }

            if (isUpdate && taskToUpdate != null) {
                // Lakukan pembaruan data ke Firebase sesuai taskToUpdate.id
                val taskKey = taskToUpdate!!.id
                taskToUpdate!!.isComplete = false
                taskRef.child(taskKey).setValue(
                    Task(taskTitle, description, date, time, event, id = taskKey)
                )
            } else {
                // Lakukan pembuatan data baru jika bukan mode pembaruan
                val taskKey = taskRef.push().key
                if (taskKey != null) {
                    val task = Task(taskTitle, description, date, time,event, id = taskKey )
                    taskRef.child(taskKey).setValue(task)
                }
                scheduleNotification(date, time, taskTitle, taskKey!!)
            }
            dismiss()
        }

        return view
    }

    @SuppressLint("RestrictedApi")
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)

                // Set teks pada EditText tanggal ke tanggal yang dipilih
                editText.setText(formattedDate)
                editText.clearFocus()
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    @SuppressLint("RestrictedApi")
    private fun showTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedTime.set(Calendar.MINUTE, selectedMinute)

                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime = timeFormat.format(selectedTime.time)

                // Set teks pada EditText waktu ke waktu yang dipilih
                editText.setText(formattedTime)
                editText.clearFocus()
            },
            hour, minute, true
        )

        timePickerDialog.show()
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun scheduleNotification(date: String, time: String, title: String, taskId: String) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateTime = "$date $time"
        val parsedDate = dateFormat.parse(dateTime)

        if (parsedDate != null) {
            calendar.time = parsedDate
        }

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(requireContext(), MyNotificationReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("taskId", taskId)

        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT)

        alarmManager.set(AlarmManager.RTC, calendar.timeInMillis, pendingIntent)
    }
    companion object {
        fun newInstance(task: Task? = null): AddTaskBottomSheetFragment {
            val fragment = AddTaskBottomSheetFragment()
            if (task != null) {
                fragment.isUpdate = true
                fragment.taskToUpdate = task
            }
            return fragment
        }
    }

}