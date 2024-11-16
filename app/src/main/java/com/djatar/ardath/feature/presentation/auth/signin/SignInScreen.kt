package com.djatar.ardath.feature.presentation.auth.signin

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.djatar.ardath.R
import com.djatar.ardath.core.presentation.components.ErrorDialog
import com.djatar.ardath.core.presentation.components.LoadingDialog
import com.djatar.ardath.feature.presentation.auth.AuthState
import com.djatar.ardath.feature.presentation.auth.AuthViewModel
import com.djatar.ardath.feature.presentation.auth.components.AuthError.getErrorText
import com.djatar.ardath.feature.presentation.auth.components.AuthInputField
import com.djatar.ardath.feature.presentation.auth.components.AuthPageTitle

private const val TAG = "LoginPage"

@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    paddingValues: PaddingValues,
    onNavigateToRegisterScreen: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthPageTitle(stringResource(R.string.login_to_pedat_community))
            Spacer(modifier = Modifier.height(30.dp))
            AuthInputField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.label_email),
                keyboardType = KeyboardType.Email
            )
            AuthInputField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(R.string.label_password),
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                onClick = { viewModel.signInUser(email, password) },
            ) {
                Text(text = stringResource(R.string.login))
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
                    onNavigateToRegisterScreen()
                }
            ) {
                Text(text = stringResource(R.string.create_new_account))
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = paddingValues.calculateBottomPadding() + 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.created_by),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Light
            )
            Text(
                text = stringResource(R.string.author),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraLight
            )
        }

        when (state) {
            is AuthState.Loading -> {
                LoadingDialog(
                    title = stringResource(R.string.process_your_login),
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