package com.example.chat_gpt.fragments

import android.content.Context
import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat_gpt.R
import com.example.chat_gpt.adapter.MessageAdapter
import com.example.chat_gpt.api.ApiUtils
import com.example.chat_gpt.databinding.FragmentImageGenerationBinding
import com.example.chat_gpt.models.MessageModel
import com.example.chat_gpt.models.request.ChatRequest
import com.example.chat_gpt.models.request.ImageGenerationRequest
import com.example.chat_gpt.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody

class ImageGenerationFragment : Fragment() {

    private lateinit var binding : FragmentImageGenerationBinding

    private var list = ArrayList<MessageModel>()
    private lateinit var mLayoutManager : LinearLayoutManager
    private lateinit var mAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentImageGenerationBinding.inflate(layoutInflater)

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_imageGenerationFragment_to_mainFragment)
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
                ImageGenerationRequest(
                    n = 2,
                    prompt = binding.userMsg.text.toString(),
                    size = "1024x1024"

                )
            )
        )
        val contentType = "application/json"
        val authorization = "Bearer ${Utils.API_KEY}"

        lifecycleScope.launch(Dispatchers.IO){

            try {
                val response = apiInterface.generateImages(
                    contentType = contentType,
                    authorization = authorization,
                    requestBody = reqBody
                )

                val textResponse = response.data.first().url

                list.add(
                    MessageModel(
                        isUser = false,
                        isImage = true,
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
                clear()


            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(), "Something went wrong !!", Toast.LENGTH_SHORT).show()

                }

            }
        }
    }

    private fun clear() {
        binding.userMsg.text.clear()
        val inputMethodManager = requireContext().getSystemService(InputMethodManager::class.java)
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}