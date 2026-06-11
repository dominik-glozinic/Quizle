package com.example.quizle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizle.network.PlayerHttpClient
import kotlin.concurrent.thread

class UsernameFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_username, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val hostUrl = arguments?.getString("hostUrl") ?: return
        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        
        view.findViewById<Button>(R.id.btnJoinGame).setOnClickListener {
            val username = etUsername.text.toString().trim()
            if (username.isNotEmpty()) {
                joinGame(hostUrl, username)
            }
        }
    }

    private fun joinGame(hostInput: String, username: String) {
        // Networking should be off the main thread
        thread {
            try {
                // Sanitize input: Remove protocol if present, then rebuild properly
                var cleanHost = hostInput.removePrefix("http://").removePrefix("https://").trimEnd('/')
                
                // If the user didn't provide a port, append the default 8888
                val finalUrl = if (!cleanHost.contains(":")) {
                    "http://$cleanHost:8888"
                } else {
                    "http://$cleanHost"
                }

                val client = PlayerHttpClient(serverUrl = finalUrl)
                val result = client.join(url = finalUrl, username = username)
                
                activity?.runOnUiThread {
                    if (result.isSuccess) {
                        val bundle = Bundle().apply {
                            putString("baseUrl", finalUrl)
                            putString("username", username)
                            putString("playerId", result.getOrThrow().userId)
                        }
                        findNavController().navigate(R.id.action_username_to_playerGame, bundle)
                    } else {
                        Toast.makeText(context, "Failed to join: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
