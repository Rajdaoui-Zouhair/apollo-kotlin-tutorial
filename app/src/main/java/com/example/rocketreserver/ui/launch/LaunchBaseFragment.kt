package com.example.rocketreserver.ui.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.rocketreserver.core.Resource
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

abstract class LaunchBaseFragment<Binding : ViewDataBinding, ViewModel : androidx.lifecycle.ViewModel, R>(
    @LayoutRes val layoutRes: Int
) : Fragment() {
    private var _binding: Binding? = null
    protected val binding: Binding get() = _binding!!

    protected lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[getViewModelClass()]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            getResource().collectLatest {
                when (it) {
                    is Resource.Loading -> handleLoadingVisibility(true)
                    is Resource.Success -> onSuccess(it.data)
                    is Resource.Error -> onError(it.error)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    abstract fun getViewModelClass(): Class<ViewModel>
    abstract fun getResource(): StateFlow<Resource<R>>
    abstract fun handleLoadingVisibility(isLoading: Boolean)
    abstract fun onSuccess(resource: R)
    abstract fun onError(error: Exception)
    abstract fun handleErrorVisibility(isLoading: Boolean)
}