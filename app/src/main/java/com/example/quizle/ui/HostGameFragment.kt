package com.example.quizle.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizle.R

class HostGameFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_host_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mainActivity = activity as? MainActivity
        val session = mainActivity?.currentSession ?: return
        val server = mainActivity.server ?: return

        val tvQuestion = view.findViewById<TextView>(R.id.tvHostQuestionText)
        val tvPlayers = view.findViewById<TextView>(R.id.tvPlayerCount)
        val btnNext = view.findViewById<Button>(R.id.btnNextQuestion)

        fun updateUi() {
            val currentQ = session.getCurrentQuestion()
            if (currentQ != null) {
                tvQuestion.text = currentQ.questionText
                tvPlayers.text = "Players connected: ${session.getPlayers().size}"
            } else if (session.isFinished()) {
                findNavController().navigate(R.id.action_hostGame_to_leaderboard)
            } else {
                tvQuestion.text = "Ready to start?"
            }
        }

        updateUi()

        btnNext.setOnClickListener {
            val nextQ = session.advance()
            if (nextQ != null) {
                server.broadcastQuestion(nextQ)
                updateUi()
            } else {
                // Game finished, results logic would go here
                // Broadcast leaderboard etc.
                findNavController().navigate(R.id.action_hostGame_to_leaderboard)
            }
        }
    }
}
