package com.example.quizle.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizle.R

class HostGameFragment : Fragment() {

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateUi()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_host_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler.post(updateRunnable)

        val mainActivity = activity as? MainActivity
        val session = mainActivity?.currentSession ?: return
        val btnNext = view.findViewById<Button>(R.id.btnNextQuestion)

        btnNext.setOnClickListener {
            val server = mainActivity.server ?: return@setOnClickListener
            val nextQ = session.advance()
            if (nextQ != null) {
                server.broadcastQuestion(nextQ)
                updateUi()
            } else {
                findNavController().navigate(R.id.action_hostGame_to_leaderboard)
            }
        }
    }

    private fun updateUi() {
        val view = view ?: return
        val mainActivity = activity as? MainActivity
        val session = mainActivity?.currentSession ?: return

        val tvQuestion = view.findViewById<TextView>(R.id.tvHostQuestionText)
        val tvPlayers = view.findViewById<TextView>(R.id.tvPlayerCount)
        val llAnswers = view.findViewById<LinearLayout>(R.id.llAnswersContainer)

        val currentQ = session.getCurrentQuestion()
        if (currentQ != null) {
            tvQuestion.text = currentQ.questionText
            tvPlayers.text = "Players connected: ${session.getPlayers().size}"
            
            val allAnswers = session.getAnswers().filter { it.questionId == currentQ.questionId }
            
            llAnswers.removeAllViews()
            currentQ.getAnswers().forEach { answer ->
                val voteCount = allAnswers.count { it.answerId == answer.answerId }
                val tv = TextView(requireContext()).apply {
                    text = "${answer.answerText}: $voteCount votes"
                    textSize = 18f
                    setPadding(0, 8, 0, 8)
                }
                llAnswers.addView(tv)
            }
        } else if (session.isFinished()) {
            findNavController().navigate(R.id.action_hostGame_to_leaderboard)
        } else {
            tvQuestion.text = "Ready to start?"
            llAnswers.removeAllViews()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateRunnable)
    }
}
