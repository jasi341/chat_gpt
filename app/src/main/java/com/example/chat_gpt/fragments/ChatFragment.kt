package com.example.chat_gpt.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat_gpt.R
import com.example.chat_gpt.adapter.MessageAdapter
import com.example.chat_gpt.api.ApiUtils
import com.example.chat_gpt.databinding.FragmentChatBinding
import com.example.chat_gpt.models.MessageModel
import com.example.chat_gpt.models.request.ChatRequest
import com.example.chat_gpt.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private var list = ArrayList<MessageModel>()
    private lateinit var mLayoutManager : LinearLayoutManager
    private lateinit var mAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater)

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_mainFragment)
        }

        mLayoutManager = LinearLayoutManager(requireContext())
        mAdapter = MessageAdapter(list)
        binding.recyclerView.adapter = mAdapter
        mLayoutManager.stackFromEnd = true
        binding.recyclerView.layoutManager = mLayoutManager


        binding.sendBtn.setOnClickListener {
            if (binding.userMsg.text.trim().isEmpty()){
                Toast.makeText(requireContext(), "Enter your question!!", Toast.LENGTH_SHORT).show()
            }else{
                callApi()
            }
        }
        return binding.root
    }

    private fun callApi() {

        list.add(
            MessageModel(
                isUser = true,
                isImage = false,
                message = binding.userMsg.text.toString()
            )
        )
        mAdapter.notifyItemInserted(list.size-1)

        binding.recyclerView.apply {
            recycledViewPool.clear()
            smoothScrollToPosition(list.size-1)
        }

        val apiInterface = ApiUtils.getApiInterface()
        val reqBody = RequestBody.create(
            MediaType.parse("application/json"),
            Gson().toJson(
                ChatRequest(
                    max_tokens = 250,
                    model = "text-davinci-003",
                    prompt = binding.userMsg.text.toString(),
                    temperature = 0.7
                )
            )
        )
        val contentType = "application/json"
        val authorization = "Bearer ${Utils.API_KEY}"

        lifecycleScope.launch(Dispatchers.IO){

            try {
                val response = apiInterface.getChat(
                    contentType = contentType,
                    authorization = authorization,
                    requestBody = reqBody
                )

                val textResponse = response.choices.first().text

                list.add(
                    MessageModel(
                        isUser = false,
                        isImage = false,
                        message = textResponse
                    )
                )
                withContext(Dispatchers.Main){
                    mAdapter.notifyItemInserted(list.size-1)
                    binding.recyclerView.apply {
                        recycledViewPool.clear()
                        smoothScrollToPosition(list.size-1)
                    }

                }
                clearTextField()


            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()

                }

            }


        }





    }

    private fun clearTextField() {
        binding.userMsg.text.clear()
        val inputMethodManager = requireContext().getSystemService(InputMethodManager::class.java)
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}