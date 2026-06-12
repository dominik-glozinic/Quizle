package com.example.quizle.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizle.R
import com.example.quizle.logic.Question
import com.example.quizle.logic.Quiz
import com.example.quizle.persistence.QuizDatabase
import com.example.quizle.persistence.QuizRepositoryImpl
import java.util.UUID

class CreateQuizFragment : Fragment() {

    private val questions = mutableListOf<Question>()
    private lateinit var adapter: QuestionAdapter
    // Note: If editing an existing quiz, its ID is passed as an argument
    private var editingQuizId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_create_quiz, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = QuizDatabase.getInstance(requireContext())
        val repo = QuizRepositoryImpl(db.quizDao())
        
        editingQuizId = arguments?.getString("quizId")

        editingQuizId?.let { id ->
            repo.loadQuiz(id).let { quiz ->
                view.findViewById<EditText>(R.id.etQuizTitle).setText(quiz.title)
                questions.clear()
                questions.addAll(quiz.getQuestions())
                
                val btnDeleteQuiz = view.findViewById<Button>(R.id.btnDeleteQuiz)
                btnDeleteQuiz.visibility = View.VISIBLE
                btnDeleteQuiz.setOnClickListener {
                    repo.removeQuiz(quiz)
                    findNavController().popBackStack()
                }
            }
        }

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerViewQuestions)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = QuestionAdapter(questions,
            onClick = { question ->
                // Navigate to edit screen, passing question data
                val bundle = Bundle().apply { putSerializable("question", question) }
                findNavController().navigate(R.id.action_createQuiz_to_addQuestion, bundle)
            },
            onDelete = { question ->
                questions.remove(question)
                adapter.notifyDataSetChanged()
            }
        )
        recycler.adapter = adapter


        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Question>("result_question")
            ?.observe(viewLifecycleOwner) { q ->
                val idx = questions.indexOfFirst { it.questionId == q.questionId }
                if (idx >= 0) questions[idx] = q else questions.add(q)
                adapter.notifyDataSetChanged()
            }

        view.findViewById<Button>(R.id.btnAddQuestion).setOnClickListener {
            findNavController().navigate(R.id.action_createQuiz_to_addQuestion)
        }

        view.findViewById<Button>(R.id.btnAbort).setOnClickListener {
            findNavController().popBackStack()
        }

        view.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val title = view.findViewById<EditText>(R.id.etQuizTitle).text.toString().trim()
            if (title.isEmpty()) { /* show error */ return@setOnClickListener }
            val quiz = Quiz(quizId = editingQuizId ?: UUID.randomUUID().toString(), title = title)
            questions.forEach { quiz.addQuestion(it) }
            val dbSave = QuizDatabase.getInstance(requireContext())
            QuizRepositoryImpl(dbSave.quizDao()).saveQuiz(quiz)
            findNavController().popBackStack(R.id.quizListFragment, false)
        }
    }
}