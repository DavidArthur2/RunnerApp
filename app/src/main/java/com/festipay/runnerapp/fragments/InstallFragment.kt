package com.festipay.runnerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.CurrentState
import com.festipay.runnerapp.data.Mode
import com.google.android.material.bottomnavigation.BottomNavigationView

class InstallFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_install, container, false)

        CurrentState.mode = Mode.INSTALL

        requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .isVisible = true

        val appBar: Toolbar = requireActivity().findViewById(R.id.toolbar)

        appBar.title = "${CurrentState.programName} - ${getString(R.string.install_title)}"
        return view
    }

}