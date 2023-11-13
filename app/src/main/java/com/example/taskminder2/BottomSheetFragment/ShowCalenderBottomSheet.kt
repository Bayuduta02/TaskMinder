package com.example.taskminder2.BottomSheetFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.taskminder2.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ShowCalenderBottomSheet : BottomSheetDialogFragment() {
    private lateinit var back: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calender_view, container, false)
        back = view.findViewById(R.id.back)
        back.setOnClickListener { dialog?.dismiss() }
        return view
    }
}