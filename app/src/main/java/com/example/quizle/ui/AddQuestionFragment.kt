package com.example.quizle.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizle.R
import com.example.quizle.logic.Answer
import com.example.quizle.logic.Question
import java.util.UUID

class AddQuestionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_add_question, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupSingleSelect(view)


        arguments?.getSerializable("question")?.let { q ->
            q as Question
            view.findViewById<EditText>(R.id.etQuestion).setText(q.questionText)
            val answers = q.getAnswers()
            val fields = listOf(R.id.etAnswer1, R.id.etAnswer2, R.id.etAnswer3, R.id.etAnswer4)
            val radios = listOf(R.id.rbCorrect1, R.id.rbCorrect2, R.id.rbCorrect3, R.id.rbCorrect4)
            answers.forEachIndexed { i, a ->
                view.findViewById<EditText>(fields[i]).setText(a.answerText)
                if (a.isCorrect) view.findViewById<RadioButton>(radios[i]).isChecked = true
            }
        }

        view.findViewById<Button>(R.id.btnCancelQuestion).setOnClickListener {
            findNavController().popBackStack()
        }

        view.findViewById<Button>(R.id.btnSaveQuestion).setOnClickListener {
            val questionText = view.findViewById<EditText>(R.id.etQuestion).text.toString().trim()
            val answerFields = listOf(R.id.etAnswer1, R.id.etAnswer2, R.id.etAnswer3, R.id.etAnswer4)
            val radioIds = listOf(R.id.rbCorrect1, R.id.rbCorrect2, R.id.rbCorrect3, R.id.rbCorrect4)

            val answers = answerFields.mapIndexed { i, fieldId ->
                Answer(
                    answerId = UUID.randomUUID().toString(),
                    answerText = view.findViewById<EditText>(fieldId).text.toString(),
                    isCorrect = view.findViewById<RadioButton>(radioIds[i]).isChecked
                )
            }

            val question = Question(
                questionId = (arguments?.getSerializable("question") as? Question)?.questionId
                    ?: UUID.randomUUID().toString(),
                quizId = "",   // filled in by CreateQuizFragment when saving
                questionText = questionText,
                timerSeconds = 30,
                answers = answers
            )

            // Send result back to CreateQuizFragment via SavedStateHandle
            findNavController()
                .previousBackStackEntry
                ?.savedStateHandle
                ?.set("result_question", question)
            findNavController().popBackStack()
        }
    }

    private fun setupSingleSelect(view: View) {
        val radioIds = listOf(R.id.rbCorrect1, R.id.rbCorrect2, R.id.rbCorrect3, R.id.rbCorrect4)
        val radios = radioIds.map { view.findViewById<RadioButton>(it) }
        radios.forEach { rb ->
            rb.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) radios.filter { it != rb }.forEach { it.isChecked = false }
            }
        }
    }
}