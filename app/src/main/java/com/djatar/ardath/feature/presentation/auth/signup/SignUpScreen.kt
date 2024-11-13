package com.djatar.ardath.feature.presentation.auth.signup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.djatar.ardath.R
import com.djatar.ardath.core.presentation.components.ErrorDialog
import com.djatar.ardath.core.presentation.components.LoadingDialog
import com.djatar.ardath.feature.presentation.auth.AuthState
import com.djatar.ardath.feature.presentation.auth.AuthViewModel
import com.djatar.ardath.feature.presentation.auth.components.AuthError
import com.djatar.ardath.feature.presentation.auth.components.AuthError.getErrorText
import com.djatar.ardath.feature.presentation.auth.components.AuthInputField
import com.djatar.ardath.feature.presentation.auth.components.AuthPageTitle

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    paddingValues: PaddingValues,
    onNavigateToLoginScreen: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    BackHandler {
        viewModel.clearState()
        onNavigateToLoginScreen()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthPageTitle(stringResource(R.string.create_new_account))
            Spacer(modifier = Modifier.height(30.dp))
            AuthInputField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(R.string.name),
                keyboardType = KeyboardType.Text,
            )
            AuthInputField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.label_email),
                keyboardType = KeyboardType.Email,
            )
            AuthInputField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(R.string.label_password),
                keyboardType = KeyboardType.Password,
                isError = password != confirmPassword
            )
            AuthInputField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = stringResource(R.string.confirm_password),
                keyboardType = KeyboardType.Password,
                isError = password != confirmPassword,
                supportingText = if (password != confirmPassword) ({
                    Text(AuthError.CONFIRM_PASSWORD.getErrorText())
                }) else null
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                onClick = { viewModel.signUpUser(name, email, password, confirmPassword) },
            ) {
                Text(text = stringResource(R.string.register))
            }
            Text(
                text = stringResource(R.string.or),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 10.dp)
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                onClick = {
                    viewModel.clearState()
                    onNavigateToLoginScreen()
                }
            ) {
                Text(text = stringResource(R.string.login))
            }
        }

        when (state) {
            is AuthState.Loading -> {
                LoadingDialog(
                    title = stringResource(R.string.creating_account),
                    visible = state == AuthState.Loading
                )
            }
            is AuthState.Error, is AuthState.ErrorCode -> {
                val error = (state as? AuthState.Error)?.error
                    ?: (state as AuthState.ErrorCode).code.getErrorText()
                ErrorDialog(errorMessage = error) { viewModel.clearState() }
            }
            else -> {}
        }
    }
}