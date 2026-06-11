package com.example.quizle

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizle.logic.GameSession
import com.example.quizle.logic.Host
import com.example.quizle.network.LocalHttpServer
import com.example.quizle.persistence.QuizDatabase
import com.example.quizle.persistence.QuizRepositoryImpl
import java.net.NetworkInterface
import java.util.Collections

class HostLobbyFragment : Fragment() {

    private var session: GameSession? = null
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var playerAdapter: PlayerNameAdapter
    private val playerNames = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_host_lobby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val quizId = arguments?.getString("quizId") ?: return
        
        val db = QuizDatabase.getInstance(requireContext())
        val repo = QuizRepositoryImpl(db.quizDao())
        val quiz = repo.loadQuiz(quizId)
        
        val mainActivity = activity as? MainActivity
        val server = mainActivity?.server ?: LocalHttpServer(port = 8888).also { mainActivity?.server = it }
        val host = Host(username = "Host", network = server)
        
        val hostIp = getLocalIpAddress() ?: "127.0.0.1"

        session = host.createSession(quiz, hostIp = hostIp, port = 8888)
        mainActivity?.currentSession = session
        
        view.findViewById<TextView>(R.id.tvJoinUrl).text = session?.getJoinUrl()
        
        val rvPlayers = view.findViewById<RecyclerView>(R.id.rvPlayers)
        rvPlayers.layoutManager = LinearLayoutManager(requireContext())
        playerAdapter = PlayerNameAdapter(playerNames)
        rvPlayers.adapter = playerAdapter
        
        view.findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            findNavController().navigate(R.id.action_hostLobby_to_hostGame)
        }
        
        startPollingPlayers()
    }

    private fun getLocalIpAddress(): String? {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress ?: continue
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (isIPv4) return sAddr
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    private fun startPollingPlayers() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val currentPlayers = session?.getPlayers()?.map { it.getUsername() } ?: emptyList()
                if (currentPlayers != playerNames) {
                    playerNames.clear()
                    playerNames.addAll(currentPlayers)
                    playerAdapter.notifyDataSetChanged()
                }
                handler.postDelayed(this, 2000)
            }
        }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    class PlayerNameAdapter(private val names: List<String>) : RecyclerView.Adapter<PlayerNameAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvName: TextView = v.findViewById(android.R.id.text1)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return VH(v)
        }
        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.tvName.text = names[position]
        }
        override fun getItemCount() = names.size
    }
}
