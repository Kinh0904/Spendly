package com.example.spendly.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.spendly.MainActivity
import com.example.spendly.R
import com.example.spendly.data.auth.AuthRepository
import com.example.spendly.data.auth.AuthResult
import com.example.spendly.databinding.FragmentLoginBinding
import com.example.spendly.domain.auth.AuthValidator
import com.example.spendly.presentation.auth.messageRes
import com.example.spendly.presentation.auth.register.RegisterFragment
import com.example.spendly.presentation.home.HomeFragment

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authRepository = AuthRepository.getInstance(requireContext())

        showArguments(savedInstanceState)

        binding.etIdentifier.doAfterTextChanged {
            binding.tilIdentifier.error = null
            hideFormError()
        }
        binding.etPassword.doAfterTextChanged {
            binding.tilPassword.error = null
            hideFormError()
        }
        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin()
                true
            } else {
                false
            }
        }
        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.tvGoRegister.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(RegisterFragment())
        }
    }

    /** Điền sẵn email và hiện thông báo thành công khi vừa đăng ký xong. */
    private fun showArguments(savedInstanceState: Bundle?) {
        val args = arguments ?: return
        if (savedInstanceState == null) {
            args.getString(ARG_IDENTIFIER)?.let { binding.etIdentifier.setText(it) }
            if (args.getBoolean(ARG_JUST_REGISTERED)) {
                binding.tvNotice.visibility = View.VISIBLE
                binding.etPassword.requestFocus()
            }
        }
    }

    private fun attemptLogin() {
        val identifier = binding.etIdentifier.text?.toString().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()

        binding.tvNotice.visibility = View.GONE
        hideFormError()

        val identifierError = AuthValidator.validateIdentifier(identifier)
        val passwordEmpty = password.isEmpty()
        binding.tilIdentifier.error = identifierError?.let { getString(it.messageRes()) }
        binding.tilPassword.error =
            if (passwordEmpty) getString(R.string.auth_error_password_empty) else null
        if (identifierError != null) {
            binding.etIdentifier.requestFocus()
            return
        }
        if (passwordEmpty) {
            binding.etPassword.requestFocus()
            return
        }

        when (val result = authRepository.login(identifier, password, binding.cbRemember.isChecked)) {
            is AuthResult.Success -> {
                hideKeyboard()
                (activity as? MainActivity)?.navigateToFragment(HomeFragment())
            }
            is AuthResult.Failure -> showFormError(getString(result.error.messageRes()))
        }
    }

    private fun showFormError(message: String) {
        binding.tvFormError.text = message
        binding.tvFormError.visibility = View.VISIBLE
    }

    private fun hideFormError() {
        _binding?.tvFormError?.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val window = activity?.window ?: return
        WindowCompat.getInsetsController(window, binding.root).hide(WindowInsetsCompat.Type.ime())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IDENTIFIER = "arg_identifier"
        private const val ARG_JUST_REGISTERED = "arg_just_registered"

        /** Mở màn hình đăng nhập sau khi đăng ký, điền sẵn email vừa tạo. */
        fun afterRegistration(email: String): LoginFragment = LoginFragment().apply {
            arguments = bundleOf(ARG_IDENTIFIER to email, ARG_JUST_REGISTERED to true)
        }
    }
}
