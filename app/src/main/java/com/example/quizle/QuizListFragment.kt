package com.example.quizle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizle.persistence.QuizDatabase
import com.example.quizle.persistence.QuizRepositoryImpl

class QuizListFragment : Fragment() {

    private lateinit var repo: QuizRepositoryImpl

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_quiz_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = QuizDatabase.getInstance(requireContext())
        repo = QuizRepositoryImpl(db.quizDao())

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerViewQuizzes)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val quizzes = repo.loadAllQuizzes().toMutableList()
        recycler.adapter = QuizAdapter(quizzes,
            onHost = { quiz ->
                val bundle = Bundle().apply { putString("quizId", quiz.quizId) }
                findNavController().navigate(R.id.action_quizList_to_hostLobby, bundle)
            },
            onDelete = { quiz ->
                repo.removeQuiz(quiz)
                quizzes.remove(quiz)
                recycler.adapter?.notifyDataSetChanged()
            }
        )

        view.findViewById<Button>(R.id.btnCreateNew).setOnClickListener {
            findNavController().navigate(R.id.action_quizList_to_createQuiz)
        }
    }
}