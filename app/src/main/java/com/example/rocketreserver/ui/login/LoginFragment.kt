package com.example.rocketreserver.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rocketreserver.LoginMutation
import com.example.rocketreserver.R
import com.example.rocketreserver.core.Resource
import com.example.rocketreserver.core.User
import com.example.rocketreserver.databinding.LoginFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: LoginFragmentBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                when (it) {
                    is Resource.Loading -> handleLoadingVisibility(true)
                    is Resource.Success -> onSuccess(it.data)
                    is Resource.Error -> onError(it.error)
                }
            }
        }

        binding.submit.setOnClickListener { viewModel.login(email = binding.email.text.toString()) }
    }

    private fun onSuccess(login: LoginMutation.Login) {
        handleLoadingVisibility(false)
        login.token?.let {
            User.setToken(requireContext(), it)
            findNavController().popBackStack()
        }
    }

    private fun onError(error: Exception) {
        handleLoadingVisibility(false)
        when (error) {
            is EmailInvalidException -> binding.emailLayout.error = getString(R.string.required)
            else -> {
                // TODO
            }
        }
    }


    private fun handleLoadingVisibility(isLoading: Boolean) {
        binding.isLoading = isLoading
    }
}
