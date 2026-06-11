package com.example.quizle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class JoinFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_join, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etUrl = view.findViewById<EditText>(R.id.etJoinUrl)
        view.findViewById<Button>(R.id.btnJoin).setOnClickListener {
            val url = etUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                val bundle = Bundle().apply { putString("hostUrl", url) }
                findNavController().navigate(R.id.action_join_to_username, bundle)
            }
        }
    }
}
