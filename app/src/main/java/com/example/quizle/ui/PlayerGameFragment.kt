package com.example.quizle.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizle.R
import com.example.quizle.logic.GameState
import com.example.quizle.network.PlayerHttpClient
import kotlin.concurrent.thread

class PlayerGameFragment : Fragment() {

    private var handler = Handler(Looper.getMainLooper())
    private var client: PlayerHttpClient? = null
    private var currentQuestionId: String? = null
    private var hasAnswered = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_player_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val baseUrl = arguments?.getString("baseUrl") ?: return
        client = PlayerHttpClient(serverUrl = baseUrl)
        client?.playerId = arguments?.getString("playerId")
        
        startPolling()
    }

    private fun startPolling() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                pollState()
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun pollState() {
        thread {
            val result = client?.pollGameState()
            activity?.runOnUiThread {
                if (result?.isSuccess == true) {
                    val stateDto = result.getOrThrow()
                    updateUi(stateDto.state, stateDto.currentQuestion?.questionText, stateDto.currentQuestion?.getAnswers()?.map { it.answerId to it.answerText }, stateDto.currentQuestion?.questionId)
                    
                    if (stateDto.state == GameState.FINISHED.name) {
                        handler.removeCallbacksAndMessages(null)
                        findNavController().navigate(R.id.action_playerGame_to_leaderboard)
                    }
                }
            }
        }
    }

    private fun updateUi(state: String, qText: String?, answers: List<Pair<String, String>>?, qId: String?) {
        val tvQuestion = view?.findViewById<TextView>(R.id.tvQuestionText)
        val tvStatus = view?.findViewById<TextView>(R.id.tvStatus)
        
        tvStatus?.text = "State: $state"
        
        if (qId != currentQuestionId) {
            currentQuestionId = qId
            hasAnswered = false
            enableButtons(true)
        }

        if (qText != null && tvQuestion?.text != qText) {
            tvQuestion?.text = qText
            val buttons = listOf(R.id.btnAnswer1, R.id.btnAnswer2, R.id.btnAnswer3, R.id.btnAnswer4)
            buttons.forEach { view?.findViewById<Button>(it)?.visibility = View.GONE }
            
            answers?.forEachIndexed { index, pair ->
                if (index < buttons.size) {
                    val btn = view?.findViewById<Button>(buttons[index])
                    btn?.visibility = View.VISIBLE
                    btn?.text = pair.second
                    btn?.setOnClickListener {
                        submitAnswer(pair.first)
                    }
                }
            }
        } else if (qText == null) {
            tvQuestion?.text = "Waiting for next question..."
            listOf(R.id.btnAnswer1, R.id.btnAnswer2, R.id.btnAnswer3, R.id.btnAnswer4).forEach {
                view?.findViewById<Button>(it)?.visibility = View.GONE
            }
        }
    }

    private fun submitAnswer(answerId: String) {
        if (hasAnswered) return
        hasAnswered = true
        enableButtons(false)
        
        val qId = currentQuestionId ?: return
        thread {
            val result = client?.submitAnswer(qId, answerId)
            activity?.runOnUiThread {
                if (result?.isSuccess == true) {
                    Toast.makeText(context, "Answer submitted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to submit: ${result?.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    hasAnswered = false
                    enableButtons(true)
                }
            }
        }
    }

    private fun enableButtons(enabled: Boolean) {
        val buttons = listOf(R.id.btnAnswer1, R.id.btnAnswer2, R.id.btnAnswer3, R.id.btnAnswer4)
        buttons.forEach { view?.findViewById<Button>(it)?.isEnabled = enabled }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}
