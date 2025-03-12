package br.ufes.inf.cityguiago.android.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.ufes.inf.cityguiago.android.R
import br.ufes.inf.cityguiago.android.ui.components.CityButton
import br.ufes.inf.cityguiago.android.ui.components.CityTextField
import br.ufes.inf.cityguiago.android.ui.components.CityTextButton
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.Success -> {
                    isLoading = false
                    onLoginSuccess()
                }
                is LoginEvent.Error -> {
                    isLoading = false
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
                is LoginEvent.Loading -> {
                    isLoading = true
                }
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(R.string.login),
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            CityTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.email),
                keyboardType = KeyboardType.Email
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CityTextField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(R.string.password),
                isPassword = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            CityButton(
                text = stringResource(R.string.login),
                onClick = { viewModel.login(email, password) },
                isLoading = isLoading,
                enabled = email.isNotEmpty() && password.isNotEmpty()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CityTextButton(
                text = stringResource(R.string.dont_have_account),
                onClick = onNavigateToSignup
            )
        }
    }
} 