package com.example.chat_gpt.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.chat_gpt.R
import com.example.chat_gpt.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        binding = FragmentMainBinding.inflate(layoutInflater)

        binding.chatWithBot.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
        }

        binding.generateImage.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_imageGenerationFragment)
        }
        // Inflate the layout for this fragment
        return binding.root
    }

}