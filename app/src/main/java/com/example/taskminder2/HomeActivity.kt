package com.example.taskminder2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskminder2.BottomSheetFragment.AddTaskBottomSheetFragment
import com.example.taskminder2.BottomSheetFragment.ShowCalenderBottomSheet
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.ArrayList

class HomeActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val taskRef = database.getReference("task")
    private lateinit var taskAdapter: AdapterTask
    private lateinit var taskRecycler: RecyclerView

    @RequiresApi(34)
    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        FirebaseApp.initializeApp(this)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM Token", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Now you have the FCM token
                Log.d("FCM Token", "Token: $token")
            }

        val calendarImageView = findViewById<ImageView>(R.id.calendar)
        val newTask: TextView = findViewById(R.id.newTask)

        newTask.setOnClickListener {
            val bottomSheetDialogFragment = AddTaskBottomSheetFragment()
            bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
        }

        calendarImageView.setOnClickListener {
            val bottomSheetDialogFragment = ShowCalenderBottomSheet()
            bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
        }
        taskRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tasksList = ArrayList<Task>()

                for (taskSnapshot in dataSnapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    if (task != null) {
                        tasksList.add(task)
                    }
                }
                Log.d("Firebase Data", "Jumlah data: ${tasksList.size}") // Tambahkan log ini
                taskRecycler = findViewById(R.id.taskRecycler) // Ganti dengan ID RecyclerView Anda
                taskRecycler.layoutManager = LinearLayoutManager(applicationContext)
                taskAdapter = AdapterTask(this@HomeActivity, tasksList)
                taskRecycler.adapter = taskAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle kesalahan jika terjadi
            }
        })
    }

}