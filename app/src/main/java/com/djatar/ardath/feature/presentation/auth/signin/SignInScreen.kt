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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.djatar.ardath.R
import com.djatar.ardath.core.presentation.components.ErrorDialog
import com.djatar.ardath.core.presentation.components.LoadingDialog
import com.djatar.ardath.feature.presentation.auth.components.AuthInputField
import com.djatar.ardath.feature.presentation.auth.components.AuthPageTitle
import com.google.firebase.auth.FirebaseUser

private const val TAG = "LoginPage"

@Composable
fun SignInScreen(
    paddingValues: PaddingValues,
    onNavigateToRegisterScreen: () -> Unit = {},
    onSignInSuccess: (user: FirebaseUser) -> Unit
) {

    val viewModel = hiltViewModel<SignInViewModel>()
    val uiState = viewModel.state.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState.value) {
        when (uiState.value) {
            is SignInState.Success -> onSignInSuccess((uiState.value as SignInState.Success).user)
            else -> {}
        }
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
                enabled = email.isNotEmpty() && password.isNotEmpty(),
                onClick = { viewModel.signIn(email, password) },
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
                onClick = { onNavigateToRegisterScreen() },
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

        LoadingDialog(
            title = stringResource(R.string.process_your_login),
            visible = uiState.value == SignInState.Loading
        )

        ErrorDialog(
            title = stringResource(R.string.login_error),
            errorMessage = stringResource(R.string.login_error),
            visible = uiState.value == SignInState.Error
        ) { viewModel.clearState() }
    }

}