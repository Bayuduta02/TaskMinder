package com.example.taskminder2

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.taskminder2.BottomSheetFragment.AddTaskBottomSheetFragment
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat

import java.util.ArrayList
import java.util.Locale

class AdapterTask(
    private val context: Context,
    private val taskList: MutableList<Task> = ArrayList()
) : RecyclerView.Adapter<AdapterTask.ViewHolder>() {
    private val database = FirebaseDatabase.getInstance()
    private val taskRef = database.getReference("task")

    private val inputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("EEE dd MMM", Locale.getDefault())

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.day)
        val dateTextView: TextView = itemView.findViewById(R.id.date)
        val monthTextView: TextView = itemView.findViewById(R.id.month)
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description)
        val timeTextView: TextView = itemView.findViewById(R.id.time)
        val statusTextView: TextView = itemView.findViewById(R.id.status)
        val optionsImageView: ImageView = itemView.findViewById(R.id.options)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return ViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]

        holder.titleTextView.text = task.title
        holder.descriptionTextView.text = task.description
        holder.timeTextView.text = task.time
        holder.statusTextView.text = if (task.isComplete) "COMPLETED" else "UPCOMING"

        try {
            val date = inputDateFormat.parse(task.date)
            val outputDateString = date?.let { dateFormat.format(it) }


            val items1 = outputDateString?.split(" ")
            val day = items1?.get(0)
            val dd = items1?.get(1)
            val month = items1?.get(2)

            holder.dayTextView.text = day
            holder.dateTextView.text = dd
            holder.monthTextView.text = month
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.optionsImageView.setOnClickListener { view ->
            showPopupMenu(view, position)
        }
    }

     private fun showPopupMenu(view: View, position: Int) {
        val imageView = view as ImageView
        val popupmenu = PopupMenu(context, imageView)
        val clickedTask = taskList[position]
        popupmenu.menuInflater.inflate(R.menu.menu, popupmenu.menu)

        popupmenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuDelete -> {
                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setTitle("Konfirmasi Hapus")
                    alertDialog.setMessage("Anda Yakin Ingin Menghapus Tugas Ini?")
                    alertDialog.setPositiveButton("Yes") { dialog, which ->
                        // Hapus tugas dari Firebase Database
                        val clickedTask = taskList[position]
                        val taskKeyToDelete = clickedTask.id

                        taskRef.child(taskKeyToDelete).removeValue()
                        taskList.removeAt(position)
                        notifyItemRemoved(position)
                        dialog.dismiss()
                    }
                    alertDialog.setNegativeButton("No") { dialog, which ->
                        // Batal menghapus, tutup dialog
                        dialog.dismiss()
                    }
                    alertDialog.show()
                    true
                }
                R.id.menuUpdate -> {
                    if (!clickedTask.isComplete) {
                        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                        val updateFragment = AddTaskBottomSheetFragment.newInstance(clickedTask)
                        updateFragment.show(fragmentManager, updateFragment.tag)
                        true
                    } else {
                        val alertDialog = AlertDialog.Builder(context)
                        alertDialog.setTitle("Data Sudah Complete")
                        alertDialog.setMessage("Tugas Ini Sudah Selesai Dan Tidak Dapat Diupdate Ulang.")
                        alertDialog.setPositiveButton("Ok") { dialog, which ->
                            dialog.dismiss()
                        }
                        alertDialog.show()
                        true
                    }
                }

                R.id.menuComplete -> {
                    if (!clickedTask.isComplete) {
                        val alertDialog = AlertDialog.Builder(context)
                        alertDialog.setTitle("Konfirmasi Complete")
                        alertDialog.setMessage("Anda Yakin Ingin Menandai Tugas Ini Sebagai Selesai?")
                        alertDialog.setPositiveButton("Yes") { dialog, which ->
                            // Ubah status tugas menjadi "Complete" di Firebase Database
                            val taskKeyToUpdate = clickedTask.id
                            taskRef.child(taskKeyToUpdate).child("complete").setValue(true)

                            // Kemudian simpan perubahan ke Firebase Database
                            clickedTask.isComplete = true
                            notifyItemChanged(position)
                        }
                        alertDialog.setNegativeButton("No") { dialog, which ->
                            // Batal menandai sebagai selesai, tutup dialog
                            dialog.dismiss()
                        }
                        alertDialog.show()
                        true
                    } else {
                        val alertDialog = AlertDialog.Builder(context)
                        alertDialog.setTitle("Data Sudah Complete")
                        alertDialog.setMessage("Tugas Ini Sudah Selesai Dan Tidak Dapat Ditandai Ulang.")
                        alertDialog.setPositiveButton("Ok") { dialog, which ->
                            dialog.dismiss()
                        }
                        alertDialog.show()
                        true
                    }
                }
                else -> false
            }
        }
        popupmenu.show()
    }
    
    override fun getItemCount(): Int {
        return taskList.size
    }

}