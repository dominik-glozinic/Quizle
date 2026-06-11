package com.example.quizle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizle.logic.Leaderboard
import com.example.quizle.logic.LeaderboardEntry

class LeaderboardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rvLeaderboard)
        rv.layoutManager = LinearLayoutManager(requireContext())
        
        // Mocking leaderboard data for now
        val mockData = listOf(
            LeaderboardEntry("1", "Domy", 100),
            LeaderboardEntry("2", "Player2", 80),
            LeaderboardEntry("3", "Player3", 50)
        )
        rv.adapter = LeaderboardAdapter(mockData)

        view.findViewById<Button>(R.id.btnDone).setOnClickListener {
            findNavController().navigate(R.id.action_leaderboard_to_startScreen)
        }
    }

    class LeaderboardAdapter(private val entries: List<LeaderboardEntry>) : RecyclerView.Adapter<LeaderboardAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvName: TextView = v.findViewById(android.R.id.text1)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return VH(v)
        }
        override fun onBindViewHolder(holder: VH, position: Int) {
            val entry = entries[position]
            holder.tvName.text = "${position + 1}. ${entry.username} - ${entry.score} pts"
        }
        override fun getItemCount() = entries.size
    }
}
